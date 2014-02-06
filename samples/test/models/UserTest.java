package models;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link User} model
 *
 * @author Jens (mail@jensjaeger.com)
 */
public class UserTest extends ModelTest {

    @Override
    public List<String> fixturesToLoad() {
        return Arrays.asList("tasks");
    }

    @Test
    public void testFindByEmail(){
        User user = User.findByEmail("mail@jensjaeger.com");
        assertEquals("jens", user.name);
    }
}
