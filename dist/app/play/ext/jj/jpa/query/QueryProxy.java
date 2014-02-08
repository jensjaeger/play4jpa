package play.ext.jj.jpa.query;

/**
 * Interface for a proxy that gets called every time when a new {@link LegacyQuery} is created. The proxy class can prepare
 * the query and add some security restrictions.
 */
public interface QueryProxy<T> {

    /**
     * Prepares a new {@link LegacyQuery}.
     *
     * @param query New {@link LegacyQuery} object.
     */
    public void prepareQuery(LegacyQuery<T> query);
}
