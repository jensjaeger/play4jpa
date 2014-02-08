package play.ext.jj.jpa.models;

import play.ext.jj.jpa.query.LegacyQuery;
import play.ext.jj.jpa.query.QueryProxy;

import java.util.List;

/**
 * Finder implementation for hibernate (similar to ebean Finder).
 *
 * @param <I> Type of the identifier/primary key field
 * @param <T> Entity type
 *
 * @author Jens (mail@jensjaeger.com)
 */
public final class Finder<I, T> implements Query<T> {

    /**
     * Entity type.
     */
    private final Class<T> type;

    /**
     * Optional proxy for new query objects
     */
    private final QueryProxy<T> queryProxy;

    /**
     * Counter for unique alias names.
     */
    private static int aliasCounter = 1;

    public Finder(Class<T> type, QueryProxy<T> queryProxy) {
        this.type = type;
        this.queryProxy = queryProxy;
    }

    public List<T> all() {
        return query().findList();
    }

    public T first() {
        return query().setMaxRows(1).findUnique();
    }

    public long count() {
        return query().findRowCount();
    }

    public LegacyQuery<T> where() {
        return query();
    }

    public T byId(I id) {
        return query().byId(id);
    }

    public LegacyQuery<T> query() {
        final String alias = generateAlias(this.type);
        return query(alias);
    }

    public LegacyQuery<T> query(String alias) {
        LegacyQuery<T> query = new LegacyQuery<>(this.type, alias);
        if (queryProxy != null) {
            queryProxy.prepareQuery(query);
        }
        return query;
    }

    /**
     * Generates a unique alias name for the given entity type.
     *
     * @param type Entity type
     * @return The generated alias name
     */
    private static String generateAlias(Class<?> type) {
        final int counter;
        synchronized (Finder.class) {
            counter = aliasCounter++;
            if (aliasCounter > 1000000) {
                aliasCounter = 1;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(type.getSimpleName());
        sb.append("_");
        sb.append(counter);
        return sb.toString();
    }



    /**
     * Returns a {@link Finder} instance for the given entity class and uses the entity as {@link QueryProxy}.
     *
     * @param type Entity Type
     * @param idType Primary Key (@Id) Type
     * @return {@link Finder} instance
     */
    /*public static <T extends QueryProxy<T>, ID> Finder<T, ID> get(Class<T> type, Class<ID> idType) {
        try {
            QueryProxy<T> queryProxy = type.newInstance();
            return new Finder<>(type, queryProxy);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not instantiate QueryProxy from class: " + type.getName());
        }
    }*/

    /**
     * Create a {@link Finder} instance for the given entity class without employing a proxy.
     *
     * @param type Entity Type
     * @param idType Primary Key (@Id) Type
     * @return {@link Finder} instance
     */
    /*public static <T, ID> Finder<T, ID> getUnproxied(Class<T> type, Class<ID> idType) {
        return new Finder<T, ID>(type, null);
    }*/
}
