package com.harsha.account.service;

import com.harsha.account.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}
