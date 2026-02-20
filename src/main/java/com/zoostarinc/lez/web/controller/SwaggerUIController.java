package com.zoostarinc.lez.web.controller;

import java.time.OffsetDateTime;
import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.zoostar.common.Utils;
import net.zoostar.common.audit.Timeable;

@Slf4j
@Timeable
@Controller
public class SwaggerUIController implements ApplicationContextAware, InitializingBean {

	@Value("${build.name}")
	protected String buildName;

	@Value("${build.timestamp}")
	protected String buildTimestamp;

	@Value("${build.version}")
	protected String buildVersion;

	protected ApplicationContext applicationContext;
	
	/**
	 * 
	 * @param name  the message to be displayed
	 * @param model
	 * @return greeting message
	 */
	@GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
	public String greet(@AuthenticationPrincipal DefaultOidcUser user, Model model, HttpSession session) {
		String greeting = "redirect:swagger-ui/index.html";
		log.debug("Loading greeting in env: {}",
				Arrays.toString(applicationContext.getEnvironment().getActiveProfiles()));
		log.debug("Session ID: {}", session.getId());

		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		log.info("Logged in Principal: {}", principal.toString());
		var name = user == null ? "Guest" : user.getGivenName();
		log.info("Hello {}!", name);

		model.addAttribute("name", name);
		model.addAttribute("currentTime", OffsetDateTime.now().format(Utils.ISO_DATE_TIME_FORMAT_UPTO_SECONDS));
		model.addAttribute("buildName", buildName);
		model.addAttribute("buildTimestamp", buildTimestamp);
		model.addAttribute("buildVersion", buildVersion);

		return greeting;
	}

	protected void determineLogLevelGranularity() {
		log.error("{}", "Logging at ERROR level");
		log.warn("{}", "Logging at WARN level");
		log.info("{}", "Logging at INFO level");
		log.debug("{}", "Logging at DEBUG level");
		log.trace("{}", "Logging at TRACE level");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		determineLogLevelGranularity();
	}

}
