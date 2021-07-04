package com.adamluptakosice.usercrdmin.infra;

import com.adamluptakosice.usercrdmin.service.UserService;
import com.adamluptakosice.usercrdmin.infra.dto.Command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;

public class CommandConsumerService {
    private ExecutorService executorService;
    private BlockingQueue<Command> sharedQueue;
    private ReadWriteLock rwLock;
    private UserService userService;
    private Output output;
    private Integer consumerCount;

    private CommandConsumerService() {

    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public void setConsumerCount(Integer consumerCount) {
        this.consumerCount = consumerCount;
    }

    public void start() {
        for (int i = 0; i < consumerCount; i++) {
            var consumer = new CommandConsumer(sharedQueue, rwLock, userService, output);
            executorService.submit(consumer);
        }
    }

    public static class Builder {
        private ExecutorService executorService;
        private UserService userService;
        private Output output;
        private Integer consumerCount;
        private BlockingQueue<Command> sharedQueue;
        private ReadWriteLock rwLock;

        public Builder withExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder withUserService(UserService userService) {
            this.userService = userService;
            return this;
        }

        public Builder withOutput(Output output) {
            this.output = output;
            return this;
        }

        public Builder withConsumerCount(Integer consumerCount) {
            this.consumerCount = consumerCount;
            return this;
        }

        public Builder withLock(ReadWriteLock rwLock) {
            this.rwLock = rwLock;
            return this;
        }


        public Builder withBlockingQueue(BlockingQueue<Command> sharedQueue) {
            this.sharedQueue = sharedQueue;
            return this;
        }

        public CommandConsumerService build() {
            CommandConsumerService commandConsumerService = new CommandConsumerService();
            commandConsumerService.executorService = this.executorService;
            commandConsumerService.consumerCount = this.consumerCount;
            commandConsumerService.output = this.output;
            commandConsumerService.userService = this.userService;
            commandConsumerService.sharedQueue = this.sharedQueue;
            commandConsumerService.rwLock = this.rwLock;

            return commandConsumerService;
        }
    }
}
