package play.ext.custom.jpa.test;

import org.hibernate.NonUniqueResultException;
import org.junit.Test;
import play.ext.custom.jpa.test.models.Task;
import play.ext.custom.jpa.test.models.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test cases for {@link play.ext.jj.jpa.query.Query}
 *
 * @author rosem
 */
public class QueryTest extends TestBase {

    @Test(expected = NonUniqueResultException.class)
    public void findUniqueFailTest() {
        Task.find.query().eq("done", false).findUnique();
    }

    @Test
    public void eqTest() {
        Task t = Task.find.query().eq("name", "Task 1").findUnique();
        assertNotNull(t);
        assertEquals("Task 1", t.name);

        t = Task.find.query().eq("done", true).findUnique();
        assertNotNull(t);
        assertEquals("Task 4", t.name);

        User tom = User.find.query().eq("name", "tom").findUnique();
        assertNotNull(tom);
        assertEquals("tom", tom.name);

        t = Task.find.query().eq("creator", tom).findUnique();
        assertNotNull(t);
        assertEquals("Task 3", t.name);
        assertEquals(tom, t.creator);
    }

}
