package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "rate", indexes = {
    @Index(name = "rate_person_id", columnList = "person_id", unique = true),
    @Index(name = "rate_product_group_id", columnList = "product_group_id", unique = true)
})
@NamedQueries({@NamedQuery(name = Rate.FIND_ALL_BY_VALUE,
    query = "SELECT rate from Rate rate WHERE rate.value = :value"),
    @NamedQuery(name = Rate.FIND_ALL_BY_PERSON_ID,
        query = "SELECT rate from Rate rate WHERE rate.person.id = :personId")}
)
public class Rate extends AbstractEntity {

  public static final String FIND_ALL_BY_VALUE = "Rate.findAllByValue";
  public static final String FIND_ALL_BY_PERSON_ID = "Rate.findAllByPerson_Id";

  @Column(nullable = false)
  @Min(1)
  @Max(5)
  private Integer value;

  @ManyToOne
  @JoinColumn(name = "person_id", nullable = false)
  private Person person;
}
