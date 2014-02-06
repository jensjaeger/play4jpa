package play.ext.jj.jpa.models;

import play.ext.jj.jpa.db.Db;
import play.ext.jj.jpa.query.Finder;
import play.ext.jj.jpa.query.Query;
import play.ext.jj.jpa.query.QueryProxy;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * GenericModel includes all helpers for model CRUD operations with JPA.
 *
 * @author Jens (mail@jensjaeger.com)
 */
@MappedSuperclass
public abstract class GenericModel<T extends GenericModel<T>> implements QueryProxy<T>, Serializable {
    private static final long serialVersionUID = 6732017982247391693L;

    private static final play.Logger.ALogger log = play.Logger.of(GenericModel.class);


    /**
     * Last change date of this entity
     */
    public Date lastUpdate;

    /**
     * Update this Model.
     */
    public final void update() {
        if (!play.db.jpa.JPA.em().contains(this)) {
            throw new IllegalStateException("Object not in persistence context! Call save for new objects");
        }
        log.trace("Running preUpdate() on: " + this.toString());
        preUpdate();
        log.trace("preUpdate() finished");
        Db.em().merge(this);
        Db.setCommitNeeded();
    }

    /**
     * Insert a new Model or update an existing one.
     */
    public final void save() {
        if (play.db.jpa.JPA.em().contains(this)) {
            throw new IllegalStateException("Object already in persistence context! Call update for existing objects");
        }
        log.trace("Running preSave() on: " + this.toString());
        preSave();
        log.trace("preSave() finished");
        Db.em().persist(this);
        Db.setCommitNeeded();
    }

    /**
     * Delete this Model.
     */
    public final void delete() {
        play.db.jpa.JPA.em().remove(this);
        Db.setCommitNeeded();
    }


    /**
     * Refresh this entity with data from the database.<br>
     */
    public final void refresh() {
        Db.em().refresh(this);
    }

    /**
     * Reverts any modifications that were made to this managed entity.<br>
     * Call this method if you want to prevent the auto commit at the end of a controller to persist modifications to
     * this object.<br>
     * Used by the versioning system.
     */
    public final void revert() {
        Db.em().refresh(this);
    }

    /**
     * Called before an existing model is updated in the database.
     */
    protected void preUpdate() {
        lastChangeValues();
    }

    /**
     * Called before a new model is inserted into the database.
     */
    protected void preSave() {
        lastChangeValues();
    }

    private void lastChangeValues() {
        lastUpdate = new Date();
    }

    /**
     * Prepares a proxied query
     */
    @Override
    public final void prepareProxiedQuery(Query<T> query) {
        addQueryRestrictions(query);
    }

    /**
     * Adds security/visibility restrictions to the given query.<br>
     * Override this method in subclasses to implement model specific restrictions.
     *
     * @param query {@link Query}
     */
    protected void addQueryRestrictions(Query<T> query) {
    }

    /**
     * Returns a {@link Finder} instance for the given entityClass.
     *
     * @param entityClass Entity class (subclass of GenericModel)
     * @param idType Type of the id field
     * @return {@link Finder}
     */
    private static final <T extends GenericModel<T>, ID> Finder<T, ID> finder(Class<T> entityClass, Class<ID> idType) {
        return Finder.get(entityClass, idType);
    }

    /**
     * Returns a {@link Query} instance for the given entityClass.
     *
     * @param entityClass Entity class (subclass of GenericModel)
     * @param idType Type of the id field
     * @return {@link Query}
     */
    protected static final <T extends GenericModel<T>, ID> Query<T> query(Class<T> entityClass, Class<ID> idType) {
        return finder(entityClass, idType).query();
    }
}
