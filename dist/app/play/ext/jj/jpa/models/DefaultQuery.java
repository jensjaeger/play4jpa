package play.ext.jj.jpa.models;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.ejb.HibernateEntityManager;
import play.db.jpa.JPA;
import play.ext.jj.jpa.query.PagedQueryIterator;
import play.ext.jj.jpa.query.Query;
import play.ext.jj.jpa.query.QueryProxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Default Implementation of the {@link play.ext.jj.jpa.query.Query} interface.
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
     * {@link java.lang.Class} of queried entity.
     */
    private final Class<T> entityClass;

    /**
     * Optional {@link play.ext.jj.jpa.query.QueryProxy}.
     */
    private final QueryProxy<T> proxy;

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
    }

    @Override
    public Query<T> eq(String field, Object value) {
        criteria.add(Restrictions.eq(field, value));
        return this;
    }

    @Override
    public Query<T> eqProperty(String field1, String field2) {
        criteria.add(Restrictions.eqProperty(field1, field2));
        return this;
    }

    @Override
    public Query<T> ieq(String field, String value) {
        criteria.add(Restrictions.eq(field, value).ignoreCase());
        return this;
    }

    @Override
    public Query<T> ne(String field, Object value) {
        criteria.add(Restrictions.ne(field, value));
        return this;
    }

    @Override
    public Query<T> neProperty(String field1, String field2) {
        criteria.add(Restrictions.neProperty(field1, field2));
        return this;
    }

    @Override
    public Query<T> ilike(String field, String value) {
        criteria.add(Restrictions.ilike(field, value));
        return this;
    }

    @Override
    public Query<T> ge(String field, Object value) {
        criteria.add(Restrictions.ge(field, value));
        return this;
    }

    @Override
    public Query<T> geProperty(String field1, String field2) {
        criteria.add(Restrictions.geProperty(field1, field2));
        return this;
    }

    @Override
    public Query<T> gt(String field, Object value) {
        criteria.add(Restrictions.gt(field, value));
        return this;
    }

    @Override
    public Query<T> gtProperty(String field1, String field2) {
        criteria.add(Restrictions.gtProperty(field1, field2));
        return this;
    }

    @Override
    public Query<T> le(String field, Object value) {
        criteria.add(Restrictions.le(field, value));
        return this;
    }

    @Override
    public Query<T> leProperty(String field1, String field2) {
        criteria.add(Restrictions.leProperty(field1, field2));
        return this;
    }

    @Override
    public Query<T> lt(String field, Object value) {
        criteria.add(Restrictions.lt(field, value));
        return this;
    }

    @Override
    public Query<T> ltProperty(String field1, String field2) {
        criteria.add(Restrictions.ltProperty(field1, field2));
        return this;
    }

    @Override
    public Query<T> between(String field, Object lo, Object hi) {
        criteria.add(Restrictions.between(field, lo, hi));
        return this;
    }

    @Override
    public Query<T> isNull(String field) {
        criteria.add(Restrictions.isNull(field));
        return this;
    }

    @Override
    public Query<T> isNotNull(String field) {
        criteria.add(Restrictions.isNotNull(field));
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
            criteria.add(Restrictions.in(field, splitIterator.next()));
        }

        return this;
    }

    @Override
    public Query<T> in(String field, Query<?> subQuery, String subField) {
        DetachedCriteria subQueryCriteria = criteriaForSubQuery(subQuery, Projections.property(subField));
        criteria.add(Subqueries.propertyIn(field, subQueryCriteria));
        return this;
    }

    @Override
    public Query<T> notIn(String field, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("At least one value must be present");
        }

        SplitCollectionIterator<?> splitIterator = new SplitCollectionIterator<>(MAX_IN_SIZE, values);
        while (splitIterator.hasNext()) {
            criteria.add(Restrictions.not(Restrictions.in(field, splitIterator.next())));
        }
        return null;
    }

    @Override
    public Query<T> notIn(String field, Query<?> subQuery, String subField) {
        DetachedCriteria subQueryCriteria = criteriaForSubQuery(subQuery, Projections.property(subField));
        criteria.add(Subqueries.propertyNotIn(field, subQueryCriteria));
        return this;
    }

    @Override
    public Query<T> orderByAsc(String field) {
        criteria.addOrder(Order.asc(field));
        return this;
    }

    @Override
    public Query<T> orderByDesc(String field) {
        criteria.addOrder(Order.desc(field));
        return this;
    }

    @Override
    public long findRowCount() {
        criteria.setProjection(Projections.rowCount());
        Long count = (Long) executablePlainCriteria().uniqueResult();
        criteria.setProjection(null);
        return count.longValue();
    }

    @Override
    public long findDistinctRowCount(String field) {
        criteria.setProjection(Projections.countDistinct(field));
        Long count = (Long) executablePlainCriteria().uniqueResult();
        criteria.setProjection(null);
        return count.longValue();
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
