package com.play4jpa.jpa.models;

import com.play4jpa.jpa.query.Query;

import java.util.List;

/**
 * Play Ebean like implementation of Finder for Hibernate.
 * <p/>
 * To execute complex queries, get an empty query by calling {@link #query()} and adding restrictions.
 * See {@link com.play4jpa.jpa.query.Query} for details.
 *
 * @param <I> Type of entity ID
 * @param <T> Type of queried entity
 * @author Jens (mail@jensjaeger.com)
 * @author rosem
 */
public final class Finder<I, T extends Model<T>> {

    /**
     * {@link java.lang.Class} of queried entity.
     */
    private final Class<T> entityClass;

    /**
     * Create a new finder for the given ID and entity class.
     *
     * @param idClass     Class of entity ID
     * @param entityClass Class of queried entity
     */
    @SuppressWarnings("unused")
    public Finder(Class<I> idClass, Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Get all entities.
     *
     * @return List with all entities
     */
    public List<T> all() {
        return query().findList();
    }

    /**
     * Get first entity.
     *
     * @return First entity
     */
    public T first() {
        return query().setMaxRows(1).findUnique();
    }

    /**
     * Get the current number of entities.
     *
     * @return Number of entities
     */
    public long count() {
        return query().findRowCount();
    }

    /**
     * Get an entity by its ID.
     *
     * @param id ID of entity to get
     * @return Entity for ID or null
     */
    public T byId(I id) {
        return query().byId(id);
    }

    /**
     * Get a new query to add more complex restrictions.
     *
     * @return New query
     */
    public Query<T> query() {
        return new DefaultQuery<>(entityClass);
    }
}
