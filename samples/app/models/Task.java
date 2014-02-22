package models;

import com.play4jpa.jpa.models.Finder;
import com.play4jpa.jpa.models.Model;
import com.play4jpa.jpa.query.Query;
import play.db.jpa.JPA;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Model to handle tasks
 *
 * @author Jens (mail@jensjaeger.com)
 */
@Entity(name = "tasks")
public class Task extends Model<Task> {

    @GeneratedValue
    @Id
    public Long id;

    //@Column(unique=true)
    public String name;

    public boolean done;

    @ManyToOne
    public User creator;

    /**
     * Sample method to demonstrate how to do find with Hql
     */
    public static Task findByNameWithHql(String name){
        return JPA.em().createQuery("from models.Task where name = :name", Task.class)
                       .setParameter("name", name)
                       .getSingleResult();
    }

    public static Query<Task> query(){
        return find.query();
    }

    public static Task findByName(String name){
        return Task.query().eq("name", name).findUnique();
    }

    public static List<Task> findByCreatorName(String creatorName){
        return Task.query().join("creator").eq("creator.name", creatorName).findList();
    }

    public static List<Task> findAll(){
        return Task.query().findList();
    }

    public static Finder<Long, Task> find = new Finder<>(Long.class, Task.class);

}
