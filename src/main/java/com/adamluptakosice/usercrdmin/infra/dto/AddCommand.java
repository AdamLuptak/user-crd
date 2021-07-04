package com.adamluptakosice.usercrdmin.infra.dto;

import com.adamluptakosice.usercrdmin.domain.User;

public class AddCommand implements Command {

    private User user;

    public static AddCommand create(Long id, String guid, String name) {
        return new AddCommand(new User(id, guid, name));
    }

    public AddCommand(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Type getType() {
        return Type.ADD;
    }

    @Override
    public String toString() {
        return "AddCommand{" +
                "user=" + user +
                '}';
    }
}
