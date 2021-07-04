package com.adamluptakosice.usercrdmin.application.cli;

import com.adamluptakosice.usercrdmin.service.UserService;
import com.adamluptakosice.usercrdmin.application.cli.command.AddUser;
import com.adamluptakosice.usercrdmin.application.cli.command.DeleteAllUsers;
import com.adamluptakosice.usercrdmin.application.cli.command.PrintAllUsers;
import com.adamluptakosice.usercrdmin.dao.UserDaoH2;
import com.adamluptakosice.usercrdmin.infra.*;
import com.adamluptakosice.usercrdmin.infra.dto.Command;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import picocli.CommandLine;

import javax.validation.Validation;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@CommandLine.Command(name = "user")
public class CliApplication {
    private static final Logger LOGGER = Logger.getLogger(CliApplication.class);

    public static void main(String[] args) {
        var sharedQueue = new LinkedBlockingQueue<Command>(10);
        var consumerCount = 4;
        var commandProducer = new CommandProducer(sharedQueue);
        var executorService = Executors.newFixedThreadPool(consumerCount + 1);

        var dataSource = new DataSourceFactory(HikariConfigProvider.getConfig()).create();
        var migrationRunner = Flyway.configure()
                .dataSource(dataSource)
                .load();

        CommandConsumerService commandConsumerService = createCommandConsumerService(
                sharedQueue,
                consumerCount,
                executorService,
                dataSource);

        // before application hooks
        try {
            migrationRunner.migrate();
            commandConsumerService.start();
        } catch (Exception e) {
            LOGGER.error("System cannot init cause: %s".formatted(e.getMessage()), e);
            System.exit(1);
        }

        try (InputStreamReader in = new InputStreamReader(System.in);
             BufferedReader reader = new BufferedReader(in)) {
            var isExit = false;
            while (!isExit) {
                var input = "";
                while (input.length() < 1) {
                    System.out.print(">");
                    input = reader.readLine();
                }

                if ("EXIT".equalsIgnoreCase(input)) {
                    isExit = true;
                } else {
                    new CommandLine(new CliApplication())
                            .addSubcommand("deleteAll", new DeleteAllUsers(commandProducer))
                            .addSubcommand("add", new AddUser(commandProducer))
                            .addSubcommand("printAll", new PrintAllUsers(commandProducer))
                            .execute(input.split(" "));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            LOGGER.info("GoodBye");
            handleTerminationOfExecutors(executorService);
        }
    }

    private static CommandConsumerService createCommandConsumerService(LinkedBlockingQueue<Command> sharedQueue, int consumerCount, ExecutorService executorService, javax.sql.DataSource dataSource) {
        var userDao = new UserDaoH2(dataSource);
        var ValidatorFactory = Validation.buildDefaultValidatorFactory();
        var validator = ValidatorFactory.getValidator();
        var userService = new UserService(userDao, validator);
        var rwLock = new ReentrantReadWriteLock();
        var output = new ConsoleOutput();

        var commandConsumerService = new CommandConsumerService.Builder()
                .withExecutorService(executorService)
                .withLock(rwLock)
                .withBlockingQueue(sharedQueue)
                .withUserService(userService)
                .withConsumerCount(consumerCount)
                .withOutput(output)
                .build();
        return commandConsumerService;
    }

    private static void handleTerminationOfExecutors(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

