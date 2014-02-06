package play.ext.jj.jpa.models;

import play.ext.jj.jpa.query.Query;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Adds a Long generatedValue id to the model.
 *
 * @author Jens (mail@jensjaeger.com)
 */
@MappedSuperclass
public abstract class Model<T extends Model<T>> extends GenericModel<T> {

    @Id
    @GeneratedValue
    public Long id;

    protected static final <T extends GenericModel<T>> Query<T> query(Class<T> entityClass) {
        return query(entityClass, Long.class);
    }
}
