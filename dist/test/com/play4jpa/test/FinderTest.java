package com.play4jpa.test;

import com.play4jpa.test.models.Task;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test cases for {@link com.play4jpa.jpa.models.Finder}
 *
 * @author rosem
 */
public class FinderTest extends TestBase {

    @Test
    public void allTest() {
        List<Task> tasks = Task.find.all();
        assertEquals(NUM_DEFAULT_TASKS, tasks.size());

        boolean task1Found = false;
        boolean task2Found = false;
        boolean task3Found = false;
        boolean task4Found = false;
        boolean task5Found = false;

        for (Task t : tasks) {
            switch (t.name) {
                case "Task 1":
                    assertFalse(t.done);
                    assertEquals("jens", t.creator.name);
                    task1Found = true;
                    break;
                case "Task 2":
                    assertNull(t.done);
                    assertEquals("max", t.creator.name);
                    task2Found = true;
                    break;
                case "Task 3":
                    assertFalse(t.done);
                    assertEquals("tom", t.creator.name);
                    task3Found = true;
                    break;
                case "Task 4":
                    assertTrue(t.done);
                    assertNull(t.creator);
                    task4Found = true;
                    break;
                case "jens":
                    assertFalse(t.done);
                    assertEquals("jens", t.creator.name);
                    task5Found = true;
                    break;
                default:
                    fail();
            }
        }

        assertTrue(task1Found);
        assertTrue(task2Found);
        assertTrue(task3Found);
        assertTrue(task4Found);
        assertTrue(task5Found);
    }

    @Test
    public void firstTest() {
        Task t = Task.find.first();
        assertNotNull(t);
    }

    @Test
    public void countTest() {
        assertEquals(NUM_DEFAULT_TASKS, Task.find.count());
    }

    @Test
    public void byId() {
        Task t = Task.find.first();
        Long id = t.id;

        t = Task.find.byId(id);
        assertNotNull(t);
        assertEquals(id, t.id);
    }
}
