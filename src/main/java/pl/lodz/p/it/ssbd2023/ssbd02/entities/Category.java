package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "category", indexes = {@Index(name = "category_parent_category_id", columnList = "parent_category_id"),
    @Index(name = "category_image_id", columnList = "image_id")})
@NamedQueries({
    @NamedQuery(name = Category.FIND_BY_CATEGORY_NAME,
                query = "SELECT category FROM Category category "
                        + "WHERE category.categoryName = :categoryName"),
    @NamedQuery(name = Category.FIND_ALL_PARENT_CATEGORIES,
            query = "SELECT category FROM Category category "
                    + "WHERE category.parentCategory = null "),

})
public class Category extends AbstractEntity {

  public static final String FIND_BY_CATEGORY_NAME = "Category.findByCategoryName";
  public static final String FIND_ALL_PARENT_CATEGORIES = "Category.findAllParentCategories";

  @OneToMany(mappedBy = "parentCategory")
  @Builder.Default
  private List<Category> subcategories = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "parent_category_id", insertable = false, updatable = false)
  private Category parentCategory;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "category_name", nullable = false)
  private CategoryName categoryName;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "image_id", nullable = false)
  private Image image;

  @OneToMany(mappedBy = "category")
  @JoinColumn(nullable = false)
  @Builder.Default
  private List<ProductGroup> productGroups = new ArrayList<>();
}
