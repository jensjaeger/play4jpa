package play.ext.jj.jpa.models;

import play.Logger;
import play.ext.jj.jpa.db.Db;
import play.ext.jj.jpa.query.LegacyQuery;
import play.ext.jj.jpa.query.QueryProxy;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Model base class similar to Play Ebean Model base class.
 *
 * @author Jens (mail@jensjaeger.com)
 * @author rosem
 */
@MappedSuperclass
public abstract class Model<T extends Model<T>> implements QueryProxy<T>, Serializable {

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
        play.db.jpa.JPA.em().remove(this);
        Db.setCommitNeeded();
    }

    /**
     * Refresh this entity with data from the database.
     */
    public final void refresh() {
        Db.em().refresh(this);
    }

    /**
     * Reverts any modifications that were made to this managed entity.<br>
     * Call this method if you want to prevent the auto commit at the end of a controller to persist modifications to
     * this object.
     */
    public final void revert() {
        Db.em().refresh(this);
    }

    @Override
    public final void prepareQuery(LegacyQuery<T> query) {
        addQueryRestrictions(query);
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
     * Adds security/visibility restrictions to the given query.
     * <p/>
     * Override this method in subclasses to implement model specific restrictions.
     *
     * @param query {@link play.ext.jj.jpa.query.LegacyQuery}
     */
    protected void addQueryRestrictions(LegacyQuery<T> query) {
    }
}
