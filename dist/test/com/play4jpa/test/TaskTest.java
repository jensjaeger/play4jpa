package com.play4jpa.test;

import com.play4jpa.test.models.Task;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for the {@link com.play4jpa.test.models.Task} test model demonstrating and utilizing
 * various {@link com.play4jpa.jpa.models.Finder} / {@link com.play4jpa.jpa.query.Query} methods.
 *
 * @author rosem
 */
public class TaskTest extends TestBase {

    @Test
    public void defaultTasksTest() {
        assertEquals(NUM_DEFAULT_TASKS, Task.find.count());

        Task t = Task.find.query().eq("name", "Task 1").findUnique();
        assertNotNull(t);
        assertEquals("Task 1", t.name);
        assertEquals(false, t.done);
        assertNotNull(t.creator);
        assertEquals("jens", t.creator.name);
        assertEquals("mail@jensjaeger.com", t.creator.email);
    }

    @Test
    public void createTaskTest() {
        long length = Task.find.count();
        assertEquals(NUM_DEFAULT_TASKS, length);

        Task t = new Task();
        t.name = "New Task";
        t.done = false;
        t.save();

        assertNotNull(t.id);
        assertEquals("New Task", t.name);
        assertFalse(t.done);
        assertNull(t.creator);
        length = Task.find.count();
        assertEquals(NUM_DEFAULT_TASKS + 1, length);
    }

    @Test
    public void createAndDeleteTaskTest() {
        Task t = new Task();
        t.done = false;
        t.save();

        assertNotNull(t.id);
        Long id = t.id;
        Task newTask = Task.find.byId(id);

        assertNotNull(newTask);
        assertEquals(id, newTask.id);
        assertEquals(t, newTask);

        t.delete();
        newTask = Task.find.byId(id);
        assertNull(newTask);
    }

    @Test
    public void updateTaskTest() {
        Task t = new Task();
        t.name = "New Task";
        t.done = false;
        t.save();

        Long id = t.id;
        assertNotNull(id);
        assertFalse(t.done);

        t = Task.find.byId(id);
        assertNotNull(t);
        assertFalse(t.done);
        t.done = true;
        t.update();

        t = Task.find.byId(id);
        assertNotNull(t);
        assertEquals("New Task", t.name);
        assertTrue(t.done);
    }
}
