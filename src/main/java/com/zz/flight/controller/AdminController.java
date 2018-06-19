package com.zz.flight.controller;


import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.User;
import com.zz.flight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@CrossOrigin(origins = "*",allowCredentials = "true")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;


    //列出所有接机人员
    @GetMapping
    @ResponseBody
    public ServerResponse<Page> getAll(HttpSession session,@RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                       @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        if(user.getRole()!=Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return userService.listAllVolunteer(pageIndex,pageSize);
    }
    //列出所有用户
    @GetMapping("/users")
    @ResponseBody
    public ServerResponse<Page> getAllUsers(HttpSession session,@RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                            @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //没登录或者不是管理员
        if(user==null||user.getRole()!=Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return userService.listAllUsers(pageIndex,pageSize);
    }


}
