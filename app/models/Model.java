package models;

import customplay.db.Db;
import play.db.jpa.JPA;

import javax.persistence.MappedSuperclass;

/**
 * BaseModel contains generic JPA methods.
 *
 * @author Jens (mail@jensjaeger.com)
 */
@MappedSuperclass
public class Model {

    /**
     * Update this Model.
     */
    public final void update() {
        if (!JPA.em().contains(this)) {
            throw new IllegalStateException("Object not in persistence context! Call save for new objects");
        }
        JPA.em().merge(this);
        Db.setCommitNeeded();
    }

    /**
     * Insert a new Model or update an existing one.
     */
    public final void save() {
        if (JPA.em().contains(this)) {
            throw new IllegalStateException("Object already in persistence context! Call update for existing objects");
        }
        JPA.em().persist(this);
        Db.setCommitNeeded();
    }

    /**
     * Removes this Model from the database.
     */
    public final void delete() {
        play.db.jpa.JPA.em().remove(this);
        Db.setCommitNeeded();
    }
}
