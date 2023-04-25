package pl.lodz.p.it.ssbd2023.ssbd02.utils.facade;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AbstractEntity;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class AbstractFacade <T extends AbstractEntity> implements Facade <T> {
    private Class<T> entityClass;

    protected abstract EntityManager getEntityManager();

    @Override
    public T create(T entity) {
        getEntityManager().persist(entity);
        return entity;
    }

    @Override
    public T archive(T entity) {
        entity.setArchive(true);
        return getEntityManager().merge(entity);
    }

    @Override
    public T update(T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public Optional<T> find(Long id) {
        return Optional.ofNullable(getEntityManager().find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        String tableName = entityClass.getName();

        return getEntityManager().createQuery("SELECT entity FROM " + tableName + " entity", entityClass)
                .getResultList();
    }

    @Override
    public List<T> findAllPresent() {
        String tableName = entityClass.getName();

        return getEntityManager().createQuery("SELECT entity FROM " + tableName + " entity WHERE entity.archive = false", entityClass)
                .getResultList();
    }

    @Override
    public List<T> findAllArchived() {
        String tableName = entityClass.getName();

        return getEntityManager().createQuery("SELECT entity FROM " + tableName + " entity WHERE entity.archive = true", entityClass)
                .getResultList();
    }
}
