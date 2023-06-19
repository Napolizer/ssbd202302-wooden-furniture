package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.7 - Add product group")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ7IT {
	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "http://localhost:8080/api/v1";
	}

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Positive {
		@ParameterizedTest(name = "categoryName: {0}")
		@Order(1)
		@DisplayName("Should properly create product group with any subcategory")
		@CsvSource({
						"SINGLE_BED",
						"DOUBLE_BED",
						"KIDS",
						"WARDROBE",
						"DRESSER",
						"LOCKER",
						"CHAIR",
						"DESK",
						"STOOL",
						"ARMCHAIR",
						"ROUND_TABLE",
						"RECTANGULAR_TABLE",
		})
		void shouldProperlyCreateProductGroupWithAnyCategory(String categoryName) {
			ProductGroupCreateDto productGroupCreateDto = InitData.getProductGroupToCreate();
			productGroupCreateDto.setName("Product Group " + categoryName.split("_")[0]);
			productGroupCreateDto.setCategoryName(categoryName);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productGroupCreateDto))
							.when()
							.post("/product/group")
							.then()
							.statusCode(201)
							.body("name", equalTo(productGroupCreateDto.getName()))
							.body("category.name", equalTo(productGroupCreateDto.getCategoryName()))
							.body("averageRating", equalTo(0.0F))
							.body("archive", equalTo(false))
							.body("name", equalTo(productGroupCreateDto.getName()));
		}

	}

	@Nested
	@Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Negative {

		@Test
		@Order(1)
		@DisplayName("Should fail to add product group with existing name")
		void shouldFailToAddProductGroupWithExistingName() {
			ProductGroupCreateDto productGroupCreateDto = InitData.getProductGroupToCreate();
			productGroupCreateDto.setName("Product Group SINGLE");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productGroupCreateDto))
							.when()
							.post("/product/group")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.PRODUCT_GROUP_ALREADY_EXITS));
		}

		@ParameterizedTest(name = "categoryName: {0}")
		@Order(2)
		@DisplayName("Should fail to add product group with any parent category")
		@CsvSource({
						"BED",
						"CASE_FURNITURE",
						"SEAT",
						"TABLE",
		})
		void shouldFailToAddProductGroupWithAnyParentCategory(String categoryName) {
			ProductGroupCreateDto productGroupCreateDto = InitData.getProductGroupToCreate();
			productGroupCreateDto.setCategoryName(categoryName);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productGroupCreateDto))
							.when()
							.post("/product/group")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.PARENT_CATEGORY_NOT_ALLOWED));

		}

		@Test
		@Order(3)
		@DisplayName("Should fail to add product with empty data")
		void shouldFailToAddProductGroupWithEmptyData() {
			ProductGroupCreateDto productGroupCreateDto = new ProductGroupCreateDto();
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productGroupCreateDto))
							.when()
							.post("/product/group")
							.then()
							.statusCode(400)
							.body("errors", hasSize(2));
		}

		@Test
		@Order(4)
		@DisplayName("Should fail to add product with blank product name")
		void shouldFailToAddProductGroupWithBlankProductName() {
			ProductGroupCreateDto productGroupCreateDto = InitData.getProductGroupToCreate();
			productGroupCreateDto.setName("");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productGroupCreateDto))
							.when()
							.post("/product/group")
							.then()
							.statusCode(400)
							.body("errors", hasSize(2))
							.body("errors[0].field", equalTo("name"))
							.body("errors[1].field", equalTo("name"));
		}

		@Test
		@Order(5)
		@DisplayName("Should fail to add product with too long product name")
		void shouldFailToAddProductGroupWithTooLongProductName() {
			ProductGroupCreateDto productGroupCreateDto = InitData.getProductGroupToCreate();
			productGroupCreateDto.setName("TooLongProductGroupNameTooLongProductGroupNameTooLongProductGroupName");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productGroupCreateDto))
							.when()
							.post("/product/group")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("name"))
							.body("errors[0].message",
											equalTo("The length of the field must be between 1 and 50 characters"));
		}

		@ParameterizedTest(name = "name: {0}")
		@Order(6)
		@DisplayName("Should fail to add product with invalid pattern product name")
		@CsvSource({
						"123Product Group",
						"product Group",
						"Product Group123"
		})
		void shouldFailToAddProductGroupWithInvalidPatternProductName(String name) {
			ProductGroupCreateDto productGroupCreateDto = InitData.getProductGroupToCreate();
			productGroupCreateDto.setName(name);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productGroupCreateDto))
							.when()
							.post("/product/group")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("name"))
							.body("errors[0].message",
											equalTo("Field must start with a capital letter and contain only letters"));
		}

		@ParameterizedTest(name = "categoryName: {0}")
		@Order(5)
		@DisplayName("Should fail to add product with non existing category name")
		@CsvSource({
						"FOTEL",
						"SOFA",
						"FURNITURE",
						"OUTDOOR",
						"DESKS"
		})
		void shouldFailToAddProductGroupWithNonExistingCategoryName(String categoryName) {
			ProductGroupCreateDto productGroupCreateDto = InitData.getProductGroupToCreate();
			productGroupCreateDto.setCategoryName(categoryName);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productGroupCreateDto))
							.when()
							.post("/product/group")
							.then()
							.statusCode(404)
							.body("message", equalTo(MessageUtil.MessageKey.CATEGORY_NOT_FOUND));
		}

	}
}
