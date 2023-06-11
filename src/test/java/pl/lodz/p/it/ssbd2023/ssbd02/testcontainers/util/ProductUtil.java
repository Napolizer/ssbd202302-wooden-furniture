package pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

public class ProductUtil {

  public static int createProductGroup(ProductGroupCreateDto productGroupCreateDto) {
    String productGroupCreateString = InitData.mapToJsonString(productGroupCreateDto);
    String token = AuthUtil.retrieveToken("employee");
    return given()
        .header(AUTHORIZATION, "Bearer " + token)
        .header(CONTENT_TYPE, "application/json")
        .body(productGroupCreateString)
        .when()
        .post("/product/group")
        .then()
        .statusCode(201)
        .extract()
        .path("id");
  }

  public static int createProductWithExistingImage(ProductCreateWithImageDto productCreateWithImageDto) {
    String productCreateWithImageString = InitData.mapToJsonString(productCreateWithImageDto);
    String token = AuthUtil.retrieveToken("employee");
    return given()
        .header(AUTHORIZATION, "Bearer " + token)
        .header(CONTENT_TYPE, "application/json")
        .body(productCreateWithImageString)
        .when()
        .post("/product/existing-image")
        .then()
        .statusCode(201)
        .extract()
        .path("id");
  }
}
