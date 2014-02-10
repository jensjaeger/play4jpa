package play.ext.custom.jpa.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.ext.custom.jpa.test.models.Task;
import play.ext.custom.jpa.test.models.User;
import play.ext.jj.jpa.models.ModelTest;
import play.test.FakeApplication;
import play.test.Helpers;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

public class TaskTest extends ModelTest {

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

    @Test
    public void createTaskTest() {
        Task t = new Task();
        t.done = false;
        t.save();

        List<Task> all = Task.find.all();
        assertNotNull(all);
        assertEquals(1, all.size());
    }

    @Test
    public void createAndDeleteTaskTest() {
        Task t = new Task();
        t.done = false;
        t.save();

        Long id = t.id;
        Task newTask = Task.find.byId(id);

        assertNotNull(newTask);
        assertEquals(id, newTask.id);
        assertEquals(t, newTask);
    }
}
