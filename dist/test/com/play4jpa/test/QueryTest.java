package com.play4jpa.test;

import com.play4jpa.test.models.Task;
import com.play4jpa.test.models.User;
import org.hibernate.NonUniqueResultException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for {@link com.play4jpa.jpa.query.Query}
 *
 * @author rosem
 */
public class QueryTest extends TestBase {

    @Test(expected = NonUniqueResultException.class)
    public void findUniqueFailTest() {
        Task.find.query().eq("done", false).findUnique();
    }

    @Test
    public void eqTest() {
        Task t = Task.find.query().eq("name", "Task 1").findUnique();
        assertNotNull(t);
        assertEquals("Task 1", t.name);

        t = Task.find.query().eq("done", true).findUnique();
        assertNotNull(t);
        assertEquals("Task 4", t.name);

        User tom = User.find.query().eq("name", "tom").findUnique();
        assertNotNull(tom);
        assertEquals("tom", tom.name);

        t = Task.find.query().eq("creator", tom).findUnique();
        assertNotNull(t);
        assertEquals("Task 3", t.name);
        assertEquals(tom, t.creator);
    }

    @Test
    public void eqPropertyTest() {
        Task t = Task.find.query().join("creator").eqProperty("name", "creator.name").findUnique();
        assertNotNull(t);
        assertFalse(t.done);
        assertEquals("jens", t.name);
        assertEquals("jens", t.creator.name);
    }

}
