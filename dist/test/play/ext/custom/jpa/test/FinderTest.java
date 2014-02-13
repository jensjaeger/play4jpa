package play.ext.custom.jpa.test;

import com.google.common.collect.Lists;
import org.junit.Test;
import play.ext.custom.jpa.test.models.Task;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test cases for {@link play.ext.jj.jpa.models.Finder}
 *
 * @author rosem
 */
public class FinderTest extends TestBase {

    private static final List<String> fixtures = Lists.newArrayList("tasks");

    @Override
    public List<String> fixturesToLoad() {
        return fixtures;
    }

    @Test
    public void allTest() {
        List<Task> tasks = Task.find.all();
        assertEquals(4, tasks.size());

        boolean task1Found = false;
        boolean task2Found = false;
        boolean task3Found = false;
        boolean task4Found = false;

        for (Task t : tasks) {
            switch (t.name) {
                case "Task 1":
                    assertFalse(t.done);
                    assertEquals("jens", t.creator.name);
                    task1Found = true;
                    break;
                case "Task 2":
                    assertFalse(t.done);
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
                    assertEquals("jens", t.creator.name);
                    task4Found = true;
                    break;
            }
        }

        assertTrue(task1Found);
        assertTrue(task2Found);
        assertTrue(task3Found);
        assertTrue(task4Found);
    }

    @Test
    public void firstTest() {
        Task t = Task.find.first();
        assertNotNull(t);
    }

    @Test
    public void countTest() {
        assertEquals(4, Task.find.count());
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
