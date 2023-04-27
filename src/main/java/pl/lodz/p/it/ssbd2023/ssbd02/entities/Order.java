package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(name = "sales_order")
@Table(name = "sales_order", indexes = {
        @Index(name = "sales_order_account_id", columnList = "account_id", unique = true),
        @Index(name = "sales_order_delivery_person_id", columnList = "delivery_person_id", unique = true),
        @Index(name = "sales_order_delivery_address_id", columnList = "delivery_address_id", unique = true)
})
public class Order extends AbstractEntity {

    @Column(nullable = false, updatable = false, name = "creation_date")
    private LocalDateTime creationDate;

    @PrePersist
    public void init() {
        creationDate = LocalDateTime.now();
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "order_state")
    private OrderState orderState;


    @ManyToMany
    @JoinTable(
            name = "sales_order_product",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"),
            indexes = @Index(name = "sales_order_product_product_id", columnList = "product_id", unique = true)
    )
    private List<Product> products = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "delivery_person_id", nullable = false, updatable = false)
    private Person deliveryPerson;

    @OneToOne
    @JoinColumn(name = "delivery_address_id", nullable = false, updatable = false)
    private Address deliveryAddress;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;

}
