package com.adamluptakosice.usercrdmin;

import com.adamluptakosice.usercrdmin.dao.UserDaoH2;
import com.adamluptakosice.usercrdmin.domain.User;
import com.adamluptakosice.usercrdmin.exception.UnableFindResourceException;
import com.adamluptakosice.usercrdmin.exception.UnableToDeleteResourceException;
import com.adamluptakosice.usercrdmin.exception.UnableToSaveResourceException;
import com.adamluptakosice.usercrdmin.infra.DataSourceFactory;
import com.adamluptakosice.usercrdmin.infra.HikariConfigProvider;
import com.adamluptakosice.usercrdmin.service.UserService;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.WithAssertions;
import org.flywaydb.core.Flyway;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class UserServiceTestIT implements WithAssertions {

    private UserService userService;
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        var dataSource = new DataSourceFactory(HikariConfigProvider.getConfig()).create();
        var userDao = new UserDaoH2(dataSource);
        var ValidatorFactory = Validation.buildDefaultValidatorFactory();
        validator = ValidatorFactory.getValidator();
        userService = new UserService(userDao, validator);

        var migrationRunner = Flyway.configure()
                .dataSource(dataSource)
                .load();
        migrationRunner.clean();
        migrationRunner.migrate();
    }


    @Test
    void chaiOfCommands() {
        // given
        var user1 = new User(1L, "a1", "Robert");
        var user2 = new User(2L, "a1", "Robert2");

        // when
        userService.add(user1);
        userService.add(user2);
        var actualList = userService.findAll();

        // then
        assertThat(actualList).containsExactlyInAnyOrder(user1, user2);

        // when
        userService.deleteAll();
        actualList = userService.findAll();

        // then
        assertThat(actualList).isEmpty();
    }

    @Test
    void shouldAddUser() {
        // given
        var expected = new User(1L, "a1", "Robert");
        var input = new User(1L, "a1", "Robert");

        // when
        final User actual = userService.add(input);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldFailWhenAddUserDueToUserSameId() {
        // given
        var input = new User(1L, "a1", "Robert");
        userService.add(input);

        // when
        assertThatThrownBy(() -> userService.add(new User(1L, "a1", "Peter")))
                // then
                .isInstanceOf(UnableToSaveResourceException.class)
                .hasMessageContaining("Unable to store User{id=1, guid='a1', name='Peter'}")
                .getCause()
                .isInstanceOf(JdbcSQLIntegrityConstraintViolationException.class)
                .hasMessageContaining("Unique index or primary key violation:");
    }

    @Test
    void shouldFailWhenAddUserDueToUserSameName() {
        // given
        var input = new User(1L, "a1", "Robert");
        userService.add(input);

        // when
        assertThatThrownBy(() -> userService.add(new User(2L, "a1", "Robert")))
                // then
                .isInstanceOf(UnableToSaveResourceException.class)
                .hasMessageContaining("Unable to store User{id=2, guid='a1', name='Robert'}")
                .getCause()
                .isInstanceOf(JdbcSQLIntegrityConstraintViolationException.class)
                .hasMessageContaining("Unique index or primary key violation:");
    }

    @Test
    void shouldFailWhenAddUserDueToDataSourceNotAvailable() {
        // given
        UserService userService = getUserServiceWithFakeDataSource();
        var input = new User(1L, "a1", "Robert");

        // when
        assertThatThrownBy(() -> userService.add(input))
                // then
                .isInstanceOf(UnableToSaveResourceException.class)
                .hasMessageContaining("Unable to store User{id=1, guid='a1', name='Robert'}");
    }

    @Test
    void shouldDeleteAllUsers() {
        // given
        LongStream.range(1, 5).forEach(i -> {
            userService.add(new User(i, "a1", "Robert" + i));
        });
        // when
        userService.deleteAll();
        // then
        final List<User> actualList = userService.findAll();
        assertThat(actualList).isEmpty();
    }

    @Test
    void shouldFailWhenDeleteAllUsersDueToDataSourceNotAvailable() {
        // given
        UserService userService = getUserServiceWithFakeDataSource();

        // when
        assertThatThrownBy(userService::deleteAll)
                // then
                .isInstanceOf(UnableToDeleteResourceException.class)
                .hasMessageContaining("Unable to delete users");
    }


    @Test
    void shouldFindAllUsers() {
        // given
        var expectedList = LongStream.range(1, 5).mapToObj(i ->
                userService.add(new User(i, "a1", "Robert" + i)))
                .collect(Collectors.toList());
        // when
        final var actualList = userService.findAll();

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void shouldFailWhenFindAllUsersDueToDataSourceNotAvailable() {
        // given
        UserService userService = getUserServiceWithFakeDataSource();

        // when
        assertThatThrownBy(userService::findAll)
                // then
                .isInstanceOf(UnableFindResourceException.class)
                .hasMessageContaining("Unable to find resource");
    }

    @NotNull
    private UserService getUserServiceWithFakeDataSource() {
        var dataSource = new HikariDataSource();
        var userDao = new UserDaoH2(dataSource);
        return new UserService(userDao, validator);
    }

}
