package com.adamluptakosice.usercrdmin.infra.dto;

public interface Command {

    enum Type {
        ADD, PRINT_ALL, DELETE_ALL
    }

    Type getType();

}
