package com.play4jpa.jpa.models;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.play4jpa.jpa.query.PagedQueryIterator;
import com.play4jpa.jpa.query.Query;
import com.play4jpa.jpa.query.QueryProxy;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.sql.JoinType;
import play.db.jpa.JPA;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;

/**
 * Default Implementation of the {@link com.play4jpa.jpa.query.Query} interface.
 *
 * @param <T> Type of queried entity
 * @author Jens (mail@jensjaeger.com)
 * @author rosem
 */
public class DefaultQuery<T> implements Query<T> {

    /**
     * Maximum number of values for one single (not) IN restriction.
     * <p/>
     * If the number of values is greater, multiple (not) IN clauses will be added.
     */
    public static final int MAX_IN_SIZE = 500;

    /**
     * Maximum alias index before reset to 1
     */
    private static final int MAX_ALIAS_INDEX = 9999;

    /**
     * Currently used maximum alias index
     */
    private static int globalAliasIndex = 0;

    /**
     * {@link java.lang.Class} of queried entity.
     */
    private final Class<T> entityClass;

    /**
     * Optional {@link com.play4jpa.jpa.query.QueryProxy}.
     */
    private final QueryProxy<T> proxy;
    /**
     * Alias index for this query
     */
    private final int aliasIndex;
    /**
     * Map of entity class to used alias
     */
    private final LinkedHashMap<String, String> aliases = Maps.newLinkedHashMap();
    /**
     * Indicates whether an ORDER BY clause is present
     */
    private boolean hasOrder = false;
    /**
     * Accumulated criteria for query.
     */
    private DetachedCriteria criteria;
    /**
     * Offset of first result row.
     */
    private int firstResult = 0;

    /**
     * Maximum number of rows to get.
     */
    private int maxRows = 0;

    /**
     * Create a new query for given entity type without a proxy.
     *
     * @param entityClass Queried entity class
     */
    public DefaultQuery(Class<T> entityClass) {
        this(entityClass, null);
    }

    /**
     * Create a new query for given entity type and using given proxy.
     *
     * @param entityClass Queried entity class
     * @param proxy       Proxy to apply additional restrictions
     */
    public DefaultQuery(Class<T> entityClass, QueryProxy<T> proxy) {
        this.entityClass = entityClass;
        this.proxy = proxy;
        this.criteria = DetachedCriteria.forClass(this.entityClass);
        this.aliasIndex = getNewAliasIndex();
    }

    /**
     * Get a new alias index for use in a new query.
     *
     * @return Alias index for query
     */
    private static synchronized int getNewAliasIndex() {
        if (globalAliasIndex == MAX_ALIAS_INDEX) {
            globalAliasIndex = 1;
        } else {
            globalAliasIndex++;
        }
        return globalAliasIndex;
    }

    public final int getAliasIndex() {
        return aliasIndex;
    }

    @Override
    public Query<T> eq(String field, Object value) {
        criteria.add(Restrictions.eq(alialize(field), value));
        return this;
    }

    @Override
    public Query<T> eqProperty(String field1, String field2) {
        criteria.add(Restrictions.eqProperty(alialize(field1), alialize(field2)));
        return this;
    }

    @Override
    public Query<T> ieq(String field, String value) {
        criteria.add(Restrictions.eq(alialize(field), value).ignoreCase());
        return this;
    }

    @Override
    public Query<T> ne(String field, Object value) {
        criteria.add(Restrictions.ne(alialize(field), value));
        return this;
    }

    @Override
    public Query<T> neProperty(String field1, String field2) {
        criteria.add(Restrictions.neProperty(alialize(field1), alialize(field2)));
        return this;
    }

    @Override
    public Query<T> ilike(String field, String value) {
        criteria.add(Restrictions.ilike(alialize(field), value));
        return this;
    }

    @Override
    public Query<T> ge(String field, Object value) {
        criteria.add(Restrictions.ge(alialize(field), value));
        return this;
    }

    @Override
    public Query<T> geProperty(String field1, String field2) {
        criteria.add(Restrictions.geProperty(alialize(field1), alialize(field2)));
        return this;
    }

    @Override
    public Query<T> gt(String field, Object value) {
        criteria.add(Restrictions.gt(alialize(field), value));
        return this;
    }

    @Override
    public Query<T> gtProperty(String field1, String field2) {
        criteria.add(Restrictions.gtProperty(alialize(field1), alialize(field2)));
        return this;
    }

    @Override
    public Query<T> le(String field, Object value) {
        criteria.add(Restrictions.le(alialize(field), value));
        return this;
    }

    @Override
    public Query<T> leProperty(String field1, String field2) {
        criteria.add(Restrictions.leProperty(alialize(field1), alialize(field2)));
        return this;
    }

    @Override
    public Query<T> lt(String field, Object value) {
        criteria.add(Restrictions.lt(alialize(field), value));
        return this;
    }

    @Override
    public Query<T> ltProperty(String field1, String field2) {
        criteria.add(Restrictions.ltProperty(alialize(field1), alialize(field2)));
        return this;
    }

    @Override
    public Query<T> between(String field, Object lo, Object hi) {
        criteria.add(Restrictions.between(alialize(field), lo, hi));
        return this;
    }

    @Override
    public Query<T> isNull(String field) {
        criteria.add(Restrictions.isNull(alialize(field)));
        return this;
    }

    @Override
    public Query<T> isNotNull(String field) {
        criteria.add(Restrictions.isNotNull(alialize(field)));
        return this;
    }

    @Override
    public Query<T> or(Criterion... predicates) {
        criteria.add(Restrictions.or(predicates));
        return this;
    }

    @Override
    public Query<T> and(Criterion... predicates) {
        criteria.add(Restrictions.and(predicates));
        return this;
    }

    @Override
    public Query<T> in(String field, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("At least one value must be present");
        }

        SplitCollectionIterator<?> splitIterator = new SplitCollectionIterator<>(MAX_IN_SIZE, values);
        while (splitIterator.hasNext()) {
            criteria.add(Restrictions.in(alialize(field), splitIterator.next()));
        }

        return this;
    }

    @Override
    public Query<T> in(String field, Query<?> subQuery, String subField) {
        DetachedCriteria subQueryCriteria = criteriaForSubQuery(subQuery, Projections.property(subField));
        criteria.add(Subqueries.propertyIn(alialize(field), subQueryCriteria));
        return this;
    }

    @Override
    public Query<T> notIn(String field, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("At least one value must be present");
        }

        SplitCollectionIterator<?> splitIterator = new SplitCollectionIterator<>(MAX_IN_SIZE, values);
        while (splitIterator.hasNext()) {
            criteria.add(Restrictions.not(Restrictions.in(alialize(field), splitIterator.next())));
        }

        return this;
    }

    @Override
    public Query<T> notIn(String field, Query<?> subQuery, String subField) {
        DetachedCriteria subQueryCriteria = criteriaForSubQuery(subQuery, Projections.property(subField));
        criteria.add(Subqueries.propertyNotIn(alialize(field), subQueryCriteria));
        return this;
    }

    @Override
    public Query<T> join(String association) {
        if (aliases.containsKey(association)) {
            throw new IllegalArgumentException("Already joined on " + association);
        }

        String associationAlias = createAlias(association);
        String alialized = alialize(association);
        criteria.createAlias(alialized, associationAlias);
        return this;
    }

    @Override
    public Query<T> leftJoin(String association) {
        if (aliases.containsKey(association)) {
            throw new IllegalArgumentException("Already joined on " + association);
        }

        String associationAlias = createAlias(association);
        String alialized = alialize(association);
        criteria.createAlias(alialized, associationAlias, JoinType.LEFT_OUTER_JOIN);
        return this;
    }

    @Override
    public Query<T> orderByAsc(String field) {
        hasOrder = true;
        criteria.addOrder(Order.asc(alialize(field)));
        return this;
    }

    @Override
    public Query<T> orderByDesc(String field) {
        hasOrder = true;
        criteria.addOrder(Order.desc(alialize(field)));
        return this;
    }

    @Override
    public long findRowCount() {
        if (hasOrder) {
            throw new IllegalStateException("Cannot count rows when ORDER BY is present");
        }

        criteria.setProjection(Projections.rowCount());
        Long count = (Long) executablePlainCriteria().uniqueResult();
        criteria.setProjection(null);
        return count;
    }

    @Override
    public long findDistinctRowCount(String field) {
        if (hasOrder) {
            throw new IllegalStateException("Cannot count rows when ORDER BY is present");
        }

        criteria.setProjection(Projections.countDistinct(alialize(field)));
        Long count = (Long) executablePlainCriteria().uniqueResult();
        criteria.setProjection(null);
        return count;
    }

    @Override
    public T byId(Object id) {
        criteria.add(Restrictions.idEq(id));
        return findUnique();
    }

    @Override
    public T byNaturalId(String field, Object id) {
        criteria.add(Restrictions.naturalId().set(field, id));
        return findUnique();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T findUnique() {
        return (T) executableEntityCriteria().uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findList() {
        return executableEntityCriteria().list();
    }

    @Override
    public List<T> findPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        setFirstResult(offset);
        setMaxRows(pageSize);

        List<T> result = findList();

        setFirstResult(0);
        setMaxRows(0);
        return result;
    }

    @Override
    public int findMaxValue(String field) {
        criteria.setProjection(Projections.max(field));
        final Integer result = (Integer)executablePlainCriteria().uniqueResult();
        criteria.setProjection(null);
        return result != null ? result.intValue() : 0 ;
    }

    @Override
    public PagedQueryIterator<T> findPagedIterator(int pageSize) {
        return null;
    }

    @Override
    public PagedQueryIterator<T> findPagedIterator(int startPage, int pageSize) {
        return null;
    }

    @Override
    public DetachedCriteria getCriteria() {
        return criteria;
    }

    @Override
    public int getFirstResult() {
        return firstResult;
    }

    @Override
    public Query<T> setFirstResult(int firstResult) {
        if (firstResult < 0) {
            throw new IllegalArgumentException("firstResult must be >=0 (0 for reset)");
        }
        this.firstResult = firstResult;
        return this;
    }

    @Override
    public int getMaxRows() {
        return maxRows;
    }

    @Override
    public Query<T> setMaxRows(int maxRows) {
        if (maxRows < 0) {
            throw new IllegalArgumentException("maxRows must be >=0 (0 for reset)");
        }
        this.maxRows = maxRows;
        return this;
    }

    public final String createAlias(String association) {
        if (aliases.containsKey(association)) {
            throw new IllegalStateException("There is already an alias for " + association);
        }

        String newAlias = association.replace(".", "_") + "_" + aliasIndex;
        aliases.put(association, newAlias);
        return newAlias;
    }

    public final String alialize(String field) {
        if (Strings.isNullOrEmpty(field)) {
            throw new IllegalArgumentException("field must not be empty");
        }

        if (!field.contains(".")) {
            return field;
        }

        if (aliases.containsKey(field)) {
            return aliases.get(field);
        }

        int lastDot = field.lastIndexOf(".");
        String path = field.substring(0, lastDot);
        String name = field.substring(lastDot + 1);
        if (!aliases.containsKey(path)) {
            throw new IllegalStateException("Cannot alialize " + field + ", first join on " + path);
        }

        return aliases.get(path) + "." + name;
    }

    /**
     * Get executable criteria with entity transformer (e.g. for {@link #findUnique()}).
     *
     * @return Executable criteria
     */
    private Criteria executableEntityCriteria() {
        return executableCriteria(true);
    }

    /**
     * Get executable criteria without entity transformer (e.g. for {@link #findRowCount()}).
     *
     * @return Executable criteria
     */
    private Criteria executablePlainCriteria() {
        return executableCriteria(false);
    }

    /**
     * Get an executable criteria from the current detached criteria. If forRootEntity is true, a
     * {@link org.hibernate.transform.DistinctRootEntityResultTransformer} will be added (needed if you want to
     * get entities out of the query).
     *
     * @param forRootEntity True, if entities should be returned by the query
     * @return Executable criteria
     */
    private Criteria executableCriteria(boolean forRootEntity) {
        HibernateEntityManager entityManager = JPA.em().unwrap(HibernateEntityManager.class);
        Session session = entityManager.getSession();
        Criteria executableCriteria = criteria.getExecutableCriteria(session);

        if (forRootEntity) {
            executableCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        }
        if (maxRows > 0) {
            executableCriteria.setMaxResults(maxRows);
        }
        if (firstResult > 0) {
            executableCriteria.setFirstResult(firstResult);
        }
        if (proxy != null) {
            proxy.preExecute(executableCriteria);
        }

        return executableCriteria;
    }

    /**
     * Transform subQuery into detached criteria and apply projection if given.
     *
     * @param subQuery   Sub query to transform (must not be this)
     * @param projection Projection to apply (can be null)
     * @return Detached criteria representing subQuery
     */
    private DetachedCriteria criteriaForSubQuery(Query<?> subQuery, Projection projection) {
        if (subQuery == this) {
            throw new IllegalArgumentException("SubQuery must be different from main query");
        }

        DetachedCriteria subQueryCriteria = subQuery.getCriteria();
        if (projection != null) {
            subQueryCriteria.setProjection(projection);
        }
        return subQueryCriteria;
    }

    /**
     * Helper class for splitting a single collection in multiple collections with smaller size. Useful if you want to
     * apply operations batch-wise.
     *
     * @author rosem
     */
    static class SplitCollectionIterator<T> implements Iterator<Collection<T>> {

        /**
         * Size of smaller collections
         */
        private final int batchSize;

        /**
         * Iterator for given values
         */
        private final Iterator<T> valueIterator;

        /**
         * Number of values in original collection
         */
        private final int numValues;

        /**
         * Number of already processed values
         */
        private int numProcessed = 0;

        /**
         * Create a new collection iterator where each smaller collection has at most batchSize values.
         *
         * @param batchSize Size of smaller collections
         * @param values    Collection to split
         */
        public SplitCollectionIterator(int batchSize, Collection<T> values) {
            this.batchSize = batchSize;
            this.valueIterator = values.iterator();
            this.numValues = values.size();
        }

        @Override
        public boolean hasNext() {
            return numProcessed < numValues;
        }

        @Override
        public Collection<T> next() {
            int nextBatchSize = Math.min(batchSize, numValues);
            List<T> nextBatch = new ArrayList<>(nextBatchSize);

            for (int i = 0; i < nextBatchSize; i++) {
                nextBatch.add(valueIterator.next());
            }

            numProcessed += nextBatchSize;
            return nextBatch;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
