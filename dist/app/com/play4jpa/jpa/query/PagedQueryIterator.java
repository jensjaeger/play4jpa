package com.play4jpa.jpa.query;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator for a paged result set of a {@link Query}.
 * <p/>
 * This will load entities in batches of the given page size. <b>Be sure to always use {@link #hasNext()} to ensure
 * that there are populated results available.</b>
 *
 * @param <T> Type of queried entity
 * @author Jens (mail@jensjaeger.com)
 * @author rosem
 */
public final class PagedQueryIterator<T> implements Iterator<T> {

    /**
     * Underlying query
     */
    private final Query<T> query;

    /**
     * Number of rows per page
     */
    private final int rowsPerPage;

    /**
     * Number of current page
     */
    private int pageNo;

    /**
     * Results for current page
     */
    private List<T> pageResults;

    /**
     * Iterator for page results
     */
    private Iterator<T> pageResultsIterator;

    /**
     * Create a new paged iterator for wrapping the given query and using the given page size.
     *
     * @param query       Query to page results for
     * @param rowsPerPage Number of results per page
     */
    public PagedQueryIterator(Query<T> query, int rowsPerPage) {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }
        if (rowsPerPage < 1) {
            throw new IllegalArgumentException("rowsPerPage must be >= 1");
        }

        this.query = query;
        this.rowsPerPage = rowsPerPage;
        this.pageNo = 1;
    }

    /**
     * Get current number of rows per page.
     *
     * @return Number of rows per page
     */
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    /**
     * Get current page number.
     *
     * @return Number of current page
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * Set page number to retrieve.
     * <p/>
     * This will reset the current data and fetch the new page once you call {@link #hasNext()}.
     *
     * @param pageNo Requested page number
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
        pageResults = null;
        pageResultsIterator = null;
    }

    @Override
    public boolean hasNext() {
        if (pageResults == null || pageResultsIterator == null) {
            loadPageResults();
        }

        if (pageResultsIterator.hasNext()) {
            return true;
        }

        // Check if we had a full page last time
        if (pageResults.size() == rowsPerPage) {
            this.pageNo++;
            loadPageResults();
            return pageResultsIterator.hasNext();
        }

        // No more results
        return false;
    }

    @Override
    public T next() {
        if (pageResults == null || pageResultsIterator == null) {
            throw new IllegalStateException("It seems results were cleared during an iteration - do not change pages while iterating");
        }
        return pageResultsIterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Load results for the current page.
     */
    private void loadPageResults() {
        pageResults = query.findPage(pageNo, rowsPerPage);
        pageResultsIterator = pageResults.iterator();
    }

}
