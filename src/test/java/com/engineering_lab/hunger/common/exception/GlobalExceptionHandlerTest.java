package com.engineering_lab.hunger.common.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private static final String TEST_ERROR_CODE = "TEST_RESOURCE_NOT_FOUND";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void appExceptionReturnsItsStatusCodeMessageAndRequestPath() throws Exception {
        mockMvc.perform(get("/test/app-exception"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value(TEST_ERROR_CODE))
                .andExpect(jsonPath("$.message").value("User was not found"))
                .andExpect(jsonPath("$.path").value("/test/app-exception"));
    }

    @Test
    void unexpectedExceptionReturnsSafeGenericResponse() throws Exception {
        mockMvc.perform(get("/test/unexpected-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(content().string(not(containsString("database-password"))))
                .andExpect(jsonPath("$.path").value("/test/unexpected-exception"));
    }

    @Test
    void malformedJsonUsesTheStandardApiErrorResponseDto() throws Exception {
        mockMvc.perform(post("/test/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request is invalid"))
                .andExpect(jsonPath("$.path").value("/test/json"));
    }

    @Test
    void unsupportedMethodKeepsTheAllowHeaderFromSpringMvc() throws Exception {
        mockMvc.perform(post("/test/get-only"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(HttpHeaders.ALLOW, "GET"))
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"))
                .andExpect(jsonPath("$.message").value("The HTTP method is not supported"))
                .andExpect(jsonPath("$.path").value("/test/get-only"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/app-exception")
        void appException() {
            throw new AppException(
                    HttpStatus.NOT_FOUND,
                    TEST_ERROR_CODE,
                    "User was not found");
        }

        @GetMapping("/unexpected-exception")
        void unexpectedException() {
            throw new IllegalStateException("database-password must not reach the client");
        }

        @PostMapping("/json")
        void readJson(@RequestBody TestRequest request) {
        }

        @GetMapping("/get-only")
        void getOnly() {
        }
    }

    record TestRequest(String name) {
    }
}
