package tests;

import models.ApiCreateUserModel;
import models.ApiCreateUserResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.ApiSpecs.*;

@Tag("api_test")
public class WorkingWithTestUser extends TestBase {

    private final String Username = "vesna2404";
    private final String Password = "vesna2404";

    @Test
    @DisplayName("Добавление нового пользователя в базу данных")
    void successfulCreateUserTest() {
        ApiCreateUserModel authData = new ApiCreateUserModel();
        authData.setId("2");
        authData.setUsername("Мария");
        authData.setFirstName("Маёрова");
        authData.setLastName("Ивановна");
        authData.setEmail("123@mail.ru");
        authData.setPassword("555Vesna24");
        authData.setPhone("+74956410041");
        authData.setUserStatus("1");

        ApiCreateUserResponseModel response = step("POST запрос на создание пользователя", () -> given(requestSpec)
                .body(authData)
                .when()
                .post("/user")

                .then()
                .spec(responseSpecReference)
                .extract().as(ApiCreateUserResponseModel.class));

        step("Получение ответа с данными по code", () ->
                assertEquals("200", response.getCode()));
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
                assertEquals("2", response.getId()));
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
        ApiCreateUserModel authData = new ApiCreateUserModel();
        authData.setId("2");
        authData.setUsername("Мария");
        authData.setFirstName("Маёрова");
        authData.setLastName("Ивановна");
        authData.setEmail("123@mail.ru");
        authData.setPassword("1234Vesna24");
        authData.setPhone("+74950044234");
        authData.setUserStatus("2");

        ApiCreateUserResponseModel response = step("PUT запрос на изменение данных пользователя", () -> given(requestSpec)
                .body(authData)
                .when()
                .put("/user/Мария")

                .then()
                .spec(responseSpecReference)
                .extract().as(ApiCreateUserResponseModel.class));

        step("Получение ответа с данными по code", () ->
                assertEquals("200", response.getCode()));
    }

    @Test
    @DisplayName("Авторизация пользователя")
    public void successfulGetUserLoginTest() {
        ApiCreateUserResponseModel response = step("Get запрос на получение информации по  login, password", () -> given(getUserRequestSpec)
                .when()
                .auth().preemptive().basic(Username, Password)
                .get("/user/login")
                .then()
                .spec(responseSpecReference)
                .extract().as(ApiCreateUserResponseModel.class));
        step("Получение ответа с данными по code", () ->
                assertEquals("200", response.getCode()));
    }

    @Test
    @DisplayName("Удаление ранее созданного пользователя")
    void successfulDeleteTest() {
        ApiCreateUserResponseModel response = step("DELETE запрос на удаление пользователя", () -> given(requestSpecDelete)
                .header("accept", "application/json")
                .cookie("jsessionid", "7xfxbvtjydg21chsweied8ng0")
                .when()
                .delete("/user/Мария")
                .then()
                .spec(responseSpecDelete)
                .extract().as(ApiCreateUserResponseModel.class));
        step("Получение ответа с данными по code", () ->
                assertEquals("200", response.getCode()));
    }

}
