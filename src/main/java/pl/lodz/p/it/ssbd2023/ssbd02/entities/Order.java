package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractEntity {
    @Column(nullable = false, name = "creation_date")
    private Date creationDate;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "order_state")
    private OrderState orderState;

    @Embedded
    @Column(nullable = false, name = "product_group")
    private ProductGroup productGroup;

    @Embedded
    @Column(nullable = false, name = "delivery_person")
    private Person deliveryPerson;

    @Embedded
    @Column(nullable = false, name = "delivery_address")
    private Address deliveryAddress;

    @Embedded
    @Column(nullable = false)
    private Account account;

    @Embedded
    private Rate rate;
}
