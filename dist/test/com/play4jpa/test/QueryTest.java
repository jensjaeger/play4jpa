package com.play4jpa.test;

import com.play4jpa.test.models.Task;
import com.play4jpa.test.models.User;
import org.hibernate.NonUniqueResultException;
import org.junit.Test;

import java.util.List;

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
    public void joinAndEqPropertyTest() {
        Task t = Task.find.query().join("creator").eqProperty("name", "creator.name").findUnique();
        assertNotNull(t);
        assertFalse(t.done);
        assertEquals("jens", t.name);
        assertEquals("jens", t.creator.name);
    }

    @Test
    public void joinAndEqTest() {
        Task t = Task.find.query().join("creator").eq("creator.name", "tom").findUnique();
        assertNotNull(t);
        assertFalse(t.done);
        assertEquals("Task 3", t.name);
        assertEquals("tom", t.creator.name);
    }

    @Test
    public void ieqTest() {
        Task t = Task.find.query().ieq("name", "task 1").findUnique();
        assertNotNull(t);
        assertEquals("Task 1", t.name);
    }

    @Test
    public void neTest() {
        List<Task> tasks = Task.find.query().ne("name", "Task 1").findList();
        assertEquals(NUM_DEFAULT_TASKS - 1, tasks.size());

        for (Task t : tasks) {
            assertNotEquals("Task 1", t.name);
        }

        Task t = Task.find.query().ne("done", false).findUnique();
        assertNotNull(t);
        assertEquals("Task 4", t.name);
        assertTrue(t.done);
    }

    @Test
    public void nePropertyTest() {
        List<Task> tasks = Task.find.query().join("creator").neProperty("name", "creator.name").findList();
        assertEquals(NUM_DEFAULT_TASKS - 1, tasks.size());

        for (Task t : tasks) {
            assertNotEquals("jens", t.name);
        }
    }

    @Test
    public void ilikeTest() {
        List<Task> tasks = Task.find.query().ilike("name", "%ask%").findList();
        assertEquals(NUM_DEFAULT_TASKS - 1, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.name.startsWith("Task"));
        }
    }

    @Test
    public void geTest() {
        List<Task> tasks = Task.find.query().ge("priority", 3).findList();
        assertEquals(3, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.priority >= 3);
        }
    }

    @Test
    public void gePropertyTest() {
        List<Task> tasks = Task.find.query().join("creator").geProperty("priority", "creator.defaultPriority").findList();
        assertEquals(3, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.priority >= t.creator.defaultPriority);
        }
    }

    @Test
    public void gtTest() {
        List<Task> tasks = Task.find.query().gt("priority", 3).findList();
        assertEquals(1, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.priority > 3);
        }
    }

    @Test
    public void gtPropertyTest() {
        List<Task> tasks = Task.find.query().join("creator").gtProperty("priority", "creator.defaultPriority").findList();
        assertEquals(2, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.priority > t.creator.defaultPriority);
        }
    }

}
