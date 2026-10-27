package com.mx.cbtis;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class Ejercicio5ClienteApplicationCoverageTest {

	@Test
	@DisplayName("Test constructor de Ejercicio5ClienteApplication")
	void testAppConstructor() {
		Ejercicio5ClienteApplication app = new Ejercicio5ClienteApplication();
		assertNotNull(app);
	}

	@Test
	@DisplayName("Test main method de Ejercicio5ClienteApplication")
	void testMainMethod() {
		try (MockedStatic<SpringApplication> mockedSpring = mockStatic(SpringApplication.class)) {
			mockedSpring.when(() -> SpringApplication.run(Ejercicio5ClienteApplication.class, new String[]{}))
					.thenReturn(null);
			Ejercicio5ClienteApplication.main(new String[]{});
			mockedSpring.verify(() -> SpringApplication.run(Ejercicio5ClienteApplication.class, new String[]{}));
		}
	}
}
