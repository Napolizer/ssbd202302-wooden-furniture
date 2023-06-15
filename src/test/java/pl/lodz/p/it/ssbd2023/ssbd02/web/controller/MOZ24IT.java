package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.ChangeOrderStateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.ProductUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.hamcrest.Matchers.*;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.*;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.24 - Get done order list")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ24IT {
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Positive {
        @Order(1)
        @DisplayName("Should properly get done orders")
        @Test
        void shouldProperlyGetDoneOrders() {
            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(SALES_REP))
                    .when()
                    .get("/order/done")
                    .then()
                    .statusCode(200)
                    .body("size()", is(greaterThan(0)));
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Negative {
        @Order(1)
        @DisplayName("Should fail to get done orders without token")
        @Test
        void shouldFailToGetDoneOrdersWithoutToken() {
            given()
                    .when()
                    .get("/order/done")
                    .then()
                    .statusCode(401);
        }
    }
}
