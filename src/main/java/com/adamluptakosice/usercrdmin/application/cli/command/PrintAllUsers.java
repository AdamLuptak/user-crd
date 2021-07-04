package com.adamluptakosice.usercrdmin.application.cli.command;

import com.adamluptakosice.usercrdmin.exception.BackPressureException;
import com.adamluptakosice.usercrdmin.infra.CommandProducer;
import com.adamluptakosice.usercrdmin.infra.dto.PrintAllCommand;
import org.apache.log4j.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "printAll")
public class PrintAllUsers implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(PrintAllUsers.class);

    private final CommandProducer commandProducer;

    public PrintAllUsers(CommandProducer commandProducer) {
        this.commandProducer = commandProducer;
    }

    @Override
    public void run() {
        try {
            commandProducer.send(new PrintAllCommand());
        } catch (BackPressureException e) {
            LOGGER.info("To many request command was not accepted please wait 5 seconds and try again");
        }
    }
}
