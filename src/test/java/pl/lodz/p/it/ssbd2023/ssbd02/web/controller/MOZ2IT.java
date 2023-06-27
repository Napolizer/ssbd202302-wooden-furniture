package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.ProductUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.2 - Get product")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MOZ2IT {
    public int productId1;
    public int productId2;
    public int productId3;
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
                    .color(Color.RED.name())
                    .woodType(WoodType.BIRCH.name())
                    .productGroupId(1L)
                    .imageProductId(3L)
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
                    .color(Color.WHITE.name())
                    .woodType(WoodType.OAK.name())
                    .productGroupId(2L)
                    .imageProductId(5L)
                    .build();

            ProductCreateWithImageDto productCreateWithImageDto3 = ProductCreateWithImageDto.builder()
                    .price(400.0)
                    .amount(2)
                    .weight(55.5)
                    .weightInPackage(60.5)
                    .furnitureWidth(300)
                    .furnitureHeight(70)
                    .furnitureDepth(40)
                    .packageWidth(310)
                    .packageHeight(80)
                    .packageDepth(40)
                    .color(Color.RED.name())
                    .woodType(WoodType.SPRUCE.name())
                    .productGroupId(3L)
                    .imageProductId(6L)
                    .build();

            productId1 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto1);
            productId2 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto2);
            productId3 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto3);
        }
        @DisplayName("Should properly get product by id")
        @Test
        @Order(2)
        void shouldProperlyGetProductById() {
            given()
                    .when()
                    .get("/product/id/" + productId1)
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(200)
                    .body("id", equalTo(productId1))
                    .body("price",equalTo(200.0F))
                    .body("amount", equalTo(2))
                    .body("archive", equalTo(false))
                    .body("color", equalTo("RED"))
                    .body("weight", equalTo(55.5F))
                    .body("weightInPackage", equalTo(60.5F))
                    .body("woodType", equalTo("BIRCH"));

            given()
                    .when()
                    .get("/product/id/" + productId2)
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(200)
                    .body("id", equalTo(productId2))
                    .body("price",equalTo(300.0F))
                    .body("amount", equalTo(2))
                    .body("archive", equalTo(false))
                    .body("color", equalTo("WHITE"))
                    .body("weight", equalTo(55.5F))
                    .body("weightInPackage", equalTo(60.5F))
                    .body("woodType", equalTo("OAK"));

            given()
                    .when()
                    .get("/product/id/" + productId3)
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(200)
                    .body("id", equalTo(productId3))
                    .body("price",equalTo(400.0F))
                    .body("amount", equalTo(2))
                    .body("archive", equalTo(false))
                    .body("color", equalTo("RED"))
                    .body("weight", equalTo(55.5F))
                    .body("weightInPackage", equalTo(60.5F))
                    .body("woodType", equalTo("SPRUCE"));
        }
    }

    @Nested
    @Order(2)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class Negative {
        @DisplayName("Should fail to get product with none id given")
        @Test
        @Order(1)
        void shouldFailToGetProductWithNoneIdGiven() {
            given()
                    .when()
                    .get("/product/id/")
                    .then()
                    .contentType("text/html")
                    .statusCode(404);
        }
    }
}
