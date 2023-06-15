package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import javax.print.attribute.standard.Media;
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
import static jakarta.ws.rs.core.HttpHeaders.IF_MATCH;
import static org.hamcrest.Matchers.equalTo;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.3 - Edit Product")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MOZ3IT {

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
                    .price(200.0)
                    .amount(2)
                    .weight(55.5)
                    .weightInPackage(60.5)
                    .furnitureWidth(300)
                    .furnitureHeight(70)
                    .furnitureDepth(40)
                    .packageWidth(310)
                    .packageHeight(80)
                    .packageDepth(40)
                    .color(Color.BROWN.name())
                    .woodType(WoodType.BIRCH.name())
                    .productGroupId(1L)
                    .imageProductId(1L)
                    .build();

            ProductCreateWithImageDto productCreateWithImageDto2 = ProductCreateWithImageDto.builder()
                    .price(300.0)
                    .amount(2)
                    .weight(55.5)
                    .weightInPackage(60.5)
                    .furnitureWidth(300)
                    .furnitureHeight(70)
                    .furnitureDepth(40)
                    .packageWidth(310)
                    .packageHeight(80)
                    .packageDepth(40)
                    .color(Color.BLACK.name())
                    .woodType(WoodType.BIRCH.name())
                    .productGroupId(1L)
                    .imageProductId(2L)
                    .build();

            productId1 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto1);
            productId2 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto2);
        }

        @Order(2)
        @DisplayName("Should properly create products")
        @Test
        void shouldProperlyEditProduct() {
            given()
                    .when()
                    .get("/product/id/" + productId1)
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(200)
                    .body("id", equalTo(productId1))
                    .body("price", equalTo(200.0F))
                    .body("amount", equalTo(2));

            EditProductDto editProductDto = InitData.getEditedProduct(productId1, 30.0, 40);

            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .body(InitData.mapToJsonString(editProductDto))
                    .when()
                    .put("/product/editProduct/id/" + productId1)
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(200)
                    .body("id", equalTo(productId1))
                    .body("price", equalTo(30.0F))
                    .body("amount", equalTo(40));

            given()
                    .when()
                    .get("/product/id/" + productId1)
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(200)
                    .body("id", equalTo(productId1))
                    .body("price", equalTo(30.0F))
                    .body("amount", equalTo(40));
        }
    }

    @Nested
    @Order(2)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class Negative {
        @Order(1)
        @DisplayName("Should fail to edit product without token")
        @Test
        void shouldFailToEditProductWithoutToken() {
            EditProductDto editProductDto = InitData.getEditedProduct(productId1, 100.0, 7000);

            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(InitData.mapToJsonString(editProductDto))
                    .when()
                    .put("/product/editProduct/id/" + productId1)
                    .then()
                    .statusCode(401);
        }

        @Order(2)
        @DisplayName("Should fail to edit product with empty body")
        @Test
        void shouldFailToEditProductWithEmptyBody() {
            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .when()
                    .put("/product/editProduct/id/" + productId1)
                    .then()
                    .statusCode(400);
        }

        @Order(3)
        @DisplayName("Should fail to edit product when optimistic lock exception is thrown")
        @Test
        void shouldFailToEditProductWhenOptimisticLockExceptionIsThrown() {
            EditProductDto editProductDto = InitData.getEditedProduct(productId1, 230.0, 1110);

            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .body(InitData.mapToJsonString(editProductDto))
                    .when()
                    .put("/product/editProduct/id/" + productId1)
                    .then()
                    .statusCode(200);

            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .body(InitData.mapToJsonString(editProductDto))
                    .when()
                    .put("/product/editProduct/id/" + productId1)
                    .then()
                    .statusCode(409);
        }
    }
}
