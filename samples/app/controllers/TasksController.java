package controllers;

import models.Task;
import play.db.jpa.Transactional;
import play.mvc.Result;

/**
 * Tasks controller
 *
 * @author Jens (mail@jensjaeger.com)
 */
public class TasksController extends Application {

    private static final play.Logger.ALogger log = play.Logger.of(TasksController.class);

    /**
     * This method demonstrates that {@link play.db.jpa.JPA#withTransaction}
     * always commits changes in persistence context with no explicit save or update call.
     * @param name
     * @return
     */
    @Transactional
    public static Result magicCommit(String name){
        Task task = Task.findByName(name);
        task.name = "replacePlayTransactionalWithSomethingBetter";
        return ok();
    }
}
