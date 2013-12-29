package models;

import play.db.jpa.JPA;
import query.Query;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Model to handle tasks
 *
 * @author Jens (mail@jensjaeger.com)
 */
@Entity(name = "tasks")
public class Task extends Model {

    @Id
    @GeneratedValue
    public Long id;

    //@Column(unique=true)
    public String name;

    public boolean done;

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


}
