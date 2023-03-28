package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@NamedQueries({ @NamedQuery(name = Rate.FIND_ALL_BY_VALUE,
                        query = "SELECT rate from Rate rate WHERE rate.value = :value"),
                @NamedQuery(name = Rate.FIND_ALL_BY_PERSON_ID,
                        query = "SELECT rate from Rate rate WHERE rate.person.id = :personId")}
)
public class Rate extends AbstractEntity {

    public static final String FIND_ALL_BY_VALUE = "Rate.findAllByValue";
    public static final String FIND_ALL_BY_PERSON_ID = "Rate.findAllByPerson_Id";

    @Column(nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}
