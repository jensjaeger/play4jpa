package play.ext.custom.jpa.test.models;

import play.ext.jj.jpa.models.Finder;
import play.ext.jj.jpa.models.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Task extends Model<Task> {

    @GeneratedValue
    @Id
    public Long id;

    public String name;

    public Boolean done;

    @ManyToOne
    public User creator;

    public static Finder<Long, Task> find = new Finder<>(Long.class, Task.class);

}
