package com.play4jpa.test;

import com.google.common.collect.Lists;
import com.play4jpa.jpa.query.Query;
import com.play4jpa.test.models.Task;
import com.play4jpa.test.models.User;
import org.hibernate.NonUniqueResultException;
import org.junit.Test;
import play.Logger;

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
        assertEquals(NUM_DEFAULT_TASKS - 2, tasks.size());

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
        assertEquals(2, tasks.size());

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

    @Test
    public void leTest() {
        List<Task> tasks = Task.find.query().le("priority", 3).findList();
        assertEquals(4, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.priority <= 3);
        }
    }

    @Test
    public void lePropertyTest() {
        List<Task> tasks = Task.find.query().join("creator").leProperty("priority", "creator.defaultPriority").findList();
        assertEquals(2, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.priority <= t.creator.defaultPriority);
        }
    }

    @Test
    public void ltTest() {
        List<Task> tasks = Task.find.query().lt("priority", 3).findList();
        assertEquals(2, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.priority < 3);
        }
    }

    @Test
    public void ltPropertyTest() {
        List<Task> tasks = Task.find.query().join("creator").ltProperty("priority", "creator.defaultPriority").findList();
        assertEquals(2, tasks.size());

        for (Task t : tasks) {
            assertTrue(t.priority <= t.creator.defaultPriority);
        }
    }

    @Test
    public void betweenTest() {
        List<Task> tasks = Task.find.query().between("priority", 3, 6).findList();
        assertEquals(3, tasks.size());

        for (Task t : tasks) {
            assertTrue(3 <= t.priority && t.priority <= 6);
        }
    }

    @Test
    public void isNullTest() {
        Task t = Task.find.query().isNull("done").findUnique();
        assertNotNull(t);
        assertNull(t.done);
        assertEquals("Task 2", t.name);
    }

    @Test
    public void isNotNullTest() {
        List<Task> tasks = Task.find.query().isNotNull("done").findList();
        assertEquals(NUM_DEFAULT_TASKS - 1, tasks.size());

        for (Task t : tasks) {
            assertNotNull(t.done);
        }
    }

    @Test
    public void inTest() {
        List<String> options = Lists.newArrayList("Task 1", "Task 2");
        List<Task> tasks = Task.find.query().in("name", options).findList();
        assertEquals(2, tasks.size());

        for (Task t : tasks) {
            assertTrue(options.contains(t.name));
        }
    }

    @Test
    public void inQueryTest() {
        Query<User> userQuery = User.find.query().eq("name", "jens");
        Task t = Task.find.query().in("name", userQuery, "name").findUnique();
        assertNotNull(t);
        assertEquals("jens", t.name);
        assertEquals("jens", t.creator.name);
    }

    @Test
    public void notInTest() {
        List<String> options = Lists.newArrayList("Task 1", "Task 2");
        List<Task> tasks = Task.find.query().notIn("name", options).findList();
        assertEquals(NUM_DEFAULT_TASKS - 2, tasks.size());

        for (Task t : tasks) {
            assertFalse(options.contains(t.name));
        }
    }

    @Test
    public void notInQueryTest() {
        Query<User> userQuery = User.find.query().eq("name", "jens");
        List<Task> tasks = Task.find.query().notIn("name", userQuery, "name").findList();

        assertEquals(4, tasks.size());
        for (Task t : tasks) {
            assertNotEquals("jens", t.name);
        }
    }

    @Test
    public void orderByAscTest() {
        List<Task> tasks = Task.find.query().orderByAsc("priority").findList();
        assertEquals(NUM_DEFAULT_TASKS, tasks.size());

        int lastPriority = Integer.MIN_VALUE;
        for (Task t : tasks) {
            assertTrue(lastPriority <= t.priority);
            lastPriority = t.priority;
        }
    }

    @Test
    public void orderByDescTest() {
        List<Task> tasks = Task.find.query().orderByDesc("priority").findList();
        assertEquals(NUM_DEFAULT_TASKS, tasks.size());

        int lastPriority = Integer.MAX_VALUE;
        for (Task t : tasks) {
            assertTrue(lastPriority >= t.priority);
            lastPriority = t.priority;
        }
    }

    @Test
    public void findRowCountTest() {
        long rowCount = Task.find.query().findRowCount();
        assertEquals(NUM_DEFAULT_TASKS, rowCount);

        rowCount = Task.find.query().eq("name", "Task 1").findRowCount();
        assertEquals(1, rowCount);

        rowCount = Task.find.query().join("creator").eqProperty("name", "creator.name").findRowCount();
        assertEquals(1, rowCount);
    }

    @Test
    public void findDistinctRowCountTest() {
        long distinctRowCount = Task.find.query().isNotNull("done").findDistinctRowCount("done");
        assertEquals(2, distinctRowCount);

        distinctRowCount = Task.find.query().join("creator").findDistinctRowCount("creator.name");
        assertEquals(3, distinctRowCount);

        distinctRowCount = Task.find.query().join("creator").findDistinctRowCount("creator.name");
        assertEquals(3, distinctRowCount);
    }

    @Test
    public void leftJoinTest() {
        List<Task> tasks = Task.find.query().join("creator").findList();
        assertEquals(NUM_DEFAULT_TASKS - 1, tasks.size());

        tasks = Task.find.query().leftJoin("creator").findList();
        assertEquals(NUM_DEFAULT_TASKS, tasks.size());
    }

    @Test
    public void findMaxValueTest(){
        int age = User.find.query().findMaxValue("age");
        assertEquals(30, age);
    }
}
