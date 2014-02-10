package play.ext.custom.jpa.test.models;

import play.ext.jj.jpa.models.Finder;
import play.ext.jj.jpa.models.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User extends Model<User> {

    @Id
    public String email;

    public String name;

    public static Finder<String, User> find = new Finder<>(String.class, User.class);

}
