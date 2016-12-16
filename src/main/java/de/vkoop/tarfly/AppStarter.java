package de.vkoop.tarfly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Starter class
 */
@SpringBootApplication
public class AppStarter {

    public static void main(final String[] args) {
        SpringApplication.run(AppStarter.class, args);
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    @Bean
    public CommandLineRunner getRunner(final ApplicationArguments applicationArguments) {
        return (args) -> {
            if (applicationArguments.containsOption("server")) {
                context.getBean(Server.class).start();
            } else if (applicationArguments.containsOption("client")) {
                context.getBean(Client.class).start();
            }
        };
    }

}
