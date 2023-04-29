package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Version
  @Column(nullable = false)
  private Long version;

  @Setter
  @Column(columnDefinition = "boolean default false not null")
  @Builder.Default
  private Boolean archive = false;
}
