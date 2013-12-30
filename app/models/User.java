package models;

import javax.persistence.Entity;

/**
 * The User
 *
 * @author Jens (mail@jensjaeger.com)
 */
@Entity
public class User extends Model<User>{

    public String email;
}
