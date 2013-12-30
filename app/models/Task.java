package models;

import play.db.jpa.JPA;
import query.Query;

import javax.persistence.*;
import java.util.List;

/**
 * Model to handle tasks
 *
 * @author Jens (mail@jensjaeger.com)
 */
@Entity(name = "tasks")
public class Task extends Model {

    //@Column(unique=true)
    public String name;

    public boolean done;

    @ManyToOne
    public User creator;

    /**
     * Sample method to demonstrate how to find a
     */
    public static Task findByNameWithJpa(String name){
        return JPA.em().createQuery("from models.Task where name = :name", Task.class)
                       .setParameter("name", name)
                       .getSingleResult();
    }

    public static Query<Task> query(){
        return query(Task.class);
    }

    public static Task findByName(String name){
        return Task.query().eq("name", name).findUnique();
    }

    public static List<Task> findAll(){
        return Task.query().findList();
    }

}
