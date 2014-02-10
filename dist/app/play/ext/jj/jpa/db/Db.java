package play.ext.jj.jpa.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Helper functions to facilitate working with database transactions for Hibernate.
 *
 * @author Jens (mail@jensjaeger.com)
 * @author rosem
 */
public class Db extends play.db.jpa.JPA {

    /**
     * Logger instance
     */
    private static final play.Logger.ALogger log = play.Logger.of(Db.class);

    /**
     * Flag to indicate whether a commit operation is needed.
     */
    private static final ThreadLocal<Boolean> needsCommit = new ThreadLocal<>();

    /**
     * Run a block of code in a JPA transaction.
     *
     * @param block Block of code to execute.
     */
    /**
     * Run a block of code in a JPA transaction and return a value.
     *
     * @param block Block to execute
     * @param <T>   Type of return value
     * @return Return value of block
     * @throws java.lang.Throwable by block.invoke()
     */
    public static <T> T withTx(play.libs.F.Function0<T> block) throws Throwable {
        return withTx("default", false, block);
    }

    /**
     * Run a block of code in a JPA transaction without returning a value.
     *
     * @param block Block to execute
     * @throws java.lang.Throwable by block.invoke()
     */
    public static void withTx(final play.libs.F.Callback0 block) throws Throwable {
        withTx("default", false, new play.libs.F.Function0<Void>() {
            @Override
            public Void apply() throws Throwable {
                block.invoke();
                return null;
            }
        });
    }

    /**
     * Run a block of code in a JPA transaction for a specific persistence unit and return a value.
     *
     * @param name     Persistence unit name
     * @param readOnly If true, transaction is read-only
     * @param block    Block to execute
     * @return Return value of block
     * @throws java.lang.Throwable by block.invoke()
     */
    public static <T> T withTx(String name, boolean readOnly, play.libs.F.Function0<T> block) throws Throwable {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = play.db.jpa.JPA.em(name);
            play.db.jpa.JPA.bindForCurrentThread(em);
            bindNeedsCommitToThread();

            if (!readOnly) {
                tx = em.getTransaction();
                tx.begin();
            }

            T result = block.apply();

            if (tx != null && tx.isActive()) {
                if (needsCommit() && !tx.getRollbackOnly()) {
                    tx.commit();
                } else {
                    tx.rollback();
                }
            }
            return result;
        } catch (Throwable t) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Throwable e) {
                    // Ignore errors on rollback
                    log.error("Error on rollback!", e);
                }
            }
            throw t;
        } finally {
            bindNeedsCommitToThread();
            play.db.jpa.JPA.bindForCurrentThread(null);
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Set default value for {@link #needsCommit} for the current thread.
     */
    public static void bindNeedsCommitToThread() {
        needsCommit.set(Boolean.FALSE);
    }

    /**
     * Execute a manual commit.
     */
    public static void commit() {
        play.db.jpa.JPA.em().getTransaction().commit();
    }

    /**
     * Execute a manual rollback
     */
    public static void rollback() {
        EntityTransaction tx = play.db.jpa.JPA.em().getTransaction();
        if (tx.isActive()) {
            tx.rollback();
        }
    }

    /**
     * If commit needed is set, the transaction is committed after the block execution.
     */
    public static void setCommitNeeded() {
        needsCommit.set(Boolean.TRUE);
    }

    /**
     * Get if a commit is needed.
     *
     * @return If a commit is needed
     */
    private static boolean needsCommit() {
        final Boolean b = needsCommit.get();
        return (b != null && b.booleanValue());
    }
}
