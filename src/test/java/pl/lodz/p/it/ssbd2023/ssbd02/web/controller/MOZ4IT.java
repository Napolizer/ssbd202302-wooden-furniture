package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.ProductUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.*;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.*;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.4 - Archive Product")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MOZ4IT {
    public int productId1;
    public int productId2;

    @Nested
    @Order(1)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class Positive {

        @Order(1)
        @DisplayName("Should properly create products")
        @Test
        void shouldProperlyCreateProducts() {
            ProductCreateWithImageDto productCreateWithImageDto1 = ProductCreateWithImageDto.builder()
                    .price(400.0)
                    .amount(5)
                    .weight(55.5)
                    .weightInPackage(60.5)
                    .furnitureWidth(300)
                    .furnitureHeight(70)
                    .furnitureDepth(40)
                    .packageWidth(310)
                    .packageHeight(80)
                    .packageDepth(40)
                    .color(Color.BROWN.name())
                    .woodType(WoodType.ACACIA.name())
                    .productGroupId(39L)
                    .imageProductId(69L)
                    .build();

            ProductCreateWithImageDto productCreateWithImageDto2 = ProductCreateWithImageDto.builder()
                    .price(499.0)
                    .amount(5)
                    .weight(53.5)
                    .weightInPackage(62.5)
                    .furnitureWidth(301)
                    .furnitureHeight(80)
                    .furnitureDepth(41)
                    .packageWidth(312)
                    .packageHeight(85)
                    .packageDepth(43)
                    .color(Color.BROWN.name())
                    .woodType(WoodType.ACACIA.name())
                    .productGroupId(39L)
                    .imageProductId(69L)
                    .build();

            productId1 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto1);
            productId2 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto2);
        }

        @Order(2)
        @DisplayName("Should properly archive product")
        @Test
        void shouldProperlyArchiveProduct() {
            given()
                    .when()
                    .get("/product/id/" + productId1)
                    .then()
                    .statusCode(200)
                    .body("archive", is(equalTo(false)));

            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .when()
                    .patch("/product/archive/" + productId1)
                    .then()
                    .statusCode(200)
                    .body("message", is(equalTo("moz.product.archive.successful")));

            given()
                    .when()
                    .get("/product/id/" + productId1)
                    .then()
                    .statusCode(200)
                    .body("archive",is(equalTo(true)));
        }
    }
    @Nested
    @Order(2)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class Negative {
        @Order(1)
        @DisplayName("Should fail to archive product with invalid token")
        @Test
        void shouldFailToArchiveProductWithoutToken() {
            given()
                    .when()
                    .patch("/product/archive/" + productId2)
                    .then()
                    .statusCode(401);
        }

        @Order(2)
        @DisplayName("Should fail to archive already archived product")
        @Test
        void shouldFailToArchiveAlreadyArchivedProduct() {

            given()
                    .when()
                    .get("/product/id/" + productId2)
                    .then()
                    .statusCode(200)
                    .body("archive", is(equalTo(false)));

            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .when()
                    .patch("/product/archive/" + productId2)
                    .then()
                    .statusCode(200)
                    .body("message", is(equalTo("moz.product.archive.successful")));

            given()
                    .when()
                    .get("/product/id/" + productId2)
                    .then()
                    .statusCode(200)
                    .body("archive",is(equalTo(true)));

            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .when()
                    .patch("/product/archive/" + productId2)
                    .then()
                    .statusCode(400);
        }

        @Order(3)
        @DisplayName("Should fail to archive product with invalid id given")
        @Test
        void shouldFailToArchiveProductWithInvalidIdGiven() {
            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .when()
                    .patch("/product/archive/" + Long.MAX_VALUE)
                    .then()
                    .statusCode(404);
        }

        @Order(4)
        @DisplayName("Should fail to archive product with empty id value")
        @Test
        void shouldFailToArchiveProductWithEmptyId() {
            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .when()
                    .patch("/product/archive/")
                    .then()
                    .statusCode(404);
        }
        @Order(5)
        @DisplayName("Should fail to archive product without enough permissions")
        @Test
        void shouldFailToEditProductWithoutEnoughPermissions() {
            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(CLIENT))
                    .when()
                    .patch("/product/archive/" + productId2)
                    .then()
                    .statusCode(401);
        }
    }
}
