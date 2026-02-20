package com.zoostarinc.lez.web.controller;

import java.security.GeneralSecurityException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.common.web.error.handler.CommonExceptionHandler;
import net.zoostar.common.web.response.FailureResponseEntity;
import net.zoostar.common.web.response.SystemFailureResponseEntity;

@Slf4j
@ControllerAdvice
public class LezExceptionHandler extends CommonExceptionHandler {

	@ExceptionHandler(GeneralSecurityException.class)
	protected FailureResponseEntity handleException(Exception e) {
		log.error(e.getMessage(), e);
		return new SystemFailureResponseEntity(e.getMessage());
	}

}
