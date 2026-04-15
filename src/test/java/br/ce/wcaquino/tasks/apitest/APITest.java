package br.ce.wcaquino.tasks.apitest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class APITest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8001/tasks-backend";
        Assume.assumeTrue("tasks-backend indisponivel para os testes de API.", endpointDisponivel("/todo"));
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

    @Test
    public void deveRemoverTarefaComSucesso() {
        String taskName = "Remover via API " + System.currentTimeMillis();
        String dueDate = LocalDate.now().plusDays(30).toString();

        Integer taskId =
            RestAssured.given()
                .body(String.format("{ \"task\": \"%s\", \"dueDate\": \"%s\" }", taskName, dueDate))
                .contentType(ContentType.JSON)
            .when()
                .post("/todo")
            .then()
                .log().all()
                .statusCode(201)
                .body("task", is(taskName))
                .extract().path("id");

        RestAssured.given()
        .when()
            .delete("/todo/" + taskId)
        .then()
            .log().all()
            .statusCode(204);
    }

    private static boolean endpointDisponivel(String path) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(RestAssured.baseURI + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
