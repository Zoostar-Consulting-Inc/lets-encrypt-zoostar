package com.zoostarinc.lez.web.controller;

import java.security.GeneralSecurityException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import net.zoostar.common.web.error.handler.CommonExceptionHandler;
import net.zoostar.common.web.response.FailureResponseEntity;
import net.zoostar.common.web.response.SystemFailureResponseEntity;

@ControllerAdvice
public class LezExceptionHandler extends CommonExceptionHandler {

	@ExceptionHandler(GeneralSecurityException.class)
	protected FailureResponseEntity handleException(Exception e) {
		return new SystemFailureResponseEntity(e.getMessage());
	}

}
