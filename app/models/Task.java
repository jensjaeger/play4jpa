package models;

import play.db.jpa.JPA;

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

    public static Task findByName(String name){
        return JPA.em().createQuery("from models.Task where name = :name", Task.class)
                       .setParameter("name", name)
                       .getSingleResult();
    }
}
