package br.ce.wcaquino.tasks.apitest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class APITest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8001/tasks-backend";
    }

    @Test
    public void deveRetornarTarefas() {
        given()
        .when()
            .get("/todo")
        .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    public void deveAdicionarTarefaComSucesso() {
        String dueDate = LocalDate.now().plusDays(30).toString();

        given()
            .body(String.format("{ \"task\": \"Teste via API\", \"dueDate\": \"%s\" }", dueDate))
            .contentType(ContentType.JSON)
        .when()
            .post("/todo")
        .then()
            .log().all()
            .statusCode(201)
            .body("task", is("Teste via API"));
    }

    @Test
    public void naoDeveAdicionarTarefaInvalida() {
        String dueDate = LocalDate.now().minusDays(1).toString();

        given()
            .body(String.format("{ \"task\": \"Teste via API\", \"dueDate\": \"%s\" }", dueDate))
            .contentType(ContentType.JSON)
        .when()
            .post("/todo")
        .then()
            .log().all()
            .statusCode(400)
            .body("message", is("Due date must not be in past"));
    }
}
