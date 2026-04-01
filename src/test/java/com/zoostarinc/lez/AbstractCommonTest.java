package com.zoostarinc.lez;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractCommonTest {

	@Autowired
	protected MockMvc client;

	@Autowired
	protected ObjectMapper om;

}
