package pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util;

import static io.restassured.RestAssured.given;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.CREATED;

import io.restassured.http.Header;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;

public class OrderUtil {
  private static final Header acceptLanguageHeader = new Header("Accept-Language", "en-US");
  public static OrderDto createOrder() {
    String body = """
        {
          "products": [{
            "amount": 1,
            "price": 3699.99,
            "productId": 104
          }],
          "shippingData": {
            "recipientFirstName": "Jan",
            "recipientLastName": "Kowalski",
            "recipientAddress": {
              "country": "Poland",
              "city": "Lodz",
              "street": "Piotrkowska",
              "streetNumber": 1,
              "postalCode": "90-000"
            }
          }
        }
        """;
    return given()
        .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
        .header("Content-Type", "application/json")
        .header(acceptLanguageHeader)
        .body(body)
        .when()
        .post("/order/create")
        .then()
        .statusCode(201)
        .extract()
        .response()
        .as(OrderDto.class);
  }

  public static OrderDto createOrder(OrderState state) {
    OrderDto order = createOrder();
    if (state.equals(CREATED)) {
      return order;
    }
    return given()
        .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
        .header("Content-Type", "application/json")
        .header(acceptLanguageHeader)
        .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(state, order.getHash()))
        .when()
        .put("/order/state/%s".formatted(order.getId()))
        .then()
        .statusCode(200)
        .extract()
        .response()
        .as(OrderDto.class);
  }
}
