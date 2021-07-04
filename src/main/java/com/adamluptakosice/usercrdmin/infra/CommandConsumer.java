package com.adamluptakosice.usercrdmin.infra;

import com.adamluptakosice.usercrdmin.service.UserService;
import com.adamluptakosice.usercrdmin.domain.User;
import com.adamluptakosice.usercrdmin.infra.dto.AddCommand;
import com.adamluptakosice.usercrdmin.infra.dto.Command;
import dnl.utils.text.table.TextTable;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;

import static com.adamluptakosice.usercrdmin.infra.dto.Command.Type.*;

public class CommandConsumer implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(CommandConsumer.class);
    public static final String[] TABLE_HEADER = {"Id", "Guid", "Name"};

    private BlockingQueue<Command> sharedQueue;
    private ReadWriteLock rwLock;
    private UserService userService;
    private Output output;

    public CommandConsumer(BlockingQueue<Command> sharedQueue,
                           ReadWriteLock rwLock,
                           UserService userService,
                           Output output) {
        this.sharedQueue = sharedQueue;
        this.rwLock = rwLock;
        this.userService = userService;
        this.output = output;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Command command = sharedQueue.take();
                switch (command.getType()) {
                    case ADD -> runWithWriteLock(() -> {
                        final var userInDb = userService.add(((AddCommand) command).getUser());
                        output.printOutput(ADD, "Successfully add user: %s".formatted(userInDb.toString()));
                    });
                    case DELETE_ALL -> {
                        runWithWriteLock(() -> {
                            final Integer deletedCount = userService.deleteAll();
                            output.printOutput(DELETE_ALL, "Successfully deleted all : %d users".formatted(deletedCount));
                        });
                    }
                    case PRINT_ALL -> runWithReadLock(() -> {
                        var userList = userService.findAll();
                        TextTable tt = transformToTextTable(userList);
                        output.printOutput(PRINT_ALL, tt::printTable);
                    });
                    default -> throw new UnsupportedOperationException("Unsupported operation");
                }

            } catch (Exception e) {
                LOGGER.error("Unable to process command: %s".formatted(e.getCause().getMessage()));
            }
        }
    }

    private TextTable transformToTextTable(java.util.List<User> userList) {
        Object[][] data = new Object[userList.size()][TABLE_HEADER.length];
        for (int i = 0; i < userList.size(); i++) {
            var user = userList.get(0);
            for (int j = 0; j < TABLE_HEADER.length; j++) {
                switch (j) {
                    case 0 -> data[i][j] = user.getId();
                    case 1 -> data[i][j] = user.getGuid();
                    case 2 -> data[i][j] = user.getName();
                }
            }
        }

        TextTable tt = new TextTable(TABLE_HEADER, data);
        return tt;
    }

    private void runWithWriteLock(Runnable runnable) {
        rwLock.writeLock().lock();
        try {
            runnable.run();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void runWithReadLock(Runnable runnable) {
        rwLock.readLock().lock();
        try {
            runnable.run();
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
