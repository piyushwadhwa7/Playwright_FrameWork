package com.qa.opencart.tests;

import com.qa.opencart.base.BaseApiTest;
import com.qa.opencart.pages.Payload;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Users_Data_API extends BaseApiTest {
    private final String uniqueValue = String.valueOf(System.currentTimeMillis());
    private String token;

    @BeforeClass
    public void setupUserApiTest() {
        token = getRequiredProperty("gorest_bearer_token");
    }

    @DataProvider(name = "UserDataProvider_Data")
    public Object[][] getUsersData() {
        return new Object[][]{{"piyush wadhwa " + uniqueValue,
       "piyush" + uniqueValue + "@example.com",
        "male",
        "active"},
                {"riti chawla " + uniqueValue,
                        "riti" + uniqueValue + "@example.com",
                        "female",
                        "active"}};
    }


    @Test(priority = 1, dataProvider = "UserDataProvider_Data")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserApiTest(String createdUserName , String createdUserEmail, String createdUserGender , String createdUserStatus ) {
        //validate create_Users API is working or not
        // methods to verify the API : Given , When, Then
        String userPayload = Payload.userData(createdUserName, createdUserEmail, createdUserGender, createdUserStatus);

        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        Response response = given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .body(userPayload)
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
                .body("email", matchesPattern("^[a-zA-Z]+\\d+@example\\.com$"))
                .body("email", equalTo(createdUserEmail))
                .body("gender", instanceOf(String.class))
                .body("gender", anyOf(equalTo("male"), equalTo("female")))
                .body("gender", equalTo(createdUserGender))
                .body("status", instanceOf(String.class))
                .body("status", anyOf(equalTo("active"), equalTo("inactive")))
                .body("status", equalTo(createdUserStatus))
                .extract()
                .response();

        Integer createdUserId = response.path("id");
        Payload.saveCreatedUserId(createdUserEmail, createdUserId);
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

    @Test(priority = 3, dataProvider = "UserDataProvider_Data", dependsOnMethods = "createUserApiTest", description = "This is the API test for getting the created USER using GET request")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserCreatedApiTest(String createdUserName , String createdUserEmail, String createdUserGender , String createdUserStatus) {
        Integer createdUserId = Payload.getCreatedUserId(createdUserEmail);
        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .when()
                .get("/users/{userId}", createdUserId)// path paramter
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
    @Test(priority = 4, dataProvider = "UserDataProvider_Data", dependsOnMethods = "getUserCreatedApiTest", description = "This is the API test for updating the created USER using PATCH request")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserApiTest(String createdUserName , String createdUserEmail, String createdUserGender , String createdUserStatus) {
        Integer createdUserId = Payload.getCreatedUserId(createdUserEmail);
        String updatedUserName = createdUserName + " updated";
        String updatedUserStatus = "inactive";
        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        String userUpdatePayload = Payload.userUpdateData(updatedUserName, updatedUserStatus);
        given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .body(userUpdatePayload)
                .when()
                .put("/users/{userId}", createdUserId)
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
    @Test(priority = 5, dataProvider = "UserDataProvider_Data", dependsOnMethods = "updateUserApiTest", description = "This is the API test for getting the updated USER using GET request")
    @Severity(SeverityLevel.CRITICAL)
    public void getUpdatedUserCreatedApiTest(String createdUserName , String createdUserEmail, String createdUserGender , String createdUserStatus) {
        Integer createdUserId = Payload.getCreatedUserId(createdUserEmail);
        String updatedUserName = createdUserName + " updated";
        String updatedUserStatus = "inactive";
        RestAssured.baseURI = "https://gorest.co.in/public/v2/";
        String getUpdatedValues=given().auth().oauth2(token)
                .header("Content-Type", "application/json")
                .when()
                .get("/users/{userId}", createdUserId)// path paramter
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .time(lessThan(3000L))
                .body("id", equalTo(createdUserId))
                .body("name", equalTo(updatedUserName))
                .body("email", equalTo(createdUserEmail))
                .body("gender", equalTo(createdUserGender))
                .body("status", equalTo(updatedUserStatus))
                .extract().response().asString();
        JsonPath jsonPath = new JsonPath(getUpdatedValues);
        String actualUpdatedValue1=jsonPath.getString("name");
        System.out.println("actualUpdatedValue1=    "+actualUpdatedValue1);
        //assertion testing framewroks : 1. Junit and TestNg
        Assert.assertEquals( actualUpdatedValue1, updatedUserName, "=======CREATED USER NAME IS NOT GETTING UPDATED THROUGH THE UPDATE ( PUT )========");
    }
    @Test(priority = 6, dataProvider = "UserDataProvider_Data", dependsOnMethods = "getUpdatedUserCreatedApiTest", description = "This is the API test for deleting the created USER using DELETE request")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserApiTest(String createdUserName , String createdUserEmail, String createdUserGender , String createdUserStatus) {
        Integer createdUserId = Payload.getCreatedUserId(createdUserEmail);
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

        Payload.removeCreatedUserId(createdUserEmail);
    }

    @Test
    public void mockResponseCheck(){
        JsonPath js=new JsonPath(Payload.coursePrice());
        int count=js.getInt("data.size()");
        System.out.println("count="+count);
        int pagecount=js.getInt("pagination.totalPages");
        System.out.println("pagecount="+pagecount);
        String dataTitle=js.get("data[1].title");
        System.out.println("dataTitle="+dataTitle);
        for(int i=0;i<count;i++){
            String dataTitlesNames =js.get("data["+i+"].title");
            int dataPrices=js.getInt("data["+i+"].price");
            System.out.println("dataTitlesNames="+dataTitlesNames);
            System.out.println("dataPrices="+dataPrices);
        }

    }
}
