package com.mx.cbtis.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;
import com.sun.net.httpserver.HttpServer;

class GestorControllerCoverageTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	private static HttpServer mockBackendServer;

	@BeforeAll
	static void startMockBackend() throws Exception {
		mockBackendServer = HttpServer.create(new InetSocketAddress(8081), 0);
		mockBackendServer.createContext("/", exchange -> {
			String path = exchange.getRequestURI().getPath();
			byte[] responseBody;

			if (path.contains("buscarProyectos")) {
				responseBody = "[{\"id_proyecto\":1,\"nombre\":\"Proyecto Test\"}]".getBytes(StandardCharsets.UTF_8);
			} else if (path.contains("buscarProyecto")) {
				responseBody = "{\"id_proyecto\":1,\"nombre\":\"Proyecto Test\"}".getBytes(StandardCharsets.UTF_8);
			} else if (path.contains("buscarUsuarios")) {
				responseBody = "[{\"id_usuario\":1,\"nombre\":\"Usuario Test\"}]".getBytes(StandardCharsets.UTF_8);
			} else if (path.contains("getUsuarioLogin")) {
				responseBody = "{\"id_usuario\":1,\"nombre\":\"Usuario Test\"}".getBytes(StandardCharsets.UTF_8);
			} else if (path.contains("getTarea") || path.contains("buscarTareas")) {
				responseBody = "[{\"id_tarea\":1,\"nombre\":\"Tarea Test\"}]".getBytes(StandardCharsets.UTF_8);
			} else if (path.contains("buscarTarea")) {
				responseBody = "{\"id_tarea\":1,\"nombre\":\"Tarea Test\"}".getBytes(StandardCharsets.UTF_8);
			} else if (path.contains("getComentario") || path.contains("buscarComentarios")) {
				responseBody = "[{\"id_comentario\":1,\"texto_comentario\":\"Comentario Test\"}]".getBytes(StandardCharsets.UTF_8);
			} else if (path.contains("add") || path.contains("delete") || path.contains("update")) {
				responseBody = "1".getBytes(StandardCharsets.UTF_8);
			} else {
				responseBody = "{}".getBytes(StandardCharsets.UTF_8);
			}

			exchange.getResponseHeaders().set("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, responseBody.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(responseBody);
			}
		});
		mockBackendServer.start();
	}

	@AfterAll
	static void stopMockBackend() {
		if (mockBackendServer != null) {
			mockBackendServer.stop(0);
		}
	}

	@BeforeEach
	void setUp() {
		org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver = new org.springframework.web.servlet.view.InternalResourceViewResolver();
		viewResolver.setPrefix("/templates/");
		viewResolver.setSuffix(".html");

		mockMvc = MockMvcBuilders.standaloneSetup(new GestorController())
				.setViewResolvers(viewResolver)
				.build();
		objectMapper = new ObjectMapper();
	}

	// ----------------- VISTAS BÁSICAS -----------------

	@Test
	@DisplayName("GET / -> home view")
	void testGetHome() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("home"));
	}

	@Test
	@DisplayName("GET /tutorial -> tutorial view")
	void testGetTutorial() throws Exception {
		mockMvc.perform(get("/tutorial"))
				.andExpect(status().isOk())
				.andExpect(view().name("tutorial"));
	}

	@Test
	@DisplayName("GET /manual -> manual view")
	void testGetManual() throws Exception {
		mockMvc.perform(get("/manual"))
				.andExpect(status().isOk())
				.andExpect(view().name("manual"));
	}

	@Test
	@DisplayName("GET /login -> login view")
	void testGetLogin() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}

	@Test
	@DisplayName("GET /register -> register view con modelo")
	void testGetRegister() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("usuario"));
	}

	@Test
	@DisplayName("GET /inicio -> inicio view con modelo")
	void testGetInicio() throws Exception {
		mockMvc.perform(get("/inicio"))
				.andExpect(status().isOk())
				.andExpect(view().name("inicio"))
				.andExpect(model().attributeExists("proyectos"));
	}

	@Test
	@DisplayName("GET /proyecto -> proyecto view con modelo")
	void testGetProyecto() throws Exception {
		mockMvc.perform(get("/proyecto"))
				.andExpect(status().isOk())
				.andExpect(view().name("proyecto"))
				.andExpect(model().attributeExists("tarea"))
				.andExpect(model().attributeExists("comentarios"))
				.andExpect(model().attributeExists("listaUsuarios"));
	}

	@Test
	@DisplayName("GET /tarea -> proyecto view con modelo tareas")
	void testGetTarea() throws Exception {
		mockMvc.perform(get("/tarea"))
				.andExpect(status().isOk())
				.andExpect(view().name("proyecto"))
				.andExpect(model().attributeExists("tareas"));
	}

	// ----------------- USUARIO Y LOGIN -----------------

	@Test
	@DisplayName("POST /saveUsuario -> redirect:/login")
	void testSaveUsuario() throws Exception {
		mockMvc.perform(post("/saveUsuario")
				.param("nombre", "Santiago")
				.param("correo_electronico", "santiago@test.com")
				.param("contrasena", "1234"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/login"));
	}

	@Test
	@DisplayName("POST /verificarLogin -> responde usuario")
	void testVerificarLogin() throws Exception {
		Usuario u = new Usuario();
		u.setCorreo_electronico("santiago@test.com");
		u.setContrasena("1234");

		mockMvc.perform(post("/verificarLogin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(u)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_usuario", is(1)))
				.andExpect(jsonPath("$.nombre", is("Usuario Test")));
	}

	@Test
	@DisplayName("POST /consultarUsuarios -> responde lista de usuarios")
	void testConsultarUsuarios() throws Exception {
		mockMvc.perform(post("/consultarUsuarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nombre", is("Usuario Test")));
	}

	// ----------------- PROYECTOS -----------------

	@Test
	@DisplayName("POST /saveProyecto -> redirect:/inicio")
	void testSaveProyecto() throws Exception {
		mockMvc.perform(post("/saveProyecto")
				.param("nombre", "Proyecto X")
				.param("descripcion", "Desc X"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/inicio"));
	}

	@Test
	@DisplayName("POST /consultarProyectos -> lista de proyectos")
	void testConsultarProyectos() throws Exception {
		mockMvc.perform(post("/consultarProyectos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nombre", is("Proyecto Test")));
	}

	@Test
	@DisplayName("POST /eliminarProyecto -> redirect:/inicio")
	void testEliminarProyecto() throws Exception {
		Proyectos p = new Proyectos();
		p.setId_proyecto(1);

		mockMvc.perform(post("/eliminarProyecto")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(p)))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/inicio"));
	}

	@Test
	@DisplayName("POST /actualizarProyecto -> responde proyecto")
	void testActualizarProyecto() throws Exception {
		Proyectos p = new Proyectos();
		p.setId_proyecto(1);

		mockMvc.perform(post("/actualizarProyecto")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(p)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_proyecto", is(1)))
				.andExpect(jsonPath("$.nombre", is("Proyecto Test")));
	}

	// ----------------- TAREAS -----------------

	@Test
	@DisplayName("POST /saveTarea -> redirect:/proyecto con id_proyecto")
	void testSaveTarea() throws Exception {
		mockMvc.perform(post("/saveTarea")
				.param("nombre", "Tarea 1")
				.param("id_proyecto", "10"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/proyecto"))
				.andExpect(model().attribute("id_proyecto", 10));
	}

	@Test
	@DisplayName("POST /obtenerTareas -> lista de tareas")
	void testObtenerTareas() throws Exception {
		Tareas t = new Tareas();
		t.setId_proyecto(10);

		mockMvc.perform(post("/obtenerTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(t)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nombre", is("Tarea Test")));
	}

	@Test
	@DisplayName("POST /actualizarTarea -> responde tarea")
	void testActualizarTarea() throws Exception {
		Tareas t = new Tareas();
		t.setId_tarea(1);

		mockMvc.perform(post("/actualizarTarea")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(t)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_tarea", is(1)))
				.andExpect(jsonPath("$.nombre", is("Tarea Test")));
	}

	@Test
	@DisplayName("POST /eliminarTarea -> redirect:/proyecto")
	void testEliminarTarea() throws Exception {
		Tareas t = new Tareas();
		t.setId_tarea(1);

		mockMvc.perform(post("/eliminarTarea")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(t)))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/proyecto"));
	}

	@Test
	@DisplayName("POST /verificarTarea -> responde entero")
	void testVerificarTarea() throws Exception {
		Tareas t = new Tareas();
		t.setNombre("Tarea Verif");

		mockMvc.perform(post("/verificarTarea")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(t)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(1)));
	}

	@Test
	@DisplayName("POST /cambiosTareas -> responde entero")
	void testCambiosTareas() throws Exception {
		Tareas t = new Tareas();
		t.setNombre("Tarea Cambio");

		mockMvc.perform(post("/cambiosTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(t)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(1)));
	}

	@Test
	@DisplayName("POST /consultarTareas -> lista de tareas")
	void testConsultarTareas() throws Exception {
		mockMvc.perform(post("/consultarTareas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nombre", is("Tarea Test")));
	}

	// ----------------- COMENTARIOS -----------------

	@Test
	@DisplayName("POST /saveComentario -> vista proyecto")
	void testSaveComentario() throws Exception {
		Comentarios c = new Comentarios();
		c.setTexto_comentario("Hola mundo");

		mockMvc.perform(post("/saveComentario")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(c)))
				.andExpect(status().isOk())
				.andExpect(view().name("proyecto"));
	}

	@Test
	@DisplayName("POST /obtenerComentarios -> lista de comentarios")
	void testObtenerComentarios() throws Exception {
		Comentarios c = new Comentarios();
		c.setId_tarea(5);

		mockMvc.perform(post("/obtenerComentarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(c)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].texto_comentario", is("Comentario Test")));
	}

	@Test
	@DisplayName("POST /consultarComentarios -> lista de comentarios")
	void testConsultarComentarios() throws Exception {
		mockMvc.perform(post("/consultarComentarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].texto_comentario", is("Comentario Test")));
	}
}
