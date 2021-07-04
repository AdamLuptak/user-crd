package com.adamluptakosice.usercrdmin.dao;


import com.adamluptakosice.usercrdmin.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    User save(User user);

    Optional<User> findById(Long id);

    Integer deleteAll();

    List<User> findAll();
}
