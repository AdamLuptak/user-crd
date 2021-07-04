package com.adamluptakosice.usercrdmin;

import com.adamluptakosice.usercrdmin.dao.UserDao;
import com.adamluptakosice.usercrdmin.domain.User;
import com.adamluptakosice.usercrdmin.exception.UnableFindResourceException;
import com.adamluptakosice.usercrdmin.exception.UnableToDeleteResourceException;
import com.adamluptakosice.usercrdmin.service.UserService;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;

class UserServiceTest implements WithAssertions {

    private UserService userService;
    private UserDao userDaoMock;

    @BeforeEach
    void beforeEach() {
        userDaoMock = Mockito.mock(UserDao.class);
        var ValidatorFactory = Validation.buildDefaultValidatorFactory();
        var validator = ValidatorFactory.getValidator();
        userService = new UserService(userDaoMock, validator);
    }

    @Test
    void shouldAddUser() {
        // given
        var expected = new User(1L, "a1", "Robert");
        var input = new User(1L, "a1", "Robert");
        Mockito.when(userDaoMock.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(userDaoMock.save(any())).thenReturn(expected);

        // when
        final User actual = userService.add(input);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @TestFactory
    Stream<DynamicTest> shouldFailWhenAddUserDueToInvalidInput() {
        var inputList = Arrays.asList(
                new User(null, "", ""),
                new User(1L, "d", ""),
                new User(1L, "sdf", ""),
                new User(-1L, "sdf", ""));
        return inputList.stream()
                .map(user -> DynamicTest.dynamicTest(
                        "addUser: " + user.toString(),
                        () -> {
                            assertThatThrownBy(() -> userService.add(user))
                                    // then
                                    .isInstanceOf(ConstraintViolationException.class)
                                    .hasMessageContaining("Invalid object User");
                        }
                ));
    }


    @Test
    void shouldDeleteAllUsers() {
        // given
        Mockito.doNothing().when(userDaoMock).deleteAll();

        // when
        // then
        assertThatNoException().isThrownBy(() -> userService.deleteAll());
    }

    @Test
    void shouldFailWhenDeleteAllUsersDueToDataSourceNotAvailable() {
        // given
        Mockito.doThrow(new UnableToDeleteResourceException("Unable to delete users", new SQLException()))
                .when(userDaoMock).deleteAll();

        // when
        assertThatThrownBy(() -> userService.deleteAll())
                // then
                .isInstanceOf(UnableToDeleteResourceException.class)
                .hasMessageContaining("Unable to delete users");
    }


    @Test
    void shouldFindAllUsers() {
        // given
        var expectedList = List.of(new User(1L, "a1", "Robert"),
                new User(2L, "a1", "Robert"));
        Mockito.when(userDaoMock.findAll()).thenReturn(expectedList);

        // when
        final var actualList = userService.findAll();

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void shouldFailWhenFindAllUsersDueToDataSourceNotAvailable() {
        // given
        Mockito.doThrow(new UnableFindResourceException("Unable to find resource", new SQLException()))
                .when(userDaoMock).findAll();

        // when
        assertThatThrownBy(() -> userService.findAll())
                // then
                .isInstanceOf(UnableFindResourceException.class)
                .hasMessageContaining("Unable to find resource");
    }

}
