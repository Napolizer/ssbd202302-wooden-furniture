package pl.lodz.p.it.ssbd2023.ssbd02.entities;

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
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
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
@Table(indexes = @Index(name = "category_parent_category_id", columnList = "parent_category_id"))
@NamedQueries({
    @NamedQuery(name = Category.FIND_ALL_BY_PARENT_CATEGORY,
        query = "SELECT category FROM Category category WHERE category.parentCategory = :parentCategory")
})
public class Category extends AbstractEntity {
  public static final String FIND_ALL_BY_PARENT_CATEGORY = "Category.findAllByParentCategory";

  @ManyToOne
  @JoinColumn(name = "parent_category_id")
  private Category parentCategory;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "category_name", nullable = false)
  private CategoryName categoryName;

  @OneToMany(mappedBy = "category")
  @JoinColumn(nullable = false)
  private List<ProductGroup> productGroups = new ArrayList<>();
}
