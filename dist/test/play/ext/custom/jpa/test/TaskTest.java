package play.ext.custom.jpa.test;

import com.google.common.collect.Lists;
import org.junit.Test;
import play.ext.custom.jpa.test.models.Task;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test cases for the {@link play.ext.custom.jpa.test.models.Task} test model demonstrating and utilizing
 * various {@link play.ext.jj.jpa.models.Finder} / {@link play.ext.jj.jpa.query.Query} methods.
 *
 * @author rosem
 */
public class TaskTest extends TestBase {

    private static final List<String> fixtures = Lists.newArrayList("tasks");

    @Override
    public List<String> fixturesToLoad() {
        return fixtures;
    }

    @Test
    public void defaultTasksTest() {
        assertEquals(4, Task.find.count());

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
        assertEquals(4, length);

        Task t = new Task();
        t.name = "New Task";
        t.done = false;
        t.save();

        assertNotNull(t.id);
        assertEquals("New Task", t.name);
        assertFalse(t.done);
        assertNull(t.creator);
        length = Task.find.count();
        assertEquals(5, length);
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
