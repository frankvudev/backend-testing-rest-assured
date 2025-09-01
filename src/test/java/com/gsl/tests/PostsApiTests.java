package com.gsl.tests;

import com.gsl.tests.utils.AllureLogger;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@Epic("JSONPlaceholder API Tests")
@Feature("Posts API")
public class PostsApiTests extends BaseTest {

    @DataProvider(name = "validIds")
    public Object[][] validIds() { return new Object[][]{{1},{5},{10}}; }

    @DataProvider(name = "invalidIds")
    public Object[][] invalidIds() { return new Object[][]{{9999},{12345}}; }

    @DataProvider(name = "createPosts")
    public Object[][] createPosts() {
        return new Object[][] { {"foo1","bar1",1}, {"foo2","bar2",2} };
    }

    @DataProvider(name = "updatePosts")
    public Object[][] updatePosts() {
        return new Object[][] { {1,"updated title 1","updated body 1"}, {2,"updated title 2","updated body 2"} };
    }

    @DataProvider(name = "patchPosts")
    public Object[][] patchPosts() {
        return new Object[][] { {1,"patched title 1"}, {2,"patched title 2"} };
    }

    @Story("Get post by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "validIds")
    public void testGetPostByValidIdSchema(int postId){
        Response response = given().log().all()
                .when().get("/posts/{id}", postId)
                .then().log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"))
                .extract().response();

        AllureLogger.logRequest("GET /posts/" + postId, "Request sent");
        AllureLogger.logResponse("GET /posts/" + postId, response.asPrettyString());
    }

    @Story("Get post by invalid ID")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "invalidIds")
    public void testGetPostByInvalidId(int postId) {
        Response response = given()
                .log().all()
                .when()
                .get("/posts/{id}", postId)
                .then()
                .log().all()
                .statusCode(404) // JSONPlaceholder returns 404 for invalid IDs
                .extract().response();

        // Assert response JSON is empty
        JsonPath json = response.jsonPath();
        assert json.getMap("$").isEmpty() : "Response JSON is not empty!";

        AllureLogger.logRequest("GET /posts/" + postId, "Request sent for invalid ID");
        AllureLogger.logResponse("GET /posts/" + postId, response.asPrettyString());
    }

    @Story("Create post")
    @Severity(SeverityLevel.BLOCKER)
    @Test(dataProvider = "createPosts")
    public void testCreatePost(String title, String body, int userId){
        String requestBody = String.format("{\"title\":\"%s\",\"body\":\"%s\",\"userId\":%d}", title, body, userId);

        Response response = given().contentType(ContentType.JSON).body(requestBody).log().all()
                .when().post("/posts")
                .then().log().all()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"))
                .extract().response();

        AllureLogger.logRequest("POST /posts", requestBody);
        AllureLogger.logResponse("POST /posts", response.asPrettyString());
    }

    @Story("Update post (PUT)")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "updatePosts")
    public void testUpdatePostPut(int postId, String title, String body){
        String requestBody = String.format("{\"id\":%d,\"title\":\"%s\",\"body\":\"%s\",\"userId\":1}", postId, title, body);

        Response response = given().contentType(ContentType.JSON).body(requestBody).log().all()
                .when().put("/posts/{id}", postId)
                .then().log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/post-update-schema.json"))
                .extract().response();

        AllureLogger.logRequest("PUT /posts/" + postId, requestBody);
        AllureLogger.logResponse("PUT /posts/" + postId, response.asPrettyString());
    }

    @Story("Partial update post (PATCH)")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "patchPosts")
    public void testUpdatePostPatch(int postId, String newTitle){
        String requestBody = String.format("{\"title\":\"%s\"}", newTitle);

        Response response = given().contentType(ContentType.JSON).body(requestBody).log().all()
                .when().patch("/posts/{id}", postId)
                .then().log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/post-update-schema.json"))
                .extract().response();

        AllureLogger.logRequest("PATCH /posts/" + postId, requestBody);
        AllureLogger.logResponse("PATCH /posts/" + postId, response.asPrettyString());
    }

    @Story("Delete post")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "validIds")
    public void testDeletePost(int postId){
        Response response = given().log().all()
                .when().delete("/posts/{id}", postId)
                .then().log().all()
                .statusCode(anyOf(is(200), is(204)))
                .extract().response();

        AllureLogger.logRequest("DELETE /posts/" + postId, "Request sent");
        AllureLogger.logResponse("DELETE /posts/" + postId, response.asPrettyString());
    }
}