package models;

import query.Query;

import javax.persistence.Entity;

/**
 * The User
 *
 * @author Jens (mail@jensjaeger.com)
 */
@Entity
public class User extends Model<User> {

    public String name;

    public String email;

    public static Query<User> query(){
        return query(User.class);
    }

    public static User findByEmail(String email){
        return query().eq("email", email).findUnique();
    }
}
