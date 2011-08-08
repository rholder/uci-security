package edu.baylor.cs.holder.security.test.model;

import edu.baylor.cs.holder.security.service.accessobjects.User;

public class Person implements User {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
