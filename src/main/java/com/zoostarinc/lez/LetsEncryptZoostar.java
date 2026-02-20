package com.zoostarinc.lez;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.Generated;

@Generated
@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = { "net.zoostar", "com.zoostarinc" })
public class LetsEncryptZoostar extends SpringBootServletInitializer implements WebMvcConfigurer {

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		SpringApplication.run(LetsEncryptZoostar.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		Security.addProvider(new BouncyCastleProvider());
		return application.sources(LetsEncryptZoostar.class);
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry vcr) {
		vcr.addRedirectViewController("/", "/swagger-ui/index.html");
	}

}
