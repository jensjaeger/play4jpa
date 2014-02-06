package play.ext.jj.jpa.query;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator for a paged result set of an {@link Query}.
 *
 * @param <T> Entity type
 */
public final class PagedQueryIterator<T> implements Iterator<T> {
    private final Query<T> query;
    private final int pageSize;
    private int pageNo;
    private List<T> pageResults;
    private Iterator<T> pageResultIterator;

    public PagedQueryIterator(Query<T> query, int pageSize) {
        if(query == null) {
            throw new NullPointerException("query can not be null!");
        }
        if(pageSize < 1) {
            throw new IllegalArgumentException("pageSize < 1 is invalid!");
        }
        this.query = query;
        this.pageSize = pageSize;
        this.pageNo = 0;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
        pageResults = null;
    }

    public int getPageNo() {
        return pageNo;
    }

    private void loadPageResults() {
        final int firstResult = pageNo * pageSize;
        query.setFirstResult(firstResult).setMaxRows(pageSize);
        pageResults = query.findList();
        pageResultIterator = pageResults.iterator();
    }

    @Override
    public boolean hasNext() {
        if (pageResultIterator == null) {
            loadPageResults();
        }

        if (pageResultIterator.hasNext()) {
            return true;
        }

        // Load next page
        if (pageResults.size() == pageSize) {
            this.pageNo++;
            loadPageResults();
            return pageResultIterator.hasNext();
        }

        // No more results
        return false;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return pageResultIterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
