package com.nexign.test.cdr_service;

import com.nexign.test.cdr_service.controller.CdrController;
import com.nexign.test.cdr_service.service.UdrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CdrServiceApplicationTests {

	@Autowired
	private CdrController cdrController;

	@Autowired
	private UdrService udrService;

	@Test
	void contextLoads() {
		assertThat(cdrController).isNotNull();
		assertThat(udrService).isNotNull();
	}
}