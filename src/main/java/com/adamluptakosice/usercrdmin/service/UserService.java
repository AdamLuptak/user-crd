package com.adamluptakosice.usercrdmin.service;

import com.adamluptakosice.usercrdmin.dao.UserDao;
import com.adamluptakosice.usercrdmin.domain.User;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;

public class UserService {

    private final UserDao userDao;
    private final Validator validator;

    public UserService(UserDao userDao, Validator validator) {
        this.userDao = userDao;
        this.validator = validator;
    }

    public User add(User user) {
        validate(user);
        return userDao.save(user);
    }

    private void validate(User user) {
        var violationList = validator.validate(user);
        if (!violationList.isEmpty()) {
            throw new ConstraintViolationException("Invalid object User", violationList);
        }
    }

    public Integer deleteAll() {
        return userDao.deleteAll();
    }

    public List<User> findAll() {
        return userDao.findAll();
    }
}
