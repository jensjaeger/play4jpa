package models;

import helper.ModelTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link Task}
 *
 * @author Jens (mail@jensjaeger.com)
 */
public class TaskTest extends ModelTest {

    @Override
    public List<String> fixturesToLoad() {
        return Arrays.asList("tasks");
    }

    @Test
    public void testFindByName(){
        Task task = Task.findByName("Task 1");
        assertEquals("Task 1", task.name);
    }
}
