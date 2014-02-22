package com.play4jpa.test.models;

import com.play4jpa.jpa.models.Finder;
import com.play4jpa.jpa.models.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User extends Model<User> {

    @Id
    public String email;

    public String name;

    public Integer defaultPriority;

    public Integer age;

    public static Finder<String, User> find = new Finder<>(String.class, User.class);

}
