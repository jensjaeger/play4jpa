package com.play4jpa.jpa.models;

import com.play4jpa.jpa.db.Db;
import com.play4jpa.jpa.query.Query;
import com.play4jpa.jpa.query.QueryProxy;
import org.hibernate.Criteria;
import play.Logger;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Play Ebean like Model base class for Hibernate.
 *
 * @author Jens (mail@jensjaeger.com)
 * @author rosem
 */
@MappedSuperclass
public abstract class Model<T extends Model<T>> implements QueryProxy<T>, Serializable {

    /**
     * Logger instance
     */
    private static final Logger.ALogger log = Logger.of(Model.class);

    /**
     * Update this Model.
     * <p/>
     * Call only if the entity is already contained in JPA entity manager (EM).
     *
     * @throws java.lang.IllegalStateException if the entity is not contained in EM
     */
    public final void update() {
        if (!play.db.jpa.JPA.em().contains(this)) {
            throw new IllegalStateException("Object not in persistence context! Call save for new objects");
        }

        log.trace("Running preUpdate() on: {}", this.toString());
        preUpdate();
        log.trace("preUpdate() finished");
        Db.em().merge(this);
        Db.setCommitNeeded();
    }

    /**
     * Insert a new Model.
     * <p/>
     * Call only if the entity is not contained in JPA entity manager (EM).
     *
     * @throws java.lang.IllegalStateException if entity is already contained in EM
     */
    public final void save() {
        if (play.db.jpa.JPA.em().contains(this)) {
            throw new IllegalStateException("Object already in persistence context! Call update for existing objects");
        }

        log.trace("Running preSave() on: {}", this.toString());
        preSave();
        log.trace("preSave() finished");
        Db.em().persist(this);
        Db.setCommitNeeded();
    }

    /**
     * Delete this Model.
     */
    public final void delete() {
        log.trace("Running preDelete() on: {}", this.toString());
        preDelete();
        log.trace("preDelete() finished", this.toString());

        play.db.jpa.JPA.em().remove(this);
        Db.setCommitNeeded();
    }

    /**
     * Refresh this entity with data from the database.
     */
    public final void refresh() {
        Db.em().refresh(this);
    }

    @Override
    public void prepareQuery(Query<T> query) {
        // Override in subclass
    }

    @Override
    public void preExecute(Criteria executableCriteria) {
        // Override in subclass
    }

    /**
     * Called before an existing model is updated in the database.
     * <p/>
     * Override this method in subclasses if needed.
     */
    protected void preUpdate() {
    }

    /**
     * Called before a new model is inserted into the database.
     * <p/>
     * Override this method in subclasses if needed.
     */
    protected void preSave() {
    }

    /**
     * Called before an existing model is deleted from the database.
     * <p/>
     * Override this method in subclasses if needed.
     */
    protected void preDelete() {
    }
}
