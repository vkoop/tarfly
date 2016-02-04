package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppStarter {

	public static void main(String[] args) {
		SpringApplication.run(AppStarter.class, args);
	}

	@Autowired
	ApplicationContext context;

	@Autowired
	@Bean
	public CommandLineRunner getRunner(ApplicationArguments applicationArguments){
		return (args)->{
			if(applicationArguments.containsOption("server")){
				context.getBean(Server.class).start();
			} else if(applicationArguments.containsOption("client")){
				context.getBean(Client.class).start();
			}
		};
	}

}
