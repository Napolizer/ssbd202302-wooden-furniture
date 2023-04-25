package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Dimensions;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.api.ProductFacadeOperations;

import java.io.File;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
class ProductFacadeOperationsIT {
    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;
    @Inject
    private ProductFacadeOperations productFacadeOperations;

    private Product validProduct;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addAsResource(new File("src/test/resources/"),"");
    }

    @BeforeEach
    void setup() throws Exception {
        validProduct = Product.builder()
                .price(1000.0)
                .available(true)
                .image(null)
                .weight(150.0)
                .amount(20)
                .weightInPackage(180.0)
                .furnitureDimensions(new Dimensions(
                        1000.0, 200.0, 200.0
                ))
                .packageDimensions(new Dimensions(
                        1100.0, 250.0, 250.0
                ))
                .color(Color.BLUE)
                .woodType(WoodType.JUNGLE)
                .build();

        utx.begin();
        em.createQuery("DELETE FROM Product").executeUpdate();
        utx.commit();
    }
    @Test
    void createProductPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        Product productWithImage = validProduct = Product.builder()
                .price(1000.0)
                .available(true)
                .image(new Byte[]{})
                .weight(150.0)
                .amount(20)
                .weightInPackage(180.0)
                .furnitureDimensions(new Dimensions(
                        1000.0, 200.0, 200.0
                ))
                .packageDimensions(new Dimensions(
                        1100.0, 250.0, 250.0
                ))
                .color(Color.BLUE)
                .woodType(WoodType.JUNGLE)
                .build();
        Product savedProductWithImage = productFacadeOperations.create(productWithImage);
        assertThat(savedProductWithImage, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(2)));
    }

    @Test
    void createProductWithNullPriceValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setPrice(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }

    @Test
    void createProductWithNullAvailableValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setAvailable(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }

    @Test
    void createProductWithNullWeightValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setWeight(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }
    @Test
    void createProductWithNullAmountValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setAmount(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }
    @Test
    void createProductWithNullWeightInPackageValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setWeightInPackage(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }

    @Test
    void createProductWithNullFurnitureDimensionsValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setFurnitureDimensions(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }

    @Test
    void createProductWithNullPackageDimensionsValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setPackageDimensions(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }

    @Test
    void createProductWithNullColorValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setColor(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }
    @Test
    void createProductWithNullWoodTypeValueNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        validProduct.setWoodType(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.create(validProduct));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
    }

    @Test
    void deleteProductPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        productFacadeOperations.archive(savedProduct);
        assertThat(productFacadeOperations.find(savedProduct.getId()).get().getArchive(), is(equalTo(true)));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void deleteProductNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        productFacadeOperations.archive(savedProduct);
        assertThat(productFacadeOperations.find(savedProduct.getId()).get().getArchive(), is(equalTo(true)));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.archive(savedProduct));
    }

    @Test
    void updatePricePositivePositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setPrice(2000.0);
        Product productWithUpdatedPrice = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedPrice, is(notNullValue()));
        assertThat(productWithUpdatedPrice.getPrice(), is(equalTo(validProduct.getPrice())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateAvailabilityPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setAvailable(false);
        Product productWithUpdatedAvailability = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedAvailability, is(notNullValue()));
        assertThat(productWithUpdatedAvailability.getAvailable(), is(equalTo(validProduct.getAvailable())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateImagePositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setImage(new Byte[]{});
        Product productWithUpdatedImage = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedImage, is(notNullValue()));
        assertThat(productWithUpdatedImage.getImage(), is(equalTo(validProduct.getImage())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateWeightPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setWeight(140.0);
        Product productWithUpdatedWeight = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedWeight, is(notNullValue()));
        assertThat(productWithUpdatedWeight.getWeight(), is(equalTo(validProduct.getWeight())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateAmountPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setAmount(10);
        Product productWithUpdatedAmount = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedAmount, is(notNullValue()));
        assertThat(productWithUpdatedAmount.getAmount(), is(equalTo(validProduct.getAmount())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateWeightInPackagePositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setWeightInPackage(170.0);
        Product productWithUpdatedWeightInPackage = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedWeightInPackage, is(notNullValue()));
        assertThat(productWithUpdatedWeightInPackage.getWeightInPackage(), is(equalTo(validProduct.getWeightInPackage())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateFurnitureDimensionsPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setFurnitureDimensions(new Dimensions(
                1100.0, 250.0, 250.0
        ));
        Product productWithUpdatedFurnitureDimensions = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedFurnitureDimensions, is(notNullValue()));
        assertThat(productWithUpdatedFurnitureDimensions.getFurnitureDimensions(), is(equalTo(validProduct.getFurnitureDimensions())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updatePackageDimensionsPositiveTest() {
        validProduct.setPackageDimensions(new Dimensions(
                1200.0, 300.0, 250.0
        ));
        Product productWithUpdatedPackageDimensions = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedPackageDimensions, is(notNullValue()));
        assertThat(productWithUpdatedPackageDimensions.getPackageDimensions(), is(equalTo(validProduct.getPackageDimensions())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateColorPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setColor(Color.GREEN);
        Product productWithUpdatedColor = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedColor, is(notNullValue()));
        assertThat(productWithUpdatedColor.getColor(), is(equalTo(validProduct.getColor())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateWoodTypePositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setWoodType(WoodType.SPRUCE);
        Product productWithUpdatedWoodType = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedWoodType, is(notNullValue()));
        assertThat(productWithUpdatedWoodType.getWoodType(), is(equalTo(validProduct.getWoodType())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void updateMultipleFieldsPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setPrice(2000.0);
        validProduct.setAvailable(false);
        validProduct.setImage(new Byte[]{});
        validProduct.setAmount(50);
        validProduct.setWeightInPackage(321.0);
        validProduct.setFurnitureDimensions(new Dimensions(
                1234.0, 321.0, 123.0
        ));
        validProduct.setPackageDimensions(new Dimensions(
                1240.0, 330.0, 130.0
        ));
        validProduct.setColor(Color.GREEN);
        validProduct.setWoodType(WoodType.SPRUCE);
        Product updatedProduct = productFacadeOperations.update(validProduct);
        assertThat(updatedProduct, is(notNullValue()));
        assertThat(updatedProduct.getPrice(), is(equalTo(validProduct.getPrice())));
        assertThat(updatedProduct.getAvailable(), is(equalTo(validProduct.getAvailable())));
        assertThat(updatedProduct.getImage(), is(equalTo(validProduct.getImage())));
        assertThat(updatedProduct.getWeight(), is(equalTo(validProduct.getWeight())));
        assertThat(updatedProduct.getAmount(), is(equalTo(validProduct.getAmount())));
        assertThat(updatedProduct.getWeightInPackage(), is(equalTo(validProduct.getWeightInPackage())));
        assertThat(updatedProduct.getFurnitureDimensions(), is(equalTo(validProduct.getFurnitureDimensions())));
        assertThat(updatedProduct.getPackageDimensions(), is(equalTo(validProduct.getPackageDimensions())));
        assertThat(updatedProduct.getColor(), is(equalTo(validProduct.getColor())));
        assertThat(updatedProduct.getWoodType(), is(equalTo(validProduct.getWoodType())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    //do dyskusji
//    @Test
//    void updateNonExistingProductNegativeTest() {
//        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
//        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
//        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
//    }

    @Test
    void updatePriceNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setPrice(2000.0);
        Product productWithUpdatedPrice = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedPrice, is(notNullValue()));
        assertThat(productWithUpdatedPrice.getPrice(), is(equalTo(validProduct.getPrice())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
        productWithUpdatedPrice.setPrice(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.update(productWithUpdatedPrice));
    }

    @Test
    void updateAvailabilityNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setAvailable(false);
        Product productWithUpdatedAvailability = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedAvailability, is(notNullValue()));
        assertThat(productWithUpdatedAvailability.getAvailable(), is(equalTo(validProduct.getAvailable())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
        productWithUpdatedAvailability.setAvailable(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.update(productWithUpdatedAvailability));
    }

    @Test
    void updateImageNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setImage(new Byte[]{});
        Product productWithUpdatedImage = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedImage, is(notNullValue()));
        assertThat(productWithUpdatedImage.getImage(), is(equalTo(validProduct.getImage())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
    }

    @Test
    void updateWeightNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setWeight(140.0);
        Product productWithUpdatedWeight = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedWeight, is(notNullValue()));
        assertThat(productWithUpdatedWeight.getWeight(), is(equalTo(validProduct.getWeight())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
        productWithUpdatedWeight.setWeight(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.update(productWithUpdatedWeight));
    }

    @Test
    void updateAmountNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setAmount(10);
        Product productWithUpdatedAmount = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedAmount, is(notNullValue()));
        assertThat(productWithUpdatedAmount.getAmount(), is(equalTo(validProduct.getAmount())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
        productWithUpdatedAmount.setAmount(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.update(productWithUpdatedAmount));
    }

    @Test
    void updateWeightInPackageNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setWeightInPackage(170.0);
        Product productWithUpdatedWeightInPackage = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedWeightInPackage, is(notNullValue()));
        assertThat(productWithUpdatedWeightInPackage.getWeightInPackage(), is(equalTo(validProduct.getWeightInPackage())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
        productWithUpdatedWeightInPackage.setWeightInPackage(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.update(productWithUpdatedWeightInPackage));
    }

    @Test
    void updateFurnitureDimensionsNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setFurnitureDimensions(new Dimensions(
                1100.0, 250.0, 250.0
        ));
        Product productWithUpdatedFurnitureDimensions = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedFurnitureDimensions, is(notNullValue()));
        assertThat(productWithUpdatedFurnitureDimensions.getFurnitureDimensions(), is(equalTo(validProduct.getFurnitureDimensions())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
    }

    @Test
    void updatePackageDimensionsNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setPackageDimensions(new Dimensions(
                1200.0, 300.0, 250.0
        ));
        Product productWithUpdatedPackageDimensions = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedPackageDimensions, is(notNullValue()));
        assertThat(productWithUpdatedPackageDimensions.getPackageDimensions(), is(equalTo(validProduct.getPackageDimensions())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
    }

    @Test
    void updateColorNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setColor(Color.GREEN);
        Product productWithUpdatedColor = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedColor, is(notNullValue()));
        assertThat(productWithUpdatedColor.getColor(), is(equalTo(validProduct.getColor())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
        productWithUpdatedColor.setColor(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.update(productWithUpdatedColor));
    }

    @Test
    void updateWoodTypeNegativeTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        validProduct.setWoodType(WoodType.SPRUCE);
        Product productWithUpdatedWoodType = productFacadeOperations.update(validProduct);
        assertThat(productWithUpdatedWoodType, is(notNullValue()));
        assertThat(productWithUpdatedWoodType.getWoodType(), is(equalTo(validProduct.getWoodType())));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThrows(EJBException.class, () -> productFacadeOperations.update(validProduct));
        productWithUpdatedWoodType.setWoodType(null);
        assertThrows(EJBException.class, () -> productFacadeOperations.update(productWithUpdatedWoodType));
    }

    @Test
    void findProductPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThat(productFacadeOperations.find(validProduct.getId()).get(), is(equalTo(validProduct)));
    }

    @Test
    void findProductNegativeTest() {
        assertThrows(EJBException.class, () -> productFacadeOperations.find(validProduct.getId()));
    }

    @Test
    void findAllProductsPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
    }

    @Test
    void findAllByWoodTypePositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        Product anotherValidProduct = validProduct = Product.builder()
                .price(1000.0)
                .available(true)
                .image(new Byte[]{})
                .weight(150.0)
                .amount(20)
                .weightInPackage(180.0)
                .furnitureDimensions(new Dimensions(
                        1000.0, 200.0, 200.0
                ))
                .packageDimensions(new Dimensions(
                        1100.0, 250.0, 250.0
                ))
                .color(Color.BLUE)
                .woodType(WoodType.OAK)
                .build();
        Product secondSavedProduct = productFacadeOperations.create(anotherValidProduct);
        assertThat(secondSavedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(2)));
        assertThat(productFacadeOperations.findAllByWoodType(WoodType.JUNGLE).size(), is(equalTo(1)));
        assertThat(productFacadeOperations.findAllByWoodType(WoodType.OAK).size(), is(equalTo(1)));
        assertThat(productFacadeOperations.findAllByWoodType(WoodType.DARK_OAK).size(), is(equalTo(0)));
    }

    @Test
    void findAllByColorPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        Product anotherValidProduct = validProduct = Product.builder()
                .price(1000.0)
                .available(true)
                .image(new Byte[]{})
                .weight(150.0)
                .amount(20)
                .weightInPackage(180.0)
                .furnitureDimensions(new Dimensions(
                        1000.0, 200.0, 200.0
                ))
                .packageDimensions(new Dimensions(
                        1100.0, 250.0, 250.0
                ))
                .color(Color.GREEN)
                .woodType(WoodType.OAK)
                .build();
        Product secondSavedProduct = productFacadeOperations.create(anotherValidProduct);
        assertThat(secondSavedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(2)));
        assertThat(productFacadeOperations.findAllByColor(Color.BLUE).size(), is(equalTo(1)));
        assertThat(productFacadeOperations.findAllByColor(Color.GREEN).size(), is(equalTo(1)));
        assertThat(productFacadeOperations.findAllByColor(Color.BLACK).size(), is(equalTo(0)));
    }

    @Test
    void findAvailableProductsPositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        assertThat(productFacadeOperations.findAllAvailable().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(1)));
        assertThat(productFacadeOperations.findAllAvailable().size(), is(equalTo(1)));
        Product anotherValidProduct = Product.builder()
                .price(1000.0)
                .available(false)
                .image(new Byte[]{})
                .weight(150.0)
                .amount(20)
                .weightInPackage(180.0)
                .furnitureDimensions(new Dimensions(
                        1000.0, 200.0, 200.0
                ))
                .packageDimensions(new Dimensions(
                        1100.0, 250.0, 250.0
                ))
                .color(Color.GREEN)
                .woodType(WoodType.OAK)
                .build();
        assertThat(productFacadeOperations.create(anotherValidProduct), is(notNullValue()));
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(2)));
        assertThat(productFacadeOperations.findAllAvailable().size(), is(equalTo(1)));
    }

    @Test
    void findProductsByPricePositiveTest() {
        assertThat(productFacadeOperations.findAll().size(), is(equalTo(0)));
        assertThat(productFacadeOperations.findAllAvailable().size(), is(equalTo(0)));
        Product savedProduct = productFacadeOperations.create(validProduct);
        assertThat(savedProduct, is(notNullValue()));
        assertThat(productFacadeOperations.findAllByPrice(10.0, 20.0).size(), is(equalTo(0)));
        assertThat(productFacadeOperations.findAllByPrice(10.0, 2500.0).size(), is(equalTo(1)));
    }
}