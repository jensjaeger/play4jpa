package play.ext.jj.jpa.query;

/**
 * Interface for a proxy that gets called every time when a new {@link Query} is created. The proxy class can prepare
 * the query and add some security restrictions.
 */
public interface QueryProxy<T> {

    /**
     * Prepares a new {@link Query}.
     *
     * @param query New {@link Query} object.
     */
    public void prepareProxiedQuery(Query<T> query);
}
