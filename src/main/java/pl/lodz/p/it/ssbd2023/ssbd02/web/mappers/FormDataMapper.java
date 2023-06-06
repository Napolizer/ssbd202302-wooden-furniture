package pl.lodz.p.it.ssbd2023.ssbd02.web.mappers;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.core.MediaType;
import java.util.Set;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public final class FormDataMapper {
  public static ProductCreateDto mapFormDataBodyPartToProductCreateDto(FormDataBodyPart product) {
    if (product == null) {
      throw ApplicationExceptionFactory.createProductCreateDtoValidationException();
    }
    product.setMediaType(MediaType.APPLICATION_JSON_TYPE);
    ProductCreateDto productCreateDto;
    try {
      productCreateDto = product.getValueAs(ProductCreateDto.class);
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createProductCreateDtoValidationException();
    }
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<ProductCreateDto>> violations = validator.validate(productCreateDto);
      if (!violations.isEmpty()) {
        JsonObjectBuilder jsonObject = Json.createObjectBuilder()
                .add("message", MessageUtil.MessageKey.PRODUCT_CREATE_DTO_VALIDATION)
                .add("title", "Validation Errors");

        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        for (ConstraintViolation<?> constraint : violations) {
          String fullClassName = constraint.getLeafBean().getClass().toString();
          String message = constraint.getMessage();
          String propertyPath = constraint.getPropertyPath().toString();

          JsonObject jsonError = Json.createObjectBuilder()
                  .add("class", fullClassName.substring(fullClassName.lastIndexOf(".") + 1))
                  .add("field", propertyPath)
                  .add("message", message)
                  .build();
          jsonArray.add(jsonError);

        }
        throw ApplicationExceptionFactory
                .createProductCreateDtoValidationException(jsonObject.add("errors", jsonArray.build()).build());
      }
    }
    return productCreateDto;
  }
}
