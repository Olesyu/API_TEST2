package tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.assertj.core.api.SoftAssertions;

public class BaseTest {

    public static SoftAssertions softAssertions = new SoftAssertions();
    public static Faker faker = new Faker();


    static {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .setBasePath("/v2")
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .build();

    }
}
