package com.play4jpa.jpa.query;

import org.hibernate.Criteria;

/**
 * Interface for a proxy that gets called every time when a new {@link com.play4jpa.jpa.query.Query} is created. The proxy class can prepare
 * the query and add some security restrictions.
 */
public interface QueryProxy<T> {

    /**
     * Prepares a new {@link com.play4jpa.jpa.query.Query}.
     *
     * @param query New {@link com.play4jpa.jpa.query.Query} object.
     */
    public void prepareQuery(Query<T> query);

    /**
     * Last callback before the criteria query is finally executed.
     *
     * @param executableCriteria Executable criteria
     */
    void preExecute(Criteria executableCriteria);
}
