package com.zz.flight.service.impl;

import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.common.TokenCache;
import com.zz.flight.entity.Request;
import com.zz.flight.entity.User;
import com.zz.flight.repository.RequestRepository;
import com.zz.flight.repository.UserRepository;
import com.zz.flight.service.UserService;
import com.zz.flight.util.EmailUtil;
import com.zz.flight.util.MD5Util;
import com.zz.flight.util.PropertyUtil;
import com.zz.flight.util.UpdateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.UUID;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;


    //创建用户
    public ServerResponse addUser(User user){
        //检查邮箱是否用过
        ServerResponse validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //status设置
        user.setStatus(Const.Status.USER_VALID);
        //设置默认头像
        if(StringUtils.isBlank(user.getAvatar())) user.setAvatar("http://img.zhuo9529.com/7407b75d-e535-4640-a0c2-0638ac087841.jpg");//青蛙
        //设置创建时间
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        //set email checked
        user.setEmailChecked(Const.EmailChecked.EMAIL_INVALID);
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("Sign up success");
    }

    //创建管理员
    public ServerResponse addAdmin(String email,String password){
        //检查邮箱是否用过
        ServerResponse validResponse = this.checkValid(email,Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        User user = new User();
        user.setEmailChecked(Const.EmailChecked.EMAIL_VALID);
        user.setEmail(email);
        user.setPassword(MD5Util.MD5EncodeUtf8(password));
        user.setRole(Const.Role.ROLE_ADMIN);
        user.setStatus(Const.Status.USER_VALID);
        user.setAvatar("http://img.zhuo9529.com/7407b75d-e535-4640-a0c2-0638ac087841.jpg");//青蛙
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);
        userRepository.save(user);
        return ServerResponse.creatBySuccess("success",user);
    }

    //创建volunteer
    public ServerResponse addVolunteer(User user){
        String email = user.getEmail();
        if(StringUtils.isBlank(email)) return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        String edu = email.substring(email.lastIndexOf('.')+1);
        if(!StringUtils.equals(edu,"edu")) return ServerResponse.creatByErrorMessage("Only support edu email");
        //检查邮箱是否注册
        ServerResponse validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_V0LUNTEER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //status设置
        user.setStatus(Const.Status.USER_VALID);
        if(StringUtils.isBlank(user.getAvatar())) user.setAvatar("http://img.zhuo9529.com/7407b75d-e535-4640-a0c2-0638ac087841.jpg");//青蛙
        user.setEmailChecked(Const.EmailChecked.EMAIL_INVALID);
        //设置创建时间
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userRepository.save(user);
        //发邮件
        //随机产生string
        String token = UUID.randomUUID().toString();
        //token 放入cache
        TokenCache.setKey(TokenCache.TOKEN_PREFIX+user.getId(),token);
        String to = user.getEmail();
        String validate = "<h1>Please click the url to activate your email:</h1> <p> "+ PropertyUtil.getProperty("email.prefix")+"validation?id="+ user.getId()+ "&token="+token + "</p>";
        if(!EmailUtil.sendgrid(to,validate)){
            return ServerResponse.creatByErrorMessage("send email error");
        }
        return ServerResponse.creatBySuccess("Success,Please check your email");
    }

    //检查是否注册
    public ServerResponse<String> checkValid(String str, String type){
        if(str == null) return ServerResponse.creatByErrorMessage("Wrong argument");
        if(StringUtils.isNotBlank(type)){
            if(Const.EMAIL.equals(type)){
               if(userRepository.findByEmail(str)!=null){
                   return ServerResponse.creatByErrorMessage("Email has been used");
               }
            }
        }else {
            return ServerResponse.creatByErrorMessage("Wrong argument");
        }
        return ServerResponse.creatBySuccessMessage("Check success");
    }

    //Log in
    public ServerResponse<User> login(String email, String password){
        User user = userRepository.findByEmail(email);
        //检查用户是否存在
        if(user == null) return ServerResponse.creatByErrorMessage("Email hasn't registered");
        //检查密码是否正确
        if(!StringUtils.equals(password,user.getPassword())){
            return ServerResponse.creatByErrorMessage("Password wrong");
        }
        return ServerResponse.creatBySuccess("Login success",user);
    }


    //用户修改
    public ServerResponse<User> updateUser(User user){
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        if(userRepository.findByEmail(user.getEmail())!= null) return ServerResponse.creatByErrorMessage("Email has been used");
        User pre = userRepository.findById(user.getId()).orElse(null);
        //新建一个User 只能更新规定的内容
        User newUser = new User();
        newUser.setWechat(user.getWechat());
        newUser.setActualName(user.getActualName());
        newUser.setGender(user.getGender());
        newUser.setGraduatedFrom(user.getGraduatedFrom());
        newUser.setHomeTown(user.getHomeTown());
        newUser.setMajor(user.getMajor());
        //获取当前时间
        newUser.setUpdateTime(new Date());
        //更新工具 不更新为null的
        UpdateUtil.copyNullProperties(pre,newUser);
        User savedUser = userRepository.save(newUser);
        savedUser.setPassword(StringUtils.EMPTY);
        savedUser.setRole(null);
        //如果newuser更新了honetown或者school，用户有已经提交的request，更新request
        if(!StringUtils.isBlank(user.getHomeTown())||!StringUtils.isBlank(user.getGraduatedFrom())){
            Request request = requestRepository.findByRequestUserIdAndStatus(savedUser.getId(),Const.RequestStatus.REQUEST_VALID);
            if(request!=null){
                if(!StringUtils.isBlank(user.getHomeTown())) request.setHomeTown(user.getHomeTown());
                if(!StringUtils.isBlank(user.getGraduatedFrom())) request.setGraduatedFrom(user.getGraduatedFrom());
            }
            requestRepository.save(request);
        }
        return ServerResponse.creatBySuccess(savedUser);
    }

    //save avatar
    public ServerResponse updateAvatar(String url,Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if(user==null) return ServerResponse.creatByErrorMessage("cant find the user");
        Request request = requestRepository.findByRequestUserIdAndStatus(userId,Const.RequestStatus.REQUEST_VALID);
        if(request!=null){
            request.setAvatar(url);
            request.setUpdateTime(new Date());
            requestRepository.save(request);
        }
        user.setAvatar(url);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccess();
    }


    //查询用户信息
    public ServerResponse<User> getInfo(Long id, User curUser){
        //找到请求的用户 find
        User find = userRepository.findById(id).orElse(null);
        if(find==null) return ServerResponse.creatByErrorMessage("Cant find the user");
        //不是自己或者管理员或者是你的接机人员,则不能看到手机号
        if(!curUser.getId().equals(id)  && curUser.getRole()!=Const.Role.ROLE_ADMIN){
            //真实姓名置空
            find.setActualName(StringUtils.EMPTY);
        }
        //密码置空
        find.setPassword(StringUtils.EMPTY);
        //角色置空
        find.setRole(null);
        return ServerResponse.creatBySuccess(find);
    }


    //获取邮件
    public ServerResponse<String> getValidateEmail(Long id){
        User user = userRepository.findById(id).orElse(null);
        if(user==null) return ServerResponse.creatByErrorMessage("没有该用户");
        if(user.getEmailChecked()==Const.EmailChecked.EMAIL_VALID) return ServerResponse.creatByErrorMessage("已经激活");
        //随机产生string
        String token = UUID.randomUUID().toString();
        //token 放入cache
        TokenCache.setKey(TokenCache.TOKEN_PREFIX+user.getId(),token);
        //发邮件
        String to = user.getEmail();

        String validate = "<h1>Please click the url to activate your email:</h1> " +
                "<p> "+ PropertyUtil.getProperty("email.prefix")+"active?id=" + user.getId()+"&token="+token +  "</p>";

        if(!EmailUtil.sendgrid(to,validate)){
            return ServerResponse.creatByErrorMessage("send email error");
        }


//        String from = "m18667015308@163.com";
//        String content = "http://flight.zhuo9529.com/validateEmail.html?id="+user.getId()+"&token="+token;
//        String title = "Validate your email";
//        if(!EmailUtil.sendEmail(from,to,content,title)){
//            return ServerResponse.creatByErrorMessage("send email error");
//        }
        return ServerResponse.creatBySuccessMessage("Please check your email");
    }

    //激活邮件
    public ServerResponse<String> validateEmail(Long id, String token){
        String getToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX+id);
        if(!StringUtils.equals(token,getToken)){
            return ServerResponse.creatByErrorMessage("Token is not right");
        }
        User user = userRepository.findById(id).orElse(null);
        if(user==null) return ServerResponse.creatByErrorMessage("can't find the user");
        user.setEmailChecked(Const.EmailChecked.EMAIL_VALID);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("validate success");
    }

    //获取邮件改密码
    public ServerResponse<String> getEmailToChangePass(String email){
        User user = userRepository.findByEmail(email);
        if(user==null) return ServerResponse.creatByErrorMessage("cant find user with this username");
        String to = user.getEmail();
        String from = "flightjoe@gmail.com";
        String title = "Reset your password";
        String token = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.TOKEN_PREFIX+user.getId(),token);
        String content = PropertyUtil.getProperty("email.prefix")+"changePwd.html?"+"token="+token+"&"+"email="+email;
        if(!EmailUtil.sendEmail(from,to,content,title)){
            return ServerResponse.creatByErrorMessage("send email error");
        }
        return ServerResponse.creatBySuccessMessage("Please check your email");
    }

    //用邮件改密码
    public ServerResponse changePass(String email,String token,String newPass){
        String tokenToCheck = TokenCache.getKey(TokenCache.TOKEN_PREFIX+email);
        if(!StringUtils.equals(token,tokenToCheck)){
            return ServerResponse.creatByErrorMessage("Token is not right");
        }
        User user = userRepository.findByEmail(email);
        if(user==null) return ServerResponse.creatBySuccessMessage("cant find the user");
        //存加密过的
        user.setPassword(MD5Util.MD5EncodeUtf8(newPass));
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("Reset success");
    }

    //登录后改密码
    public ServerResponse resetPass(Long id,String newPass){
        User user = userRepository.findById(id).orElse(null);
        if(user==null) return ServerResponse.creatByErrorMessage("cant find the user");
        String MD5NewPass = MD5Util.MD5EncodeUtf8(newPass);
        user.setPassword(MD5NewPass);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("Reset success");
    }


    /**
     *====================================================================
     *
     * Admin 使用
     * ===================================================================
     */

    //列出所有用户
    public ServerResponse<Page> listAllUsers(int pageIndex,int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);
        return ServerResponse.creatBySuccess(page);
    }

    //列出所有接机人员
    public ServerResponse<Page> listAllVolunteer(int pageIndex, int pageSize){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<User> page = userRepository.findAllByRole(Const.Role.ROLE_V0LUNTEER,pageable);
        return ServerResponse.creatBySuccess(page);
    }

    //列入黑名单
    public ServerResponse deleteUser(Long id){
        User user = userRepository.findById(id).orElse(null);
        if(user==null) return ServerResponse.creatByErrorMessage("找不到该用户");
        if(user.getRole()==Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorMessage("没有权限");
        user.setStatus(Const.Status.USER_INVALID);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccess("操作成功");
    }

    public ServerResponse validateUser(Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user==null) return ServerResponse.creatByErrorMessage("找不到该用户");
        user.setStatus(Const.Status.USER_VALID);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccess("操作成功");
    }
}
