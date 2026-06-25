package com.sava.teachernet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class TeachMeetsLearnApplicationTest {
	@MockitoBean
	private ClientRegistrationRepository clientRegistrationRepository;

	@Test
	void contextLoads() {
	}
}
