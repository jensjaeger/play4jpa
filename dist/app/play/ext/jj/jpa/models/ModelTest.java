package play.ext.jj.jpa.models;

import play.Logger;
import play.db.jpa.JPA;
import play.ext.jj.fixy.Fixy;
import play.ext.jj.fixy.JpaFixyBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic Test helper for all Model tests.
 * <p/>
 * Provides helper methods to load fixtures and automatically wrap test methods in transactions.
 *
 * @author Jens (mail@jensjaeger.com)
 * @author rosem
 */
public abstract class ModelTest {

    public static final String DEFAULT_FIXTURE_PATH = "fixtures";

    /**
     * EntityManager to use (created for each test case).
     */
    protected EntityManager em = null;

    /**
     * Transaction to use (created for each test case).
     */
    protected EntityTransaction tx = null;

    /**
     * Load fixtures and open a new transaction.
     */
    public void beforeEachTest() {
        loadFixtures();
        openTransaction();
    }

    /**
     * Use this method for @Before, startup the fake application and <b>finally call {@link #beforeEachTest()}</b>.
     */
    public abstract void before();

    /**
     * Close open transaction.
     */
    public void afterEachTest() {
        closeTransaction();
    }

    /**
     * Use this method for @After, <b>call {@link #beforeEachTest()}</b> and then stop the fake application.
     */
    public abstract void after();

    /**
     * Template method - override if needed.
     * <p/>
     * Return a list of fixture names (placed in conf/fixtures) to load before each test.
     *
     * @return Fixtures to load
     */
    public List<String> fixturesToLoad() {
        return new ArrayList<String>();
    }

    /**
     * Get the default path to where your fixtures are located.
     * @return Path to fixtures' folder
     */
    public static String getDefaultFixturePath() {
        return DEFAULT_FIXTURE_PATH;
    }

    /**
     * Map the given fixture names to full paths using the pattern fixturePath/name.yaml where fixturePath
     * is determined by {@link #getDefaultFixturePath()}.
     * @param names Fixture names
     * @return Paths to fixtures
     */
    public static String[] pathsForFixtureNames(List<String> names) {
        List<String> fixtureNames = new ArrayList<>();
        for (String name : names) {
            fixtureNames.add(getDefaultFixturePath() + "/" + name + ".yaml");
        }
        return fixtureNames.toArray(new String[0]);
    }

    /**
     * Get a new EntityManager and open a transaction.
     */
    protected void openTransaction() {
        em = JPA.em("default");
        if (em == null) {
            Logger.error("Could not get JPA EntityManager");
        } else {
            Logger.debug("Found entity manager: {}", em);
        }

        JPA.bindForCurrentThread(em);
        tx = em.getTransaction();
        tx.begin();
        Logger.debug("Opened transaction");
    }

    /**
     * Commit or rollback transaction if active and close EntityManager.
     */
    protected void closeTransaction() {
        if (tx != null) {
            if (tx.isActive()) {
                if (tx.getRollbackOnly()) {
                    tx.rollback();
                } else {
                    tx.commit();
                }
            }

        }
        JPA.bindForCurrentThread(null);
        if (em != null) {
            em.close();
        }
        Logger.debug("Closed transaction");
    }

    /**
     * Load the fixtures given by {@link #fixturesToLoad()}.
     */
    protected void loadFixtures() {
        Logger.debug("Loading fixtures");
        openTransaction();
        Fixy fixtures = new JpaFixyBuilder(JPA.em()).build();
        fixtures.load(pathsForFixtureNames(fixturesToLoad()));
        closeTransaction();
        Logger.debug("Fixtures loaded successfully.");
    }
}
