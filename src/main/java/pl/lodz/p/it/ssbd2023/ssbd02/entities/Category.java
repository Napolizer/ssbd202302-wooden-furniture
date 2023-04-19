package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
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
    @Column(name= "category_name", nullable = false)
    private CategoryName categoryName;

    @OneToMany(mappedBy = "category")
    private List<ProductGroup> productGroups = new ArrayList<>();
}
