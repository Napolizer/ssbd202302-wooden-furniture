package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(name = "sales_order")
public class Order extends AbstractEntity {

    @Column(nullable = false, name = "creation_date")
    private Date creationDate;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "order_state")
    private OrderState orderState;


    @ManyToMany
    @JoinTable(
            name = "sales_order_product",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "delivery_person_id", nullable = false)
    private Person deliveryPerson;

    @OneToOne
    @JoinColumn(name = "delivery_address_id", nullable = false)
    private Address deliveryAddress;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

}
