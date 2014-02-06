package query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.sql.JoinType;
import org.hibernate.type.TypeResolver;
import play.db.jpa.JPA;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;

/**
 * Query on a specific entity class.
 *
 * @param <T> Entity type
 *
 * @author Jens (mail@jensjaeger.com)*
 */
public class Query<T> implements Iterable<T> {
    /**
     * Maximum number of elements in IN statement, before another in statement is added to the query.
     */
    private static final int IN_LIMIT = 500;
    /**
     * Default page size when iterating over the query result elements.
     */
    private static final int DEFAULT_ITERATOR_PAGE_SIZE = 100;

    private final Class<T> type;
    private final String alias;
    private final String aliasDot;
    private final DetachedCriteria detachedCriteria;
    private final Set<String> aliases = new HashSet<>();

    /**
     * Set by {@link #setFirstResult(int)}.
     */
    private int firstResult;
    /**
     * Set by {@link #setMaxRows(int)}.
     */
    private int maxResults;
    /**
     * True as soon as an order by is added to the query.<br>
     * Used internally to prevent from row counting on invalid statements
     */
    private boolean orderAdded;

    public Query(Class<T> type, String alias) {
        this(type, alias, DetachedCriteria.forClass(type, alias));
    }

    protected Query(Query<T> query) {
        this(query.type, query.alias, query.detachedCriteria);
    }

    protected Query(Class<T> type, String alias, DetachedCriteria criteria) {
        this.type = type;
        this.alias = alias;
        this.aliasDot = alias + ".";
        this.detachedCriteria = criteria;
        this.maxResults = 0;
    }

    @SuppressWarnings("unchecked")
    public final T byId(Object id) {
        detachedCriteria.add(Restrictions.idEq(id));
        return (T) executableCriteria().uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public final T byNaturalId(String field, Object id) {
        detachedCriteria.add(Restrictions.naturalId().set(field, id));
        return (T) executableCriteria().uniqueResult();
    }

    /**
     * Adds an equal restriction
     *
     * @param field The field name
     * @param value The value to compare
     * @return this (for method chaining)
     */
    public final Query<T> eq(String field, Object value) {
        detachedCriteria.add(Restrictions.eq(field, value));
        return this;
    }

    public final Query<T> eq(String field, Property value) {
        detachedCriteria.add(Restrictions.eqProperty(field, value.fullname));
        return this;
    }

    /**
     * Adds a case indifferent string equal restriction
     *
     * @param field The field name
     * @param value The value to compare
     * @return this (for method chaining)
     */
    public final Query<T> ieq(String field, String value) {
        detachedCriteria.add(Restrictions.eq(field, value).ignoreCase());
        return this;
    }

    /**
     * Adds a not equal restriction
     *
     * @param field The field name
     * @param value The value to compare
     * @return this (for method chaining)
     */
    public final Query<T> ne(String field, Object value) {
        detachedCriteria.add(Restrictions.ne(field, value));
        return this;
    }

    public final Query<T> ne(String field, Property value) {
        detachedCriteria.add(Restrictions.neProperty(field, value.fullname));
        return this;
    }

    /**
     * Adds a case indifferent like restriction
     *
     * @param field The field name
     * @param value The value to compare
     * @return this (for method chaining)
     */
    public final Query<T> ilike(String field, String value) {
        detachedCriteria.add(Restrictions.ilike(field, value));
        return this;
    }

    /**
     * Adds a greater or equal restriction
     *
     * @param field The field name
     * @param value The value to compare
     * @return this (for method chaining)
     */
    public final Query<T> ge(String field, Object value) {
        detachedCriteria.add(Restrictions.ge(field, value));
        return this;
    }

    public final Query<T> ge(String field, Property value) {
        detachedCriteria.add(Restrictions.geProperty(field, value.fullname));
        return this;
    }

    /**
     * Adds a greater than restriction
     *
     * @param field The field name
     * @param value The value to compare
     * @return this (for method chaining)
     */
    public final Query<T> gt(String field, Object value) {
        detachedCriteria.add(Restrictions.gt(field, value));
        return this;
    }

    public final Query<T> gt(String field, Property value) {
        detachedCriteria.add(Restrictions.gtProperty(field, value.fullname));
        return this;
    }

    /**
     * Adds a lesser or equal restriction
     *
     * @param field The field name
     * @param value The value to compare
     * @return this (for method chaining)
     */
    public final Query<T> le(String field, Object value) {
        detachedCriteria.add(Restrictions.le(field, value));
        return this;
    }

    public final Query<T> le(String field, Property value) {
        detachedCriteria.add(Restrictions.leProperty(field, value.fullname));
        return this;
    }

    /**
     * Adds a lesser than restriction
     *
     * @param field The field name
     * @param value The value to compare
     * @return this (for method chaining)
     */
    public final Query<T> lt(String field, Object value) {
        detachedCriteria.add(Restrictions.lt(field, value));
        return this;
    }

    public final Query<T> lt(String field, Property value) {
        detachedCriteria.add(Restrictions.ltProperty(field, value.fullname));
        return this;
    }

    /**
     * Adds a between restriction
     *
     * @param field
     * @param lo
     * @param hi
     * @return this (for method chaining)
     */
    public final Query<T> between(String field, Object lo, Object hi) {
        if (lo instanceof Property || hi instanceof Property) {
            throw new IllegalArgumentException("Properties are not allowed as arguments for 'between'!");
        }
        detachedCriteria.add(Restrictions.between(field, lo, hi));
        return this;
    }

    /**
     * Adds is null restriction
     *
     * @param field The field name
     * @return this (for method chaining)
     */
    public final Query<T> isNull(String field) {
        detachedCriteria.add(Restrictions.isNull(field));
        return this;
    }

    public final Query<T> isNull(Property field) {
        detachedCriteria.add(Restrictions.isNull(field.fullname));
        return this;
    }

    /**
     * Adds is not null restriction
     *
     * @param field The field name
     * @return this (for method chaining)
     */
    public final Query<T> isNotNull(String field) {
        detachedCriteria.add(Restrictions.isNotNull(field));
        return this;
    }

    public final Query<T> isNotNull(Property field) {
        detachedCriteria.add(Restrictions.isNotNull(field.fullname));
        return this;
    }

    /**
     * Adds an native sql restriction to the query.
     *
     * @deprecated This method should not be used due to native SQL. Use the criteria api provided by this {@link Query}
     *             class.
     * @param sql Native sql to add
     * @param parameters Parameters for the native sql
     * @return this (for method chaining)
     */
    @Deprecated
    public final Query<T> sqlRestriction(String sql, Object... parameters) {
        if (parameters.length > 0) {
            Object[] values = parameters;
            org.hibernate.type.Type[] types = new org.hibernate.type.Type[parameters.length];
            for (int i = 0; i < parameters.length; ++i) {
                types[i] = new TypeResolver().basic(values.getClass().getName());
            }
            detachedCriteria.add(Restrictions.sqlRestriction(sql, values, types));
        }
        else {
            detachedCriteria.add(Restrictions.sqlRestriction(sql));
        }
        return this;
    }

    public final Query<T> or(Criterion... predicates) {
        detachedCriteria.add(Restrictions.or(predicates));
        return this;
    }

    public final Query<T> and(Criterion... predicates) {
        detachedCriteria.add(Restrictions.and(predicates));
        return this;
    }

    public final Query<T> in(String field, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            detachedCriteria.add(Restrictions.sqlRestriction("1=0"));
        }
        else if (values.size() > IN_LIMIT) {
            // Split into multiple "IN" restrictions if there are too many values
            final List<Collection<?>> splittedValues = split(values, IN_LIMIT);
            Disjunction inRestrictions = Restrictions.disjunction();
            for (Collection<?> v : splittedValues) {
                inRestrictions.add(Restrictions.in("id", v));
            }
            detachedCriteria.add(inRestrictions);
        }
        else {
            detachedCriteria.add(Restrictions.in(field, values));
        }
        return this;
    }

    private final Criterion inCriterion(String field, Query<?> subQuery, Projection projection) {
        if (subQuery == this) {
            throw new IllegalArgumentException("Cannot use THIS as subquery!");
        }
        final DetachedCriteria subCriteria = subQuery.getDetachedCriteria();
        if (projection != null) {
            subCriteria.setProjection(projection);
        }
        return Subqueries.propertyIn(field, subQuery.getDetachedCriteria());
    }

    /**
     * Adds an IN restriction on the id field of this query and the sub query.
     *
     * @param subQuery The sub query
     * @return this (for method chaining)
     */
    public final Query<T> in(Query<?> subQuery) {
        detachedCriteria.add(inCriterion("id", subQuery, Projections.id()));
        return this;
    }

    /**
     * Adds an IN restriction on the given field of this query and sub query.
     *
     * @param field Name of the field in this query
     * @param subField Name of the field in the sub query
     * @param subQuery The sub query
     * @return this (for method chaining)
     */
    public final Query<T> in(String field, Query<?> subQuery, String subField) {
        detachedCriteria.add(inCriterion(field, subQuery, Projections.property(subField)));
        return this;
    }

    public final Query<T> notIn(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            if (values.size() > IN_LIMIT) {
                // Split into multiple "NOT IN" restrictions if there are too many values
                final List<Collection<?>> splittedValues = split(values, IN_LIMIT);
                Conjunction restrictions = Restrictions.conjunction();
                for (Collection<?> v : splittedValues) {
                    restrictions.add(Restrictions.not(Restrictions.in("id", v)));
                }
                detachedCriteria.add(restrictions);
            }
            else {
                detachedCriteria.add(Restrictions.not(Restrictions.in(field, values)));
            }
        }
        return this;
    }

    /**
     * Adds an NOT IN restriction on the id field of this query and the sub query.
     *
     * @param subQuery The sub query
     * @return this (for method chaining)
     */
    public final Query<T> notIn(Query<?> subQuery) {
        detachedCriteria.add(Restrictions.not(inCriterion("id", subQuery, Projections.id())));
        return this;
    }

    /**
     * Adds an NOT IN restriction on the given field of this query and sub query.
     *
     * @param field Name of the field in this query
     * @param subField Name of the field in the sub query
     * @param subQuery The sub query
     * @return this (for method chaining)
     */
    public final Query<T> notIn(String field, Query<?> subQuery, String subField) {
        detachedCriteria.add(Restrictions.not(inCriterion(field, subQuery, Projections.property(subField))));
        return this;
    }

    /**
     * Left outer join on the given path.
     *
     * @param associated Path to join
     * @return this (for method chaining)
     */
    public final Query<T> leftJoin(String associated) {
        final String alias = associated.replaceAll("\\.", "_");
        if (!aliases.contains(alias)) {
            detachedCriteria.createAlias(associated, alias, JoinType.LEFT_OUTER_JOIN);
            aliases.add(alias);
        }
        return this;
    }

    /**
     * Inner join on the given path.
     *
     * @param associated Path to join
     * @return this (for method chaining)
     */
    public final Query<T> join(String associated) {
        final String alias = associated.replaceAll("\\.", "_");
        if (!aliases.contains(alias)) {
            detachedCriteria.createAlias(associated, alias);
            aliases.add(alias);
        }
        return this;
    }

    public final Query<T> exists(Query<T> subQuery) {
        if (subQuery == this) {
            throw new IllegalArgumentException("Cannot use THIS as subquery!");
        }
        final DetachedCriteria subCriteria = subQuery.getDetachedCriteria();
        subCriteria.setProjection(Projections.id());
        detachedCriteria.add(Subqueries.exists(subCriteria));
        return this;
    }

    public final Query<T> notExists(Query<T> subQuery) {
        if (subQuery == this) {
            throw new IllegalArgumentException("Cannot use THIS as subquery!");
        }
        final DetachedCriteria subCriteria = subQuery.getDetachedCriteria();
        subCriteria.setProjection(Projections.id());
        detachedCriteria.add(Subqueries.notExists(subCriteria));
        return this;
    }



    @SuppressWarnings("unchecked")
    public final List<T> findList() {
        beforeExecute();
        return executableCriteria().list();
    }

    @SuppressWarnings("unchecked")
    public final T findUnique() {
        beforeExecute();
        return (T) executableCriteria().uniqueResult();
    }

    /**
     * Find count of all rows = count(*).<br>
     * Be aware that this method does count all rows and not the root entity when you join on *ToMany associations.
     */
    public final long findRowCount() {
        if (orderAdded) {
            throw new IllegalStateException("Cannot count rows with order by!");
        }
        beforeExecute();
        detachedCriteria.setProjection(Projections.rowCount());
        final Long result = (Long) executableCriteria().uniqueResult();
        detachedCriteria.setProjection(null);
        return result.longValue();
    }

    /**
     * Find distinct count on given field = count(distinct field).
     *
     * @param field a field of the model
     */
    public final long findDistinctCount(String field) {
        if (orderAdded) {
            throw new IllegalStateException("Cannot count rows with order by!");
        }
        beforeExecute();
        detachedCriteria.setProjection(Projections.countDistinct(field));
        final Long result = (Long) executableCriteria().uniqueResult();
        detachedCriteria.setProjection(null);
        return result.longValue();
    }

    public final <ID> List<ID> findIds() {
        beforeExecute();
        detachedCriteria.setProjection(Projections.id());
        @SuppressWarnings("unchecked")
        final List<ID> result = executableCriteria().list();
        detachedCriteria.setProjection(null);
        return result;
    }

    public final PagedQueryIterator<T> findPagedIterator(int pageSize) {
        return new PagedQueryIterator<T>(this, pageSize);
    }

    @Override
    public final Iterator<T> iterator() {
        return findPagedIterator(DEFAULT_ITERATOR_PAGE_SIZE);
    }

    public final Query<T> orderByAsc(String field) {
        detachedCriteria.addOrder(Order.asc(field));
        this.orderAdded = true;
        return this;
    }

    public final Query<T> orderByDesc(String field) {
        detachedCriteria.addOrder(Order.desc(field));
        this.orderAdded = true;
        return this;
    }

    public final Query<T> setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    public final Query<T> setMaxRows(int rows) {
        this.maxResults = rows;
        return this;
    }

    /**
     * Called before the query is executed.
     */
    protected final void beforeExecute() {
        detachedCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Splits a collection if the number of elements exceeds the given maximum size.
     *
     * @param original Input collection
     * @param maxSize Maximum number of elements in a single collection
     * @return List of split collections
     */
    private static List<Collection<?>> split(Collection<?> original, int maxSize) {
        ArrayList<Collection<?>> result = new ArrayList<Collection<?>>();
        Iterator<?> it = original.iterator();
        int numOfSets = (original.size() - 1) / maxSize + 1;

        // Loop for each new collection.
        for (int i = 0; i < numOfSets; i++) {
            HashSet<Object> s = new HashSet<Object>(maxSize);

            // Loop over each element.
            for (int j = 0; j < maxSize && it.hasNext(); j++) {
                s.add(it.next());
            }
            result.add(s);
        }
        return result;
    }

    /**
     * Extracts the value of the id field of the given objects by use of reflection.
     *
     * @param objects Array with objects that contain a field that is annotated with {@link javax.persistence.Id} or {@link javax.persistence.EmbeddedId}.
     * @return The list of ids of the provided objects or the objects themself in case their type is not an entity.
     */
    private static Object[] objectsToIds(Object[] objects) {
        if (objects == null) {
            return null;
        }
        if (objects.length == 0) {
            return objects;
        }
        final Class<?> entityClazz = objects[0].getClass();
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            return objects;
        }

        // Iterate the class hierarchy
        Class<?> clazz = entityClazz;
        while (clazz != null) {

            // Iterate all fields in class
            for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {

                // Stop at first id field
                if (f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(EmbeddedId.class)) {
                    Object[] result = new Object[objects.length];
                    for (int i = 0; i < objects.length; ++i) {
                        try {
                            result[i] = f.get(objects[i]);
                        }
                        catch (Exception e) {
                            throw new IllegalArgumentException("Could not retrieve value of id field from object: "
                                    + objects[i]);
                        }
                    }
                    return result;
                }
            }

            clazz = clazz.getSuperclass();
        }

        throw new IllegalArgumentException("Given entity class has no id field: " + entityClazz.getName());
    }

    /**
     * Shortcut of {@link #objectsToIds(Object[])} for single objects.
     *
     * @see {@link #objectsToIds(Object[])}
     */
    private static Object objectToId(Object object) {
        return objectsToIds(new Object[] { object })[0];
    }

    public final Class<T> getType() {
        return type;
    }

    public final String getAlias() {
        return alias;
    }

    public final String getAliasDot() {
        return aliasDot;
    }

    /**
     * Class that represents a property/field of a specific query. You can use a object of this class as value in the
     * restriction methods.
     */
    public static class Property {
        public final String fullname;

        public Property(String fullname) {
            this.fullname = fullname;
        }
    }

    /**
     * Returns the given field prefixed with the alias of this query.
     *
     * @return Prefixed field name
     */
    public final Property property(String field) {
        return new Property(aliasDot + field);
    }

    protected final DetachedCriteria getDetachedCriteria() {
        return detachedCriteria;
    }

    protected final Criteria executableCriteria() {
        final Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }
        if (firstResult > 0) {
            criteria.setFirstResult(firstResult);
        }
        return criteria;
    }

    public static Session getSession() {
        HibernateEntityManager hem = JPA.em().unwrap(HibernateEntityManager.class);
        return hem.getSession();
    }

}
