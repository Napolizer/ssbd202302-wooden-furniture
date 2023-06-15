package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.1 - Get product list")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ1IT {
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Positive {
        @Test
        @Order(1)
        @DisplayName("Should properly get all products list")
        void shouldProperlyGetAllProductsList() {
            given()
                    .when()
                    .get("/product")
                    .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("size()", greaterThanOrEqualTo(111))
                    .rootPath("find { it.id == 1 }")
                    .body("id", equalTo(1))
                    .body("amount", equalTo(4))
                    .body("archive", equalTo(false))
                    .body("color", equalTo("BROWN"))
                    .body("price", equalTo(2299.99F))
                    .body("weight", equalTo(80.3F))
                    .body("weightInPackage", equalTo(84.1F))
                    .body("woodType", equalTo("BIRCH"));
        }

        @ParameterizedTest(name = "productGroupId: {0}")
        @Order(2)
        @DisplayName("Should properly get all products by product group list")
        @CsvSource({
                "1",
                "2",
                "3",
        })
        void shouldProperlyGetAllProductsByProductGroupList(String productGroupId) {
            given()
                    .when()
                    .get("/product/group/products/id/" + productGroupId)
                    .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("size()", greaterThanOrEqualTo(2));
        }

        @ParameterizedTest(name = "categoryId: {0}")
        @Order(3)
        @DisplayName("Should properly get all products by category list")
        @CsvSource({
                "5",
                "6",
                "7",
        })
        void shouldProperlyGetAllProductsByCategoryList(String categoryId) {
            given()
                    .when()
                    .get("/product/category/id/" + categoryId)
                    .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("size()", greaterThanOrEqualTo(2));
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Negative {
        @Test
        @Order(1)
        @DisplayName("Should fail to find products by product group with none id given")
        void shouldFailToFindProductsByProductGroupWithNoneIdGiven() {
            given()
                    .when()
                    .get("/product/group/products/id")
                    .then()
                    .statusCode(404)
                    .contentType("text/html");
        }

        @Test
        @Order(2)
        @DisplayName("Should fail to find products by category with none id given")
        void shouldFailToFindProductsByCategoryWithNoneIdGiven() {
            given()
                    .when()
                    .get("/product/category/id")
                    .then()
                    .statusCode(404)
                    .contentType("text/html");
        }
    }
}
