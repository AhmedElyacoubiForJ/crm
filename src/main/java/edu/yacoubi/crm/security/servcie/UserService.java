package edu.yacoubi.crm.security.servcie;

import edu.yacoubi.crm.security.dto.UserRequestDTO;
import edu.yacoubi.crm.security.model.User;

public interface UserService {
    User registerNewUser(UserRequestDTO userRequestDTO);
    void login(String username, String password);
}
