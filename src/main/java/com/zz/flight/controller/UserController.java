package com.zz.flight.controller;

import com.google.common.collect.Maps;
import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.User;
import com.zz.flight.service.FileService;
import com.zz.flight.service.UserService;
import com.zz.flight.util.MD5Util;
import com.zz.flight.util.PropertyUtil;
import com.zz.flight.util.cookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@CrossOrigin(origins = "*",allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    //创建用户
    @PostMapping("/new")
    @ResponseBody
    public ServerResponse addUser(User user){
        return userService.addUser(user);
    }

//    //注册管理员
//    @PostMapping("/admin")
//    @ResponseBody
//    public ServerResponse addAdmin(String password,String email,String adminToken){
//        if (!adminToken.equals(Const.ADMIN_TOKEN)){
//            return ServerResponse.creatByErrorMessage("密码错误");
//        }
//        return userService.addAdmin(email,password);
//    }


    //注册接机人员
    @PostMapping("/volunteer")
    @ResponseBody
    public ServerResponse addVolunteer(User user){
        return userService.addVolunteer(user);
    }

      /*================================================================
                Admin
     ===========================*/

    //列入黑名单
    @DeleteMapping("/{id}")
    @ResponseBody
    public ServerResponse deleteUser(@PathVariable("id") Long id,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null||user.getRole()!=Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorMessage("没有权限");
        return userService.deleteUser(id);
    }

    //移出黑名单
    @PatchMapping("/{id}")
    @ResponseBody
    public ServerResponse validateUser(@PathVariable("id")Long id,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null||user.getRole()!=Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorMessage("没有权限");
        return userService.validateUser(id);
    }

    //=============================================================

    //检查用户名邮箱是否注册
    @PostMapping("/check_valid")
    @ResponseBody
    public ServerResponse checkValid(String str,String type){
        return userService.checkValid(str,type);
    }


    //login
    @PostMapping
    @ResponseBody
    public ServerResponse logIn(@RequestParam(required = false)String email, @RequestParam(required = false) String password, HttpSession session, HttpServletRequest request
    , HttpServletResponse response){
        if(!StringUtils.isBlank(email)&&!StringUtils.isBlank(password)){
            //密码加密
            password = MD5Util.MD5EncodeUtf8(password);
            ServerResponse<User> sresponse = userService.login(email,password);
            //登录成功
            if(sresponse.isSuccess()){
                cookieUtil.addCookie(response,"flightGWU_pass", sresponse.getData().getPassword(),60*24*60*60);
                cookieUtil.addCookie(response,"flightGWU_email", email ,60*24*60*60);
                //密码置空
                sresponse.getData().setPassword(StringUtils.EMPTY);
                session.setAttribute(Const.CURRENT_USER,sresponse.getData());
            }
            return sresponse;
        }else{
            //没有密码或者邮箱则用cookie
            Cookie emailCookie = cookieUtil.getCookieByName(request,"flightGWU_email");
            Cookie passCookie = cookieUtil.getCookieByName(request,"flightGWU_pass");
            if(emailCookie==null||passCookie==null||emailCookie.getValue().equals("")||passCookie.getValue().equals("")){
                //如果为空
                return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            //设置pass和email
            password = passCookie.getValue();
            email = emailCookie.getValue();
            //登录逻辑
            ServerResponse<User> sresponse = userService.login(email,password);
            if(sresponse.isSuccess()){
                //密码置空
                sresponse.getData().setPassword(StringUtils.EMPTY);
                session.setAttribute(Const.CURRENT_USER,sresponse.getData());
            }
            return sresponse;
        }

    }

    //检查登录状态并返回user
    @GetMapping("/check_login")
    @ResponseBody
    public ServerResponse<User> checkLogin(HttpSession session){
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(curUser==null) return ServerResponse.creatByErrorMessage("用户没有登录");
        return ServerResponse.creatBySuccess(curUser);
    }

    //用户退出
    @GetMapping("/logout")
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session,HttpServletRequest request
            , HttpServletResponse response){
        session.removeAttribute(Const.CURRENT_USER);
        //删除cookie
        cookieUtil.addCookie(response,"flightGWU_pass", "",60*24*60*60);
        cookieUtil.addCookie(response,"flightGWU_email", "" ,60*24*60*60);
        return ServerResponse.creatBySuccessMessage("Log out success");
    }

    //用户修改
    @PatchMapping
    @ResponseBody
    public ServerResponse<User> updateUser(User user,HttpSession session){
        //check if log in
        User currentUser =(User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null)return ServerResponse.creatByErrorMessage("User doesn't login");
        user.setId(currentUser.getId());
        ServerResponse response = userService.updateUser(user);
        if(response.isSuccess()) session.setAttribute(Const.CURRENT_USER,response.getData());
        return response;
    }

    //上传头像
    @PostMapping("/avatar")
    @ResponseBody
    public ServerResponse uploadAvatar(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile multipartFile, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        String path = request.getSession().getServletContext().getRealPath("upload");
        //上传
        String targetFileName = fileService.upload(multipartFile,path);
        String url = PropertyUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        //设置头像;
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        ServerResponse response = userService.updateAvatar(url,user.getId());
        if(!response.isSuccess()) return response;
        return ServerResponse.creatBySuccess(fileMap);
    }



    //获取某个账户的信息
    @GetMapping("/{id}")
    @ResponseBody
    public ServerResponse<User> getInfo(@PathVariable("id") Long id,HttpSession session){
        if(id==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(curUser==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return userService.getInfo(id,curUser);
    }

    //获取当前用户信息
    @GetMapping
    @ResponseBody
    public ServerResponse<User> getCurUser(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(), ResponseCode.NEEDLOG_IN.getDesc());
        //置空密码
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess(user);
    }

    //send email 激活
    @GetMapping("/email/{id}")
    @ResponseBody
    public ServerResponse sendEmail(@PathVariable("id")Long  id){
        return userService.getValidateEmail(id);
    }

    //激活地址
    @PatchMapping("/email/{id}/{token}")
    @ResponseBody
    public ServerResponse<String> validateEmail(@PathVariable("id")Long id,@PathVariable("token")String token){
        return userService.validateEmail(id,token);
    }

    //获取重置密码邮件
    @GetMapping("/password/{username}")
    @ResponseBody
    public ServerResponse getEmailToChangePass(@PathVariable("username") String username){
        return userService.getEmailToChangePass(username);
    }

    //重置密码
    @PatchMapping("/password/email/{username}")
    @ResponseBody
    public ServerResponse changePass(@PathVariable("username")String username,String token,String newPass){
        return userService.changePass(username,token,newPass);
    }

    //登录状态重置密码
    @PatchMapping("/password/{id}")
    @ResponseBody
    public ServerResponse resetPass(@PathVariable("id")Long id,String newPass,HttpSession session){
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(curUser.getId().equals(id)) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return userService.resetPass(id,newPass);
    }

}
