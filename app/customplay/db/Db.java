package customplay.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Custom JPA Helpers.
 */
public class Db extends play.db.jpa.JPA {

    private static final play.Logger.ALogger log = play.Logger.of(Db.class);

    private static final ThreadLocal<Boolean> needsCommit = new ThreadLocal<>();

    /**
     * Run a block of code in a JPA transaction.
     *
     * @param block Block of code to execute.
     */
    public static <T> T withTx(play.libs.F.Function0<T> block) throws Throwable {
        return withTx("default", false, block);
    }

    /**
     * Run a block of code in a JPA transaction.
     *
     * @param block Block of code to execute.
     */
    public static void withTx(final play.libs.F.Callback0 block) {
        try {
            withTx("default", false, new play.libs.F.Function0<Void>() {
                @Override
                public Void apply() throws Throwable {
                    block.invoke();
                    return null;
                }
            });
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Run a block of code in a JPA transaction.
     *
     * @param name The persistence unit name
     * @param readOnly Is the transaction read-only?
     * @param block Block of code to execute.
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
                }
                else {
                    tx.rollback();
                }
            }

            return result;

        }
        catch (Throwable t) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                }
                catch (Throwable e) {
                    // Ignore errors on rollback
                    log.error("Error on rollback!", e);
                }
            }
            throw t;
        }
        finally {
            bindNeedsCommitToThread();
            play.db.jpa.JPA.bindForCurrentThread(null);
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Bind an EntityManager to the current thread.
     */
    public static void bindNeedsCommitToThread() {
        needsCommit.set(Boolean.FALSE);
    }

    /**
     * Manual commit
     */
    public static void commit() {
        play.db.jpa.JPA.em().getTransaction().commit();
    }

    /**
     * Manual rollback
     */
    public static void rollback() {
        EntityTransaction tx = play.db.jpa.JPA.em().getTransaction();
        if(tx.isActive()) {
            tx.rollback();
        }
    }

    /**
     * If set commit needed is set. The transaction is commited
     * after the block execution.
     */
    public static void setCommitNeeded() {
        needsCommit.set(Boolean.TRUE);
    }

    private static boolean needsCommit() {
        final Boolean b = needsCommit.get();
        return (b != null && b.booleanValue());
    }
}
