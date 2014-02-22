package com.play4jpa.test.models;

import com.play4jpa.jpa.models.Finder;
import com.play4jpa.jpa.models.Model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Task extends Model<Task> {

    @GeneratedValue
    @Id
    public Long id;

    public String name;

    public Boolean done;

    public Integer priority;

    @ManyToOne
    public User creator;

    @ManyToMany
    public Set<User> assignees;

    public static Finder<Long, Task> find = new Finder<>(Long.class, Task.class);

}
