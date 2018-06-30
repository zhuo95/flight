package com.zz.flight.service;

import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    ServerResponse addUser(User user);

    ServerResponse addAdmin(String email,String password);

    ServerResponse addVolunteer(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse updateUser(User user);

    ServerResponse updateAvatar(String url,Long userId);

    ServerResponse<User> login(String username, String password);

    ServerResponse<User> getInfo(Long id, User curUser);

    ServerResponse<String> getValidateEmail(Long id);

    ServerResponse<String> validateEmail(Long id,String token);

    ServerResponse<String> getEmailToChangePass(String username);

    ServerResponse changePass(String email,String token,String newPass);

    ServerResponse resetPass(Long id,String newPass);

    ServerResponse<Page> listAllUsers(int pageIndex, int pageSize);

    ServerResponse<Page> listAllVolunteer(int pageIndex, int pageSize);

    ServerResponse deleteUser(Long id);

    ServerResponse validateUser(Long id);

    ServerResponse cancelReceiveEmail(Long userId);
}
