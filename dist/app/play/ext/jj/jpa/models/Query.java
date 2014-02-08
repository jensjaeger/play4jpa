package play.ext.jj.jpa.models;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;
import play.ext.jj.jpa.query.PagedQueryIterator;

import java.util.Collection;
import java.util.List;

public interface Query<T> {

    /**
     * Add equality constraint for single field value (field = value).
     * @param field Field to constrain
     * @param value Constraint value
     * @return
     */
    Query<T> eq(String field, Object value);

    /**
     * Add equality constraint between two fields (field1 = field2).
     * @param field1 First Field
     * @param field2 Second Field
     * @return
     */
    Query<T> eqProperties(String field1, String field2);

    /**
     * Add <b>case-insensitive</b> equality constraint for single field value (field = value).
     * @param field Field to constrain
     * @param value Constraint value
     * @return
     */
    Query<T> ieq(String field, String value);

    /**
     * Add in-equality constraint for single field value (field != value).
     * @param field Field to constrain
     * @param value Constraint value
     * @return
     */
    Query<T> ne(String field, Object value);

    /**
     * Add in-equality constraint for two fields (field1 != field2).
     * @param field1 First Field
     * @param field2 Second Field
     * @return
     */
    Query<T> neProperties(String field1, String field2);

    /**
     * Add case-insensitive LIKE constraint for single field value.
     * @param field Field to constrain
     * @param value Constraint value
     * @return
     */
    Query<T> ilike(String field, String value);

    /**
     * Add greater-or-equal constraint for single field value (field >= value).
     * @param field Field to constrain
     * @param value Constraint value
     * @return
     */
    Query<T> ge(String field, Object value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    Query<T> ge(String field, String value);

    Query<T> geProperties(String field1, String field2);

    Query<T> gt(String field, Object value);

    Query<T> gtProperties(String field1, String field2);

    Query<T> le(String field, Object value);

    Query<T> leProperties(String field1, String field2);

    Query<T> lt(String field, Object value);

    Query<T> ltProperties(String field1, String field2);

    Query<T> between(String field, Object lo, Object hi);

    Query<T> isNull(String field);

    Query<T> isNotNull(String field);

    Query<T> or(Criterion... predicates);

    Query<T> and(Criterion... predicates);

    Query<T> in(String field, Collection<?> values);

    Query<T> in(Query<?> subQuery);

    Query<T> in(String field, Query<?> subQuery, String subField);

    Query<T> notIn(String field, Collection<?> values);

    Query<T> notIn(Query<?> subQuery);

    Query<T> notIn(String field, Query<?> subQuery, String subField);

    Query<T> leftJoin(String associated);

    Query<T> join(String associated);

    Query<T> exists(Query<T> subQuery);

    Query<T> notExists(Query<T> subQuery);

    Query<T> orderByAsc(String field);

    Query<T> orderByDesc(String field);

    long count();

    T findUnique();

    List<T> findList();

    List<T> findPage(int page, int pageSize);

    PagedQueryIterator<T> findPagedIterator(int pageSize);

    PagedQueryIterator<T> findPagedIterator(int startPage, int pageSize);
}
