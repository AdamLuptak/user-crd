package com.adamluptakosice.usercrdmin.infra;

import com.adamluptakosice.usercrdmin.exception.BackPressureException;
import com.adamluptakosice.usercrdmin.infra.dto.AddCommand;
import com.adamluptakosice.usercrdmin.infra.dto.Command;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class CommandProducer {
    private static final Logger LOGGER = Logger.getLogger(CommandProducer.class);

    BlockingQueue<Command> sharedQueue;

    public CommandProducer(BlockingQueue<Command> sharedQueue) {
        this.sharedQueue = sharedQueue;
    }

    public void send(Command command) throws BackPressureException {
        final boolean offer = sharedQueue.offer(command);
        if (!offer) {
            throw new BackPressureException("Queue is full please try again");
        } else {
            LOGGER.info("Command %s was accepted".formatted(command.toString()));
        }
    }
}
