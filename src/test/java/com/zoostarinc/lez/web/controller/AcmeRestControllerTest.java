package com.zoostarinc.lez.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.zoostarinc.lez.AbstractCommonTest;

import net.zoostar.common.StringWrapper;

@SpringBootTest
@AutoConfigureMockMvc
class AcmeRestControllerTest extends AbstractCommonTest {

	@Test
	void testGenerateCSR200() throws Exception {
		// given
		String url = "/csr/generate?domainCommonName=junit";
		
		// then
		var response = client.perform(get(url).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		var value = om.readValue(response.getContentAsString(), StringWrapper.class);
		assertThat(value).isNotNull();
	}

	@Test
	void testGenerateCSRRequiredRequestParamMissing() throws Exception {
		// given
		String url = "/csr/generate?domainCommonName=";
		
		// then
		var response = client.perform(get(url).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void testGenerateCSRRequiredUnknownSecurityProvider() throws Exception {
		// given
		String url = "/csr/generate?domainCommonName=junit";
		
		// then
		var response = client.perform(get(url).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

}
