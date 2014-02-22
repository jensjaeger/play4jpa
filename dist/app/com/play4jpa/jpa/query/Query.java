package com.play4jpa.jpa.query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;

import java.util.Collection;
import java.util.List;

/**
 * Play Ebean like interface of a Query object for Hibernate.
 *
 * @param <T> Type of queried entity
 * @author Jens (mail@jensjaeger.com)
 * @author rosem
 */
public interface Query<T> {

    /**
     * Add equality constraint for single field value (field = value).
     *
     * @param field Field to constrain
     * @param value Constraint value
     * @return this (for method chaining)
     */
    Query<T> eq(String field, Object value);

    /**
     * Add equality constraint between two fields (field1 = field2).
     *
     * @param field1 First Field
     * @param field2 Second Field
     * @return this (for method chaining)
     */
    Query<T> eqProperty(String field1, String field2);

    /**
     * Add <b>case-insensitive</b> equality constraint for single field value (field = value).
     *
     * @param field Field to constrain
     * @param value Constraint value
     * @return this (for method chaining)
     */
    Query<T> ieq(String field, String value);

    /**
     * Add in-equality constraint for single field value (field != value).
     *
     * @param field Field to constrain
     * @param value Constraint value
     * @return this (for method chaining)
     */
    Query<T> ne(String field, Object value);

    /**
     * Add in-equality constraint for two fields (field1 != field2).
     *
     * @param field1 First Field
     * @param field2 Second Field
     * @return this (for method chaining)
     */
    Query<T> neProperty(String field1, String field2);

    /**
     * Add case-insensitive LIKE constraint for single field value.
     *
     * @param field Field to constrain
     * @param value Constraint value
     * @return this (for method chaining)
     */
    Query<T> ilike(String field, String value);

    /**
     * Add greater-or-equal constraint for single field value (field >= value).
     *
     * @param field Field to constrain
     * @param value Constraint value
     * @return this (for method chaining)
     */
    Query<T> ge(String field, Object value);

    /**
     * Add greater-or-equal constraint for two fields (field1 >= field2).
     *
     * @param field1 First Field
     * @param field2 Second Field
     * @return this (for method chaining)
     */
    Query<T> geProperty(String field1, String field2);

    /**
     * Add greater-than constraint for single field value (field > value).
     *
     * @param field Field to constrain
     * @param value Constraint value
     * @return this (for method chaining)
     */
    Query<T> gt(String field, Object value);

    /**
     * Add greater-than constraint for two fields (field1 > field2).
     *
     * @param field1 First Field
     * @param field2 Second Field
     * @return this (for method chaining)
     */
    Query<T> gtProperty(String field1, String field2);

    /**
     * Add lower-or-equal constraint for single field value (field <= value).
     *
     * @param field Field to constrain
     * @param value Constraint value
     * @return this (for method chaining)
     */
    Query<T> le(String field, Object value);

    /**
     * Add lower-or-equal constraint for two fields (field1 <= field2).
     *
     * @param field1 First Field
     * @param field2 Second Field
     * @return this (for method chaining)
     */
    Query<T> leProperty(String field1, String field2);

    /**
     * Add lower-than constraint for single field value (field < value).
     *
     * @param field Field to constrain
     * @param value Constraint value
     * @return this (for method chaining)
     */
    Query<T> lt(String field, Object value);

    /**
     * Add lower-than constraint for two fields (field1 < field2).
     *
     * @param field1 First Field
     * @param field2 Second Field
     * @return this (for method chaining)
     */
    Query<T> ltProperty(String field1, String field2);

    /**
     * Add BETWEEN constraint for single field value (lo <= field <= hi).
     *
     * @param field Field to constrain
     * @param lo    Lower bound
     * @param hi    Upper bound
     * @return this (for method chaining)
     */
    Query<T> between(String field, Object lo, Object hi);

    /**
     * Add IS NULL constraint for field.
     *
     * @param field Field to constrain
     * @return this (for method chaining)
     */
    Query<T> isNull(String field);

    /**
     * Add IS NOT NULL constraint for field.
     *
     * @param field Field to constrain
     * @return this (for method chaining)
     */
    Query<T> isNotNull(String field);

    /**
     * Add disjunction of given predicates as constraint.
     *
     * @param predicates Predicates to form disjunction of
     * @return this (for method chaining)
     */
    Query<T> or(Criterion... predicates);

    /**
     * Add conjunction of given predicates as constraint.
     *
     * @param predicates Predicates to form conjunction of
     * @return this (for method chaining)
     */
    Query<T> and(Criterion... predicates);

    /**
     * Add IN constraint for single field value (field = value1 || field = value2 ...).
     *
     * @param field  Field to constrain
     * @param values Constraint values
     * @return this (for method chaining)
     */
    Query<T> in(String field, Collection<?> values);

    /**
     * Add IN constraint for single field value by selecting the possible values via a sub-query.
     * <p/>
     * The value of <b>field</b> must match one of the values of <b>subField</b> in the results
     * returned by <b>subQuery</b>.
     *
     * @param field    Field to constrain
     * @param subQuery Query to determine sub-results
     * @param subField Field to get values from
     * @return this (for method chaining)
     */
    Query<T> in(String field, Query<?> subQuery, String subField);

    /**
     * Add NOT IN constraint for single field value (field != value1 && field != value2 ...).
     *
     * @param field  Field to constrain
     * @param values Constraint values
     * @return this (for method chaining)
     */
    Query<T> notIn(String field, Collection<?> values);

    /**
     * ADD NOT IN constraint for single field value by selecting the excluded values via a sub-query.
     * <p/>
     * The value of <b>field</b> must not match any of the values of <b>subField</b> in the results
     * returned by <b>subQuery</b>.
     *
     * @param field    Field to constrain
     * @param subQuery Query to determine sub-results
     * @param subField Field to get values from
     * @return this (for method chaining)
     */
    Query<T> notIn(String field, Query<?> subQuery, String subField);

    /**
     * Add a inner join on the given association (entity role).
     *
     * @param association Name of the association
     * @return this (for method chaining)
     */
    Query<T> join(String association);

    /**
     * Add a left outer join on the given association (entity role).
     *
     * @param association Name of the association
     * @return this (for method chaining)
     */
    Query<T> leftJoin(String association);

    /**
     * Order query ascending by field.
     *
     * @param field Field to order by
     * @return this (for method chaining)
     */
    Query<T> orderByAsc(String field);

    /**
     * Order query descending by field.
     *
     * @param field Field to order by
     * @return this (for method chaining)
     */
    Query<T> orderByDesc(String field);

    /**
     * Get the result count of the current query.
     * <p/>
     * <b>Warning: Adding an order-by clause to the query will potentially slow down the count.</b>
     *
     * @return Number of results
     */
    long findRowCount();

    /**
     * Get the number of distinct values for a given field.
     *
     * @param field Field to count distinct values of
     * @return Number of distinct values
     */
    long findDistinctRowCount(String field);

    /**
     * Get entity for given ID.
     *
     * @param id ID of entity to get
     * @return Entity for ID or null
     */
    T byId(Object id);

    /**
     * Get entity by employing a natural ID.
     *
     * @param field Field to use for natural ID
     * @param id    ID
     * @return Entity for natural ID
     */
    T byNaturalId(String field, Object id);

    /**
     * Find unique entity for current query.
     *
     * @return Unique Entity
     */
    T findUnique();

    /**
     * Find all entities matching the current query.
     *
     * @return All matching Entities
     */
    List<T> findList();

    /**
     * Find all entities matching the current query but only for the given page and pageSize.
     * <p/>
     * <b>Will override any values set by {@link #setFirstResult(int)} and {@link #setMaxRows(int)}!</b>
     *
     * @param page     Page to get Entities for
     * @param pageSize Size of a single page
     * @return All matching Entities on requested page
     */
    List<T> findPage(int page, int pageSize);

    /**
     * Find all entities matching the current query and return them in a paged fashion.
     * See {@link com.play4jpa.jpa.query.PagedQueryIterator} for details.
     * <p/>
     * This is the same as using {@link #findPagedIterator(int, int)} with <b>startPage</b>=1 and <b>pageSize</b>.
     * <p/>
     * <b>Will override any values set by {@link #setFirstResult(int)} and {@link #setMaxRows(int)}!</b>
     *
     * @param pageSize Size of a single page
     * @return Paged iterator
     */
    PagedQueryIterator<T> findPagedIterator(int pageSize);

    /**
     * Find all entities matching the current query and return them in a paged fashion starting with startPage.
     * See {@link com.play4jpa.jpa.query.PagedQueryIterator} for details.
     * <p/>
     * <b>Will override any values set by {@link #setFirstResult(int)} and {@link #setMaxRows(int)}!</b>
     *
     * @param startPage Page to start with
     * @param pageSize  Size of a single page
     * @return Paged iterator
     */
    PagedQueryIterator<T> findPagedIterator(int startPage, int pageSize);

    /**
     * SELECT MAX(field) FROM table_name;
     *
     * This method only works for int fields at the moment.
     *
     * @param field
     * @return the MAX value of the given field in query
     */
    public int findMaxValue(String field);

    /**
     * Get the current criteria to access them directly.
     *
     * @return Current criteria
     */
    DetachedCriteria getCriteria();

    /**
     * Get the current offset of the first result to get.
     *
     * @return Offset of first result
     */
    int getFirstResult();

    /**
     * Set the offset of the first result to get.
     *
     * @param firstResult Offset of first result
     * @return this (for method chaining)
     */
    Query<T> setFirstResult(int firstResult);

    /**
     * Get the current maximum of returned rows.
     *
     * @return Maximum rows to return
     */
    int getMaxRows();

    /**
     * Set the maximum of returned rows.
     *
     * @param maxRows Maximum number of rows
     * @return this (for method chaining)
     */
    Query<T> setMaxRows(int maxRows);
}
