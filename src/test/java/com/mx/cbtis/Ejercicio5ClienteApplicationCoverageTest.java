package com.mx.cbtis;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Ejercicio5ClienteApplicationCoverageTest {

	@Test
	@DisplayName("Test constructor de Ejercicio5ClienteApplication")
	void testAppConstructor() {
		Ejercicio5ClienteApplication app = new Ejercicio5ClienteApplication();
		assertNotNull(app);
	}
}
