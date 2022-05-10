package tests;

import com.github.javafaker.Faker;
import core.model.BaseResponseModel;
import core.model.UserModel;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import static core.Endpoints.*;

public class UserTest extends BaseTest {


    static UserModel userModel;
    static String message;


    @Test  //6
    public void createUserWithAnyRandomData() {

        String userNickName = faker.name().username();

        UserModel expectUserModel = UserModel.builder()
                .username(userNickName)
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .password(faker.internet().password())
                .build();

        BaseResponseModel response = RestAssured
                .given()
                .body(expectUserModel)
                .when()
                .post(USER)
                .then()
                .statusCode(200).extract().as(BaseResponseModel.class);

        userModel = expectUserModel;
        message = response.message;


        softAssertions.assertThat(response.message).as("check response body not 0").isNotEqualTo("0");
        softAssertions.assertAll();


    }

    @Test(dependsOnMethods = "createUserWithAnyRandomData")  //7
    public void getUserFromTest6() {

        UserModel response = RestAssured
                .given()
                .pathParam("username", userModel.username)
                .when()
                .get(USER_BY_USERNAME)
                .then()
                .statusCode(200).extract().as(UserModel.class);

        softAssertions.assertThat(Long.toString(response.id))
                .as("Check that used id = value of massage field from previous test (#6)").isEqualTo(message);
        softAssertions.assertThat(response)
                .as("Check that all data from test #6 was saved")
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(userModel);

        softAssertions.assertAll();

    }


    @Test(dependsOnMethods = "createUserWithAnyRandomData") //8
    public void LogWithTheCreatedUser() {

        BaseResponseModel response = RestAssured
                .given()
                .queryParam("username", userModel.username)
                .queryParam("password ", userModel.password)
                .when()
                .get(USER_LOGIN)
                .then()
                .statusCode(200).extract().as(BaseResponseModel.class);


        softAssertions.assertThat(response.message)
                .as("Check that message contains value is logged in user session:").contains("logged in user session:");
        softAssertions.assertAll();
    }
}
