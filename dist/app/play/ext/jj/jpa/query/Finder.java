package play.ext.jj.jpa.query;

import java.util.List;

/**
 * Finder implementation for hibernate (similar to ebean Finder).
 *
 * @param <T> Entity type
 * @param <ID> Type of the identifier/primary key field
 *
 * @author Jens (mail@jensjaeger.com)
 */
public final class Finder<T, ID> {
    private final Class<T> type;
    /**
     * Optional proxy for new query objects
     */
    private final QueryProxy<T> queryProxy;
    /**
     * Counter for unique alias names.
     */
    private static int aliasCounter = 1;

    private Finder(Class<T> type, QueryProxy<T> queryProxy) {
        this.type = type;
        this.queryProxy = queryProxy;
    }

    /**
     * Returns a {@link Finder} instance for the given entity class with security restrictions.
     *
     * @return {@link Finder} instance
     */
    public static <T extends QueryProxy<T>, ID> Finder<T, ID> get(Class<T> type,
                                                                  Class<ID> idType) {
        try {
            QueryProxy<T> queryProxy = type.newInstance();
            return new Finder<>(type, queryProxy);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not instantiate QueryProxy from class: " + type.getName());
        }
    }

    /**
     * Returns a {@link Finder} instance for the given entity class with no restrictions.
     *
     * @return {@link Finder} instance
     */
    public static <T, ID> Finder<T, ID> getUnproxied(Class<T> type, Class<ID> idType) {
        return new Finder<T, ID>(type, null);
    }

    public List<T> all() {
        return query().findList();
    }

    public T first() {
        return query().setMaxRows(1).findUnique();
    }

    public List<T> findList() {
        return all();
    }

    public long findRowCount() {
        return query().findRowCount();
    }

    public Query<T> where() {
        return query();
    }

    public T byId(ID id) {
        return query().byId(id);
    }

    public Query<T> query() {
        final String alias = generateAlias(this.type);
        return query(alias);
    }

    public Query<T> query(String alias) {
        Query<T> query = new Query<T>(this.type, alias);
        if (queryProxy != null) {
            queryProxy.prepareProxiedQuery(query);
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

}
