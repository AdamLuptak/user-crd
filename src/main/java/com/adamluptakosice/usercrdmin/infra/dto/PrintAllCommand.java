package com.adamluptakosice.usercrdmin.infra.dto;

public class PrintAllCommand implements Command {

    @Override
    public Type getType() {
        return Type.PRINT_ALL;
    }

}
