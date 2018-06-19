package com.zz.flight.controller;

import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.Request;
import com.zz.flight.entity.User;
import com.zz.flight.service.FlightService;
import com.zz.flight.service.InterestService;
import com.zz.flight.vo.RequestUserShowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private FlightService flightService;
    @Autowired
    private InterestService interestService;

    //获取所有valid的请求列表
    @GetMapping
    @ResponseBody
    public ServerResponse<Page> getList( @RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                            @RequestParam(value = "pageSize",defaultValue = "9") int pageSize){

        return flightService.listAllByStatus(0,pageIndex,pageSize);

    }

    //获取所有
    @GetMapping("/all")
    @ResponseBody
    public ServerResponse<Page> getAllList(@RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                           @RequestParam(value = "pageSize",defaultValue = "9") int pageSize){
        return flightService.listAll(pageIndex,pageSize);
    }


    //新建请求
    @PostMapping
    @ResponseBody
    public ServerResponse<Request> addRequest(Request request,HttpSession session){
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        //检查是否登录
        if(curUser==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        //检查邮箱是否认证
        //if(curUser.getEmailChecked()==Const.EmailChecked.EMAIL_INVALID) return ServerResponse.creatByErrorMessage("请先验证邮箱");
        if(curUser.getRole()!=Const.Role.ROLE_CUSTOMER || curUser.getStatus()!=Const.Status.USER_VALID) return ServerResponse.creatByErrorMessage("you are not a new student");
        return flightService.addRequest(request,curUser.getId(),curUser.getHomeTown(),curUser.getGraduatedFrom(),curUser.getGender(),curUser.getAvatar());
    }


    //接受请求
    @PatchMapping("/{id}")
    @ResponseBody
    public ServerResponse takeRequest(@PathVariable("id")Long id, @RequestParam(value = "charge",defaultValue = "0") Integer charge,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        if(user.getEmailChecked()==Const.EmailChecked.EMAIL_INVALID) return ServerResponse.creatByErrorMessage("Please validate your email first");
        if(user.getRole()==Const.Role.ROLE_ADMIN||user.getRole()==Const.Role.ROLE_CUSTOMER || user.getStatus()==Const.Status.USER_INVALID){
            return ServerResponse.creatByErrorMessage("You can't pick up new students");
        }
        return flightService.takeRequest(id,user.getId(),charge);
    }


    //按照requestId 来获取请求信息
    @GetMapping("/{id}")
    @ResponseBody
    public ServerResponse<Request> getRequestById(@PathVariable("id") Long id){
        return flightService.getRequestById(id);
    }


    //按照地点关键词模糊查询
    @GetMapping("/location")
    @ResponseBody
    public ServerResponse<Page> getRequestByLocation(HttpSession session,@RequestParam(value = "location",defaultValue = "北京")String location,
                                                     @RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                                     @RequestParam(value = "pageSize",defaultValue = "9") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
       // if(user==null||user.getRole()==Const.Role.ROLE_CUSTOMER) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return flightService.findRequestByLocation(pageIndex,pageSize,location);
    }


    //获取当前用户的请求和感兴趣的人，当前用户的
    @GetMapping("/user")
    @ResponseBody
    public ServerResponse<RequestUserShowVo> getRequestByUsername(HttpSession session,
                                                                  @RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return interestService.getRequestInfo(user.getId());
    }


    //按照request id来删除
    @DeleteMapping("/{id}")
    @ResponseBody
    public ServerResponse cancelById(@PathVariable("id") Long id,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return flightService.cancelById(id,user.getRole(),user.getId());
    }

    //取消（请求）点赞
    @DeleteMapping("/like/{id}")
    @ResponseBody
    public ServerResponse cancelRequest(@PathVariable("id") Long id,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        if(user.getRole()==Const.Role.ROLE_CUSTOMER||user.getRole()==Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorMessage("您不能接机");
        return interestService.cancelRequest(id,user.getId());
    }

    //当前用户修改，新生修改request
    @PatchMapping("/modify")
    @ResponseBody
    public ServerResponse<Request> modifyRequest(Request request,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return flightService.modifyRequest(request,user.getId());
    }

    //查看有没有点过赞
    @GetMapping("/like/{id}")
    public ServerResponse getIfLiked(HttpSession session,@PathVariable("id") Long id){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return interestService.getIfLiked(user.getId(),id);
    }

}
