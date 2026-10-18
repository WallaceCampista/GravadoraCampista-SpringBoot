package com.gravadoracampista;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// O teste sobe a aplicação contra o banco de dev; sem isso o build populava o banco
@SpringBootTest(properties = "api.dados-exemplo=false")
class ApiGravadoraCampistaApplicationTests {

	@Test
	void contextLoads() {
	}

}
