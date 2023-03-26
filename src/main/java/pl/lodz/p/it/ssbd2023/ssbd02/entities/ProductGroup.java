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
@Entity(name = "product_group")
public class ProductGroup extends AbstractEntity {

    @Column(nullable = false)
    private Boolean archive;

    @Column(nullable = false)
    private String name;

    @Column(name = "average_rating")
    private Double averageRating;

    @OneToMany
    @JoinColumn(name = "product_group_id")
    private List<Product> products = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "product_group_id")
    private List<Rate> rates = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
