package play.ext.custom.jpa.test;

import org.junit.After;
import org.junit.Before;
import play.ext.jj.jpa.models.ModelTest;
import play.test.FakeApplication;
import play.test.Helpers;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

public abstract class TestBase extends ModelTest {

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
}
