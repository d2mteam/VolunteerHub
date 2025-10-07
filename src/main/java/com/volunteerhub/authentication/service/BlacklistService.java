package com.volunteerhub.authentication.service;

public interface BlacklistService {
    boolean validateUser(String username);
    void addUserToBlacklist(String username);
    void removeUserFromBlacklist(String username);
}
