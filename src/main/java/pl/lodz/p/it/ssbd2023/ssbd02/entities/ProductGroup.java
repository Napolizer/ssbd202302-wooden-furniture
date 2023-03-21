package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductGroup extends AbstractEntity {
    private Boolean archive;
    private String name;
    private Double averageRating;
    private Collection<Product> products;
    private Collection<Rate> rates;
    private Category category;
}
