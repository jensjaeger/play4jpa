package models;


import com.play4jpa.jpa.models.Finder;
import com.play4jpa.jpa.models.Model;
import com.play4jpa.jpa.query.Query;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The User
 *
 * @author Jens (mail@jensjaeger.com)
 */
@Entity
public class User extends Model<User> {

    @Id
    public String email;

    public String name;

    public static Query<User> query() {
        return find.query();
    }

    public static User findByEmail(String email) {
        return query().eq("email", email).findUnique();
    }

    public static Finder<String, User> find = new Finder<>(String.class, User.class);

}
