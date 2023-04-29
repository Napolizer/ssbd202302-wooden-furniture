package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
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
@NamedQueries({
    @NamedQuery(name = Company.FIND_BY_NIP,
        query = "SELECT company FROM Company company WHERE company.nip = :nip"),
    @NamedQuery(name = Company.FIND_ALL_BY_COMPANY_NAME,
        query = "SELECT company FROM Company company WHERE company.companyName = :companyName"),
})
public class Company extends AbstractEntity {

  public static final String FIND_BY_NIP = "Company.findByNip";
  public static final String FIND_ALL_BY_COMPANY_NAME = "Company.findAllByCompanyName";

  @Column(nullable = false, unique = true, updatable = false, length = 10)
  private String nip;

  @Column(name = "company_name", nullable = false)
  private String companyName;

  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "client_id", nullable = false, unique = true)
  private Client client;
}
