package com.zoostarinc.lez;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.Generated;

@Generated
@Configuration
public class ApplicationContext {

	@Value("${build.name}")
	private String buildName;

	@Value("${build.version}")
	private String buildVersion;

	@Value("${build.timestamp}")
	private String buildTimestamp;

	@Bean
	OpenAPI openAPI() {
		var version = new StringBuilder(buildVersion).append(".").append(buildName).append(".").append(buildTimestamp);
		return new OpenAPI().info(new Info().title("Zoostar Web Console").description("Starter Project with OAuth2 Authentication.")
				.version(version.toString()).contact(new Contact().name("zoostar").email("devops@zoostar.net")));
	}

}
