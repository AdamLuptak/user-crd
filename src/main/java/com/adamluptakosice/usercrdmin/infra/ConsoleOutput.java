package com.adamluptakosice.usercrdmin.infra;


import com.adamluptakosice.usercrdmin.infra.dto.Command;

public class ConsoleOutput implements Output {

    @Override
    public void printOutput(Command.Type command, String body) {
        System.out.printf("Command: %s%n", command.name());
        System.out.println(body);
    }

    @Override
    public void printOutput(Command.Type command, Runnable body) {
        System.out.printf("Command: %s%n", command.name());
        body.run();
    }
}
