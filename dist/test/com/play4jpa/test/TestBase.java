package com.play4jpa.test;

import com.google.common.collect.Lists;
import com.play4jpa.jpa.test.ModelTest;
import org.junit.After;
import org.junit.Before;
import play.test.FakeApplication;
import play.test.Helpers;

import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

public abstract class TestBase extends ModelTest {

    public static final int NUM_DEFAULT_TASKS = 5;

    private static final List<String> fixtures = Lists.newArrayList("tasks");

    private FakeApplication app;

    @Override
    public List<String> fixturesToLoad() {
        return fixtures;
    }

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
