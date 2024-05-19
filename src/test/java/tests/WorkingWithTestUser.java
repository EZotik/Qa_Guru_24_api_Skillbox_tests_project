package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.CredentialConfig;
import models.ApiCreateUserModel;
import models.ApiUserResponseModel;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static specs.ApiSpecs.*;

@Tag("api_test")
@DisplayName("Тесты на операции с новым пользователем")
public class WorkingWithTestUser extends TestBase {
    CredentialConfig credentialConfig = ConfigFactory.create(CredentialConfig.class);

    @Test
    @DisplayName("Добавление нового пользователя в базу данных")
    void successfulCreateUserTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        ApiCreateUserModel authData = null;
        try {
            authData = objectMapper.readValue(new File("src/test/resources/files/user.json"), ApiCreateUserModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ApiCreateUserModel finalAuthData = authData;
        ApiUserResponseModel response = step("POST запрос на создание пользователя", () -> given(requestSpec)
                .body(finalAuthData)
                .when()
                .post("/user")
                .then()
                .spec(responseSpecReference)
                .extract().as(ApiUserResponseModel.class));

        step("Получение ответа с данными по code", () ->
                assertEquals("200", response.getCode()));
        step("Получение ответа с данными по type", () ->
                assertEquals("unknown", response.getType()));
        step("Получение ответа с данными по message", () ->
                assertEquals("2", response.getMessage()));
    }

    @Test
    @DisplayName("Получение информации по указанному Username")
    public void successfulGetUserInfoTest() {
        ApiCreateUserModel response = step("Get запрос на получение информации по  username", () -> given(getUserRequestSpec)
                .when()
                .get("/user/Мария")
                .then()
                .spec(responseSpecReference)
                .body(matchesJsonSchemaInClasspath("schemas/create-response-schema.json"))
                .extract().as(ApiCreateUserModel.class));
        step("Получение ответа с данными по Id", () ->
                assertEquals(2, response.getId()));
        step("Получение ответа с данными по username", () ->
                assertEquals("Мария", response.getUsername()));
        step("Получение ответа с данными по firstName", () ->
                assertEquals("Маёрова", response.getFirstName()));
        step("Получение ответа с данными по email", () ->
                assertEquals("123@mail.ru", response.getEmail()));
    }

    @Test
    @DisplayName("Изменение информации по ранее созданному пользователю")
    void successfulPutUpdateUserInfoTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        ApiCreateUserModel authData = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream("/files/user.json");
            if (inputStream != null) {
                authData = objectMapper.readValue(inputStream, ApiCreateUserModel.class);
                authData.setPassword("1234Vesna24");
                authData.setPhone("+74950044234");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ApiCreateUserModel finalAuthData = authData;
        ApiUserResponseModel response = step("PUT запрос на изменение данных пользователя", () -> given(requestSpec)
                .body(finalAuthData)
                .when()
                .put("/user/Мария")
                .then()
                .spec(responseSpecReference)
                .extract().as(ApiUserResponseModel.class));

        step("Получение ответа с данными по code", () ->
                assertEquals("200", response.getCode()));
        step("Получение ответа с данными по type", () ->
                assertEquals("unknown", response.getType()));
        step("Получение ответа с данными по message", () ->
                assertEquals("2", response.getMessage()));
    }

    @Test
    @DisplayName("Авторизация пользователя")
    public void successfulGetUserLoginTest() {
        ApiUserResponseModel response = step("Get запрос на получение информации по  login, password", () -> given(getUserRequestSpec)
                .when()
                .auth().preemptive().basic(credentialConfig.loginUserName(), credentialConfig.loginPassword())
                .get("/user/login")
                .then()
                .spec(responseSpecReference)
                .extract().as(ApiUserResponseModel.class));
        step("Получение ответа с данными по code", () ->
                assertEquals("200", response.getCode()));
        step("Получение ответа с данными по type", () ->
                assertEquals("unknown", response.getType()));
        step("Проверяем сообщение о сессии пользователя", () -> {
            assertTrue(response.getMessage().matches("logged in user session:\\d+"));
        });
    }

    @Test
    @DisplayName("Удаление ранее созданного пользователя")
    void successfulDeleteTest() {
        ApiUserResponseModel response = step("DELETE запрос на удаление пользователя", () -> given(requestSpecDelete)
                .header("accept", "application/json")
                .cookie("jsessionid", "7xfxbvtjydg21chsweied8ng0")
                .when()
                .delete("/user/Мария")
                .then()
                .spec(responseSpecDelete)
                .extract().as(ApiUserResponseModel.class));
        step("Получение ответа с данными по code", () ->
                assertEquals("200", response.getCode()));
    }

}
