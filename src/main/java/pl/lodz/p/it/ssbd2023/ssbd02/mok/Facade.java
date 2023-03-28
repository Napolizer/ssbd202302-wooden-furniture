package pl.lodz.p.it.ssbd2023.ssbd02.mok;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.AbstractEntity;

import java.util.List;
import java.util.Optional;

public interface Facade <T extends AbstractEntity> {
    T create(T entity);
    void delete(T entity);
    T update(T entity);
    Optional<T> find(Long id);
    List<T> findAll();
}
