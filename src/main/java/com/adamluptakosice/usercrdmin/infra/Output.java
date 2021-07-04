package com.adamluptakosice.usercrdmin.infra;


import com.adamluptakosice.usercrdmin.infra.dto.Command;

public interface Output {
    void printOutput(Command.Type command, String body);

    void printOutput(Command.Type command, Runnable body);

}
