package pl.lodz.p.it.ssbd2023.ssbd02.utils;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.AbstractEntity;

import java.util.List;
import java.util.Optional;

public interface Facade <T extends AbstractEntity> {
    T create(T entity);
    T delete(T entity);
    T update(T entity);
    Optional<T> find(Long id);
    List<T> findAll();
    List<T> findAllPresent();
    List<T> findAllArchived();
}
