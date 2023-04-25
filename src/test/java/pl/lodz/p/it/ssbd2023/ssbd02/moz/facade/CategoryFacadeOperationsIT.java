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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.api.CategoryFacadeOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class CategoryFacadeOperationsIT {
    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;
    @Inject
    private CategoryFacadeOperations categoryFacadeOperations;
    private Category category;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addAsResource(new File("src/test/resources/"), "");
    }

    @BeforeEach
    void setUp() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        category = Category.builder()
                .categoryName(CategoryName.BED)
                .build();
        utx.begin();
        em.createQuery("DELETE FROM Category").executeUpdate();
        utx.commit();
    }

    @Test
    void findAllCategoriesTest() {
        assertEquals(0, categoryFacadeOperations.findAll().size());
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        assertEquals(1, categoryFacadeOperations.findAll().size());
        Category newCategory = Category.builder()
                        .categoryName(CategoryName.ARMCHAIR)
                        .build();
        assertDoesNotThrow(() -> categoryFacadeOperations.create(newCategory));
        assertEquals(2, categoryFacadeOperations.findAll().size());

    }

    @Test
    void createCategoryPositiveTest() {
        assertEquals(0, categoryFacadeOperations.findAll().size());

        Category persistedCategory = categoryFacadeOperations.create(category);
        List<Category> allCategories = categoryFacadeOperations.findAll();

        assertEquals(1, allCategories.size());
        assertEquals(persistedCategory, allCategories.get(0));
    }

    @Test
    void createCategoryWithNullCategoryNameNegativeTest() {
        Category wrongCategory = Category.builder().build();
        assertThrows(EJBException.class, () -> categoryFacadeOperations.create(wrongCategory));
    }

    @Test
    void findCategoryByIdPositiveTest() {
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        assertDoesNotThrow(() -> categoryFacadeOperations.find(category.getId()));
        assertEquals(category, categoryFacadeOperations.find(category.getId()).orElse(null));
    }

    @Test
    void findCategoryByIdNegativeTestCategoryDoesNotExistInDatabase() {
        assertThrows(EJBException.class, () -> categoryFacadeOperations.find(category.getId()));
    }

    @Test
    void updateCategoryNamePositiveTest() {
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        category.setCategoryName(CategoryName.CHAIR);
        assertDoesNotThrow(() -> categoryFacadeOperations.update(category));
        assertEquals(CategoryName.CHAIR, categoryFacadeOperations.find(category.getId()).orElseThrow().getCategoryName());
    }

    @Test
    void updateParentCategoryPositiveTest() {
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        Category newParentCategory = Category
                .builder()
                .categoryName(CategoryName.BED)
                .build();
        assertNull(categoryFacadeOperations.find(category.getId()).orElseThrow().getParentCategory());
        category.setParentCategory(newParentCategory);
        assertDoesNotThrow(() -> categoryFacadeOperations.update(category));
        assertEquals(newParentCategory.getCategoryName(), categoryFacadeOperations.find(category.getId()).orElseThrow().getParentCategory().getCategoryName());
    }

    @Test
    void updateProductGroupPositiveTest() {
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        Product product = Product.builder()
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

        List<Product> products = new ArrayList<>();
        products.add(product);

        ProductGroup productGroup = ProductGroup.builder()
                .archive(false)
                .products(products)
                .name("BEDS")
                .build();
        List<ProductGroup> productGroups = new ArrayList<>();
        productGroups.add(productGroup);

        assertEquals(0, categoryFacadeOperations.find(category.getId()).orElseThrow().getProductGroups().size());
        category.setProductGroups(productGroups);
        assertDoesNotThrow(() -> categoryFacadeOperations.update(category));
        assertEquals(1, categoryFacadeOperations.find(category.getId()).orElseThrow().getProductGroups().size());
    }

    @Test
    void updateCategoryWithNullCategoryNameNegativeTest() {
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        category.setCategoryName(null);
        assertThrows(EJBException.class, () -> categoryFacadeOperations.update(category));
    }

    @Test
    void deleteCategoryPositiveTest() {
        assertEquals(0, categoryFacadeOperations.findAll().size());
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        assertEquals(1, categoryFacadeOperations.findAll().size());
        categoryFacadeOperations.archive(category);
        assertTrue(categoryFacadeOperations.find(category.getId()).orElseThrow().getArchive());
        assertEquals(1, categoryFacadeOperations.findAll().size());
    }

    @Test
    void deleteCategoryNegativeTest() {
        assertEquals(0, categoryFacadeOperations.findAll().size());
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        assertEquals(1, categoryFacadeOperations.findAll().size());
        categoryFacadeOperations.archive(category);
        assertTrue(categoryFacadeOperations.find(category.getId()).orElseThrow().getArchive());
        assertEquals(1, categoryFacadeOperations.findAll().size());
        assertThrows(EJBException.class, () -> categoryFacadeOperations.archive(category));
    }

    @Test
    void findAllByParentCategoryTest() {
        Category firstCategory = Category.builder()
                .parentCategory(category)
                .categoryName(CategoryName.DOUBLE_BED)
                .build();
        Category secondCategory = Category.builder()
                .parentCategory(category)
                .categoryName(CategoryName.DOUBLE_BED)
                .build();
        assertDoesNotThrow(() -> categoryFacadeOperations.create(category));
        assertEquals(0, categoryFacadeOperations.findAllByParentCategory(category).size());
        assertDoesNotThrow(() -> categoryFacadeOperations.create(firstCategory));
        assertEquals(1, categoryFacadeOperations.findAllByParentCategory(category).size());
        assertDoesNotThrow(() -> categoryFacadeOperations.create(secondCategory));
        assertEquals(2, categoryFacadeOperations.findAllByParentCategory(category).size());
    }
}
