package com.adamluptakosice.usercrdmin.infra.dto;


public class DeleteAllCommand implements Command {

    @Override
    public Type getType() {
        return Type.DELETE_ALL;
    }
}
