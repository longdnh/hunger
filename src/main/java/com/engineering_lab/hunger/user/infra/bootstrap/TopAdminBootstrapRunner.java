package com.engineering_lab.hunger.user.infra.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.engineering_lab.hunger.user.application.UserService;
import com.engineering_lab.hunger.user.application.result.UserResult;
import com.engineering_lab.hunger.user.infra.config.TopAdminBootstrapProperties;

@Component
public class TopAdminBootstrapRunner
                implements ApplicationRunner {

        private static final Logger LOG = LoggerFactory.getLogger(
                        TopAdminBootstrapRunner.class);

        private final TopAdminBootstrapProperties properties;
        private final UserService userService;

        public TopAdminBootstrapRunner(
                        TopAdminBootstrapProperties properties,
                        UserService userService) {
                this.properties = properties;
                this.userService = userService;
        }

        @Override
        public void run(ApplicationArguments arguments) {
                if (!properties.enabled()) {
                        return;
                }

                UserResult topAdmin = userService.bootstrapTopAdmin(
                                properties.name(),
                                properties.email(),
                                properties.password());

                LOG.info(
                                "Top-admin bootstrap completed: userId={}",
                                topAdmin.userId());
        }
}
