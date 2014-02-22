package models;

import com.play4jpa.jpa.test.ModelTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.FakeApplication;
import play.test.Helpers;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Unit test for {@link User} model
 *
 * @author Jens (mail@jensjaeger.com)
 */
public class UserTest extends ModelTest {

    FakeApplication app;

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
        return Arrays.asList("tasks");
    }

    @Test
    public void testFindByEmail(){
        assertNotNull(em);
        User user = User.findByEmail("mail@jensjaeger.com");
        assertEquals("jens", user.name);
    }
}
