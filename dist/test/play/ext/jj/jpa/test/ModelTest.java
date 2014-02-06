package play.ext.jj.jpa.test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import play.ext.jj.fixy.Fixy;
import play.ext.jj.fixy.JpaFixyBuilder;
import org.junit.After;
import org.junit.Before;

import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.Helpers;

/**
 * Generic Test setup for all Model tests.
 *
 * Handles the fakeApplication startup, loads an fresh in memory database before each test and loads the fixtures.
 *
 * @author Jens (mail@jensjaeger.com)
 */
public class ModelTest {

    public FakeApplication app;
    public Fixy fixtures;
    EntityManager em = null;
    EntityTransaction tx = null;

    @Before
    public void beforeEachTest() {
        startFakeApp();
        loadFixtures();
        openTransaction();
    }

    @After
    public void afterEachTest(){
        closeTransaction();
        stopFakeApp();
    }

    // template method to load fixtures
    public List<String> fixturesToLoad() { return new ArrayList<String>(); }

    private void startFakeApp() {
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
        Helpers.start(app);
    }

    private void stopFakeApp(){
        Helpers.stop(app);
    }

    private void openTransaction() {
        em = JPA.em("default");
        JPA.bindForCurrentThread(em);
        tx = em.getTransaction();
        tx.begin();
    }

    private void closeTransaction() {
        if(tx != null) {
            if (tx.isActive()){
                if(tx.getRollbackOnly()) {
                    tx.rollback();
                } else {
                    tx.commit();
                }
            }

        }
        JPA.bindForCurrentThread(null);
        if(em != null) {
            em.close();
        }
    }

    private void loadFixtures() {
        openTransaction();
        fixtures = new JpaFixyBuilder(JPA.em()).build();
        fixtures.load(addPathAndPrefix(fixturesToLoad()));
        closeTransaction();
    }

    private static String[] addPathAndPrefix(List<String> names){
        List<String> fixtureNames = new ArrayList<>();
        for (String name : names){
            fixtureNames.add("fixtures/" + name + ".yaml");
        }
        return fixtureNames.toArray(new String[0]);
    }
}
