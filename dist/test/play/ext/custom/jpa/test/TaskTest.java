package play.ext.custom.jpa.test;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.ext.custom.jpa.test.models.Task;
import play.ext.jj.jpa.models.ModelTest;
import play.test.FakeApplication;
import play.test.Helpers;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

public class TaskTest extends ModelTest {

    private static final List<String> fixtures = Lists.newArrayList("tasks");

    private FakeApplication app;

    @Before
    @Override
    public void before() {
        app = fakeApplication(inMemoryDatabase());
        Helpers.start(app);
        beforeEachTest();
    }

    @After
    @Override
    public void after() {
        afterEachTest();
        Helpers.stop(app);
    }

    @Override
    public List<String> fixturesToLoad() {
        return fixtures;
    }

    @Test
    public void defaultTasks() {
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
        t.done = false;
        t.save();

        assertNotNull(t.id);
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
    }
}
