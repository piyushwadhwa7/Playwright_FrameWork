package com.qa.opencart.tests;

import com.qa.opencart.base.BaseApiTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Users_Data_API extends BaseApiTest {
    private Integer createdUserId;
    private String createdUserName;
    private String createdUserEmail;
    private String createdUserGender;
    private String createdUserStatus;
    private String updatedUserName;
    private String updatedUserStatus;
    private String token;

    @BeforeClass
    public void setupUserApiTest() {
        token = getRequiredProperty("gorest_bearer_token");
    }


    @Test(priority = 1, description = "This is the API test for creating a USER using POST request")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserApiTest() {
        //validate create_Users API is working or not
        // methods to verify the API : Given , When, Then
        String uniqueValue = String.valueOf(System.currentTimeMillis());
        createdUserName = "piyush wadhwa " + uniqueValue;
        createdUserEmail = "piyush" + uniqueValue + "@example.com";
        createdUserGender = "male";
        createdUserStatus = "active";

        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        Response response = given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .body("{ \"name\": \"" + createdUserName + "\", \"email\": \"" + createdUserEmail + "\", \"gender\": \"" + createdUserGender + "\", \"status\": \"" + createdUserStatus + "\" }")
                .when()
                .post("/users")
                .then()
                .log().all()
                .assertThat()
                .statusCode(201)
                .time(lessThan(3000L))
                .header("Content-Type", containsString("application/json"))
                .header("Location", matchesPattern("https://gorest.co.in/public/v2/users/\\d+"))
                .header("Cache-Control", containsString("max-age=0"))
                .header("x-content-type-options", equalTo("nosniff"))
                .header("x-frame-options", equalTo("SAMEORIGIN"))
                .header("x-request-id", matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))
                .header("x-ratelimit-limit", matchesPattern("\\d+"))
                .header("x-ratelimit-remaining", matchesPattern("\\d+"))
                .body("id", notNullValue())
                .body("id", instanceOf(Integer.class))
                .body("id", greaterThan(0))
                .body("name", instanceOf(String.class))
                .body("name", matchesPattern("^[a-zA-Z ]+\\d+$"))
                .body("name", equalTo(createdUserName))
                .body("email", instanceOf(String.class))
                .body("email", matchesPattern("^piyush\\d+@example\\.com$"))
                .body("email", equalTo(createdUserEmail))
                .body("gender", instanceOf(String.class))
                .body("gender", anyOf(equalTo("male"), equalTo("female")))
                .body("gender", equalTo(createdUserGender))
                .body("status", instanceOf(String.class))
                .body("status", anyOf(equalTo("active"), equalTo("inactive")))
                .body("status", equalTo(createdUserStatus))
                .extract()
                .response();

        createdUserId = response.path("id");
        System.out.println("Response as json / string "+ response.asString());
        Assert.assertTrue(
                response.getHeader("Location").endsWith("/" + createdUserId),
                "Location header should end with created user id"
        );
    }

    @Test(priority = 2, description = "This is the API test for listing all USERS using GET request")
    @Severity(SeverityLevel.CRITICAL)
    public void listOfUserApiTest() {
        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .when()
                .get("/users")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .time(lessThan(3000L))
                .body("$", instanceOf(List.class))
                .body("size()", greaterThan(0))
                .body("id", everyItem(notNullValue()))
                .body("name", everyItem(notNullValue()))
                .body("email", everyItem(notNullValue()))
                .body("gender", everyItem(notNullValue()))
                .body("status", everyItem(notNullValue()));
    }

    @Test(priority = 3, dependsOnMethods = "createUserApiTest", description = "This is the API test for getting the created USER using GET request")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserCreatedApiTest() {
        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .when()
                .get("/users/{userId}", createdUserId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .time(lessThan(3000L))
                .body("id", equalTo(createdUserId))
                .body("name", equalTo(createdUserName))
                .body("email", equalTo(createdUserEmail))
                .body("gender", equalTo(createdUserGender))
                .body("status", equalTo(createdUserStatus));
    }
    @Test(priority = 4, dependsOnMethods = "getUserCreatedApiTest", description = "This is the API test for updating the created USER using PATCH request")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserApiTest() {
        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        updatedUserName = "piyush wadhwa updated";
        updatedUserStatus = "inactive";
        given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .body("{ \"name\": \"" + updatedUserName + "\", \"status\": \"" + updatedUserStatus + "\" }")
                .when()
                .patch("/users/{userId}", createdUserId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .time(lessThan(3000L))
                .body("id", equalTo(createdUserId))
                .body("name", equalTo(updatedUserName))
                .body("email", equalTo(createdUserEmail))
                .body("gender", equalTo(createdUserGender))
                .body("status", equalTo(updatedUserStatus));
    }
    @Test(priority = 5, dependsOnMethods = "updateUserApiTest", description = "This is the API test for deleting the created USER using DELETE request")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserApiTest() {
        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        Response deleteResponse = given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .when()
                .delete("/users/{userId}", createdUserId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(204)
                .time(lessThan(3000L))
                .extract()
                .response();

        Assert.assertTrue(deleteResponse.asString().isEmpty(), "Delete response body should be empty for 204 response");

        given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .when()
                .get("/users/{userId}", createdUserId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(404)
                .body("message", equalTo("Resource not found"));
    }
}
