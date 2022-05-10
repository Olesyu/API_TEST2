package tests;

import com.github.javafaker.Faker;
import core.model.BaseResponseModel;
import core.model.PetModel;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static core.Endpoints.*;
import static core.Statuses.*;

public class PetTests extends BaseTest {
    static long petId;

    @Test    //1
    public void checkThatAfterCreationPetIdNotEquals0Test() {
        List<PetModel.Tag> listTags = new ArrayList<>();
        listTags.add(new PetModel.Tag(31, "Small dog"));
        listTags.add(new PetModel.Tag(30, "Cute"));
        listTags.add(new PetModel.Tag(20, "Silent"));
        List<String> listUrl = new ArrayList<>();
        listUrl.add("https://unsplash.com/photos/v3-zcCWMjgM");
        listUrl.add("https://unsplash.com/photos/T-0EW-SEbsE");
        listUrl.add("https://unsplash.com/photos/BJaqPaH6AGQ");

        PetModel expectPetModel = PetModel.builder()
                .name("Rex")
                .category(new PetModel.Category(10, "Dogs"))
                .tags(listTags)
                .photoUrls(listUrl)
                .status(AVAILABLE)
                .build();

        PetModel response = RestAssured
                .given()
                .body(expectPetModel)
                .when()
                .post(PET)
                .then()
                .statusCode(200).extract().as(PetModel.class);

        petId = response.id;

        softAssertions.assertThat(response.id)
                .as("check response ID is not 0")
                .isNotEqualTo(0);
        softAssertions.assertThat(response)
                .as("check if response data is equals to expected data")
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectPetModel);
        softAssertions.assertAll();

    }

    @Test(dependsOnMethods = "checkThatAfterCreationPetIdNotEquals0Test")     //2

    public void checkThatPetNameInResponseRexTest() {

        PetModel response = RestAssured
                .given()
                .pathParam("id", petId)
                .when()
                .get(PET_BY_ID)
                .then()
                .statusCode(200).extract().as(PetModel.class);


        softAssertions.assertThat(response.name)
                .as("check name in response is Rex ")
                .isEqualTo("Rex");
        softAssertions.assertThat(response.status)
                .as("check dog's status")
                .isEqualTo(AVAILABLE);
        softAssertions.assertAll();
    }

    @Test(dependsOnMethods = "checkThatAfterCreationPetIdNotEquals0Test")   //3

    public void updatePetNameAndStatus() {

        RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .pathParam("id", petId)
                .formParam("name", "Sky")
                .formParam("status", SOLD)
                .when()
                .post(PET_BY_ID)
                .then()
                .statusCode(200).extract();

        PetModel response = RestAssured
                .given()
                .pathParam("id", petId)
                .when()
                .get(PET_BY_ID)
                .then()
                .statusCode(200).extract().as(PetModel.class);

        softAssertions.assertThat(response.name)
                .as("check name in response is Sky ")
                .isEqualTo("Sky");
        softAssertions.assertThat(response.status)
                .as("check dog's status")
                .isEqualTo(SOLD);
        softAssertions.assertAll();
    }

    @Test(dependsOnMethods = "checkThatAfterCreationPetIdNotEquals0Test")  //4

    public void deletePetTest() {
        BaseResponseModel baseResponseModel = new BaseResponseModel(1, "error", "Pet not found");

        BaseResponseModel responseDelete = RestAssured
                .given()
                .pathParam("id", petId)
                .when()
                .delete(PET_BY_ID)
                .then()
                .statusCode(200).extract().as(BaseResponseModel.class);

        BaseResponseModel response = RestAssured
                .given()
                .pathParam("id", petId)
                .when()
                .get(PET_BY_ID)
                .then()
                .statusCode(404).extract().as(BaseResponseModel.class);

        softAssertions.assertThat(responseDelete.message)
                .as("check id in message ")
                .isEqualTo(Long.toString(petId));
        softAssertions.assertThat(response)
                .as("check response body")
                .isEqualTo(baseResponseModel);
        softAssertions.assertAll();
    }

    @Test //5

    public void createNewDogWithRandomDataAndStatusSold() {

        List<PetModel.Tag> listTags = new ArrayList<>();
        listTags.add(new PetModel.Tag(faker.number().numberBetween(10, 40), faker.dog().gender()));
        listTags.add(new PetModel.Tag(faker.number().numberBetween(10, 40), faker.dog().size()));
        listTags.add(new PetModel.Tag(faker.number().numberBetween(10, 40), faker.dog().coatLength()));


        PetModel expectPetModel = PetModel.builder()
                .name(faker.dog().name())
                .tags(listTags)
                .category(new PetModel.Category(10, "Dogs"))
                .status(SOLD)
                .build();

        PetModel createResponse = RestAssured
                .given()
                .body(expectPetModel)
                .when()
                .post(PET)
                .then()
                .statusCode(200).extract().as(PetModel.class);

        PetModel[] response = RestAssured
                .given()
                .queryParam("status", SOLD)
                .when()
                .get(PET_BY_STATUS)
                .then()
                .statusCode(200).extract().as(PetModel[].class);

        softAssertions.assertThat(response)
                .as("Find created pet by status sold")
                .anyMatch(pet -> pet.id == createResponse.id);
        softAssertions.assertAll();

    }


}
