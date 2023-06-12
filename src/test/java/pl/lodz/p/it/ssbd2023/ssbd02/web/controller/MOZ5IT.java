package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.5 - Add product")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ5IT {

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Positive {
		@ParameterizedTest(name = "fileName: {0}, width: {1}")
		@Order(1)
		@DisplayName("Should properly create product with any image file format")
		@CsvSource({
						"image.jpg,111",
						"image.jpeg,222",
						"image.png,333",
		})
		void shouldProperlyCreateProductWithAnyImageImageFileFormat(String fileName, Integer width) {
			ProductCreateDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setFurnitureWidth(width);
			File file = new File(System.getProperty("user.dir") + "/src/test/resources/uploads/" + fileName);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.multiPart("image", file)
							.multiPart("product", InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/new-image")
							.then()
							.statusCode(201);
		}

		@Test
		@Order(2)
		@DisplayName("Should properly create product with existing image")
		void shouldProperlyCreateWithExistingImage() {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setFurnitureWidth(444);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.log().all()
							.post("/product/existing-image")
							.then()
							.log().all()
							.statusCode(201);

		}

		@ParameterizedTest(name = "color: {0}, width: {1}")
		@Order(3)
		@DisplayName("Should properly create product with each color")
		@CsvSource({
						"BLACK,555",
						"RED,666",
						"GREEN,777",
						"BLUE,888",
						"BROWN,999",
						"WHITE,1111"
		})
		void shouldProperlyCreateProductWithEachColor(String color, Integer width) {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setFurnitureWidth(width);
			productCreateDto.setColor(color);
			File file = new File(System.getProperty("user.dir") + "/src/test/resources/uploads/image.jpg");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.multiPart("image", file)
							.multiPart("product", InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/new-image")
							.then()
							.statusCode(201);

		}

		@ParameterizedTest(name = "woodType: {0}, width: {1}")
		@Order(4)
		@DisplayName("Should properly create product with each wood type")
		@CsvSource({
						"BIRCH,2222",
						"OAK,3333",
						"DARK_OAK,4444",
						"SPRUCE,5555",
						"JUNGLE,6666",
						"ACACIA,7777"
		})
		void shouldProperlyCreateProductWithEachWoodType(String woodType, Integer width) {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setFurnitureWidth(width);
			productCreateDto.setWoodType(woodType);
			File file = new File(System.getProperty("user.dir") + "/src/test/resources/uploads/image.jpg");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.multiPart("image", file)
							.multiPart("product", InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/new-image")
							.then()
							.statusCode(201);
		}
	}

	@Nested
	@Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Negative {

		@Test
		@Order(1)
		@DisplayName("Should fail to add product with same details")
		void shouldFailToAddProductWithSameDetails() {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setFurnitureWidth(111);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.PRODUCT_ALREADY_EXITS));
		}

		@Test
		@Order(2)
		@DisplayName("Should fail to add product with empty data")
		void shouldFailToAddProductWithEmptyData() {
			ProductCreateWithImageDto productCreateDto = new ProductCreateWithImageDto();
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(400)
							.body("errors", hasSize(14));
		}

		@Test
		@Order(3)
		@DisplayName("Should fail to add product with negative number data")
		void shouldFailToAddProductWithNegativeNumberData() {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setFurnitureWidth(-1);
			productCreateDto.setFurnitureHeight(-1);
			productCreateDto.setFurnitureDepth(-1);
			productCreateDto.setPackageDepth(-1);
			productCreateDto.setPackageHeight(-1);
			productCreateDto.setPackageWidth(-1);
			productCreateDto.setWeight(-1.5);
			productCreateDto.setWeightInPackage(-1.5);
			productCreateDto.setAmount(-1);
			productCreateDto.setPrice(-1.99);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(400)
							.body("errors", hasSize(10));
		}

		@ParameterizedTest(name = "color: {0}")
		@Order(4)
		@DisplayName("Should fail to add product with non existing color")
		@CsvSource({
						"NEW_COLOR",
						"PURPLE",
						"ORANGE",
						"PINK",
		})
		void shouldFailToAddProductWithNonExistingColor(String color) {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setColor(color);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.INVALID_COLOR));
		}

		@ParameterizedTest(name = "woodType: {0}")
		@Order(5)
		@DisplayName("Should fail to add product with non existing wood type")
		@CsvSource({
						"ASH_WOOD",
						"NEW",
						"BROWN_CHERRY",
						"MAPLE_WOOD",
		})
		void shouldFailToAddProductWithNonExistingWoodType(String woodType) {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setWoodType(woodType);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.INVALID_WOOD_TYPE));
		}

		@Test
		@Order(6)
		@DisplayName("Should fail to add product with non existing product group")
		void shouldFailToAddProductWithNonExistingProductGroup() {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setProductGroupId(Long.MAX_VALUE);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(404)
							.body("message", equalTo(MessageUtil.MessageKey.PRODUCT_GROUP_NOT_FOUND));
		}

		@ParameterizedTest(name = "fileName: {0}")
		@Order(7)
		@DisplayName("Should fail to add product with invalid image file format")
		@CsvSource({
						"image.docx",
						"image.pdf",
						"image.txt",
		})
		void shouldFailToAddProductWithInvalidImageFileFormat(String fileName) {
			ProductCreateDto productCreateDto = InitData.getProductToCreate();
			File file = new File(System.getProperty("user.dir") + "/src/test/resources/uploads/" + fileName);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.multiPart("image", file)
							.multiPart("product", InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/new-image")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.INVALID_IMAGE_FILE_FORMAT));
		}

		@Test
		@Order(8)
		@DisplayName("Should fail to add product with existing image but with incompatible product group")
		void shouldFailToAddProductWithExistingImageButWithIncompatibleProductGroup() {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setProductGroupId(10L);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.INCOMPATIBLE_PRODUCT_IMAGE));
		}

		@Test
		@Order(9)
		@DisplayName("Should fail to add product with existing image but with incompatible color")
		void shouldFailToAddProductWithExistingImageButWithIncompatibleColor() {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setColor("BLACK");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.INCOMPATIBLE_PRODUCT_IMAGE));
		}

		@Test
		@Order(10)
		@DisplayName("Should fail to add product with existing image but with incompatible wood type")
		void shouldFailToAddProductWithExistingImageButWithIncompatibleWoodType() {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setWoodType("OAK");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.INCOMPATIBLE_PRODUCT_IMAGE));
		}

		@Test
		@Order(11)
		@DisplayName("Should fail to add product without image file or product data")
		void shouldFailToAddProductWithoutImageFileOrProductData() {
			ProductCreateDto productCreateDto = InitData.getProductToCreate();
			File file = new File(System.getProperty("user.dir") + "/src/test/resources/uploads/image.jpg");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.multiPart("product", InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/new-image")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.PRODUCT_CREATE_DTO_VALIDATION));

			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.multiPart("image", file)
							.when()
							.post("/product/new-image")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.PRODUCT_CREATE_DTO_VALIDATION));
		}

		@Test
		@Order(12)
		@DisplayName("Should fail to add product with image of non existing product")
		void shouldFailToAddProductWithImageOfNonExistingProduct() {
			ProductCreateWithImageDto productCreateDto = InitData.getProductToCreate();
			productCreateDto.setImageProductId(Long.MAX_VALUE);
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(productCreateDto))
							.when()
							.post("/product/existing-image")
							.then()
							.statusCode(404)
							.body("message", equalTo(MessageUtil.MessageKey.PRODUCT_NOT_FOUND));
		}
	}
}
