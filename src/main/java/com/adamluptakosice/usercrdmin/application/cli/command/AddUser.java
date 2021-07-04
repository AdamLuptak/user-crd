package com.adamluptakosice.usercrdmin.application.cli.command;

import com.adamluptakosice.usercrdmin.exception.BackPressureException;
import com.adamluptakosice.usercrdmin.infra.CommandProducer;
import com.adamluptakosice.usercrdmin.infra.dto.AddCommand;
import org.apache.log4j.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "add")
public class AddUser implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(AddUser.class);

    @CommandLine.Option(names = {"-name", "-n"}, required = true, description = "User name")
    String name;

    @CommandLine.Option(names = "-id", required = true, description = "User id")
    Long id;

    @CommandLine.Option(names = "-guid", required = true, description = "User guid")
    String guid;

    private final CommandProducer commandProducer;

    public AddUser(CommandProducer commandProducer) {
        this.commandProducer = commandProducer;
    }

    @Override
    public void run() {
        var addUserCommand = AddCommand.create(id, guid, name);
        try {
            commandProducer.send(addUserCommand);
        } catch (BackPressureException e) {
            LOGGER.info("To many request command was not accepted please wait 5 seconds and try again");
        }
    }
}
