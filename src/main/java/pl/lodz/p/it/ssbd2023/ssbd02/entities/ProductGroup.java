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
@Table(name = "product_group", indexes = @Index(name = "product_group_category_id", columnList = "category_id", unique = true))
public class ProductGroup extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "average_rating")
    private Double averageRating;

    @OneToMany
    @JoinColumn(name = "product_group_id", nullable = false)
    private List<Product> products = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "product_group_id", nullable = false)
    private List<Rate> rates = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
