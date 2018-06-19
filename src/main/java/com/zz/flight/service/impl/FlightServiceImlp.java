package com.zz.flight.service.impl;

import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.Interest;
import com.zz.flight.entity.Request;
import com.zz.flight.repository.InterestRepository;
import com.zz.flight.repository.RequestRepository;
import com.zz.flight.repository.UserRepository;
import com.zz.flight.service.FlightService;
import com.zz.flight.util.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("FlightService")
public class FlightServiceImlp implements FlightService {

    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InterestRepository interestRepository;

    /**
     *@Description:listByStatus,按照请求status查找
     *@Param:[status, pageIndex, pageSize]
     *@Return:com.zz.flight.common.ServerResponse<org.springframework.data.domain.Page>
     *@Author:zz
     */
    public ServerResponse<Page> listAllByStatus(int status, int pageIndex, int pageSize) {
        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);

        Page<Request> page  = requestRepository.findAllByStatus(status,pageable);

        if(page.getTotalElements()>0){
            return ServerResponse.creatBySuccess(page);
        }

        return ServerResponse.creatByErrorMessage("cant find");
    }

    /**
     *@Description:listAll显示所有的请求
     *@Param:[pageIndex, pageSize]
     *@Return:com.zz.flight.common.ServerResponse<org.springframework.data.domain.Page>
     *@Author:zz
     */
    public ServerResponse<Page> listAll(int pageIndex, int pageSize){
        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        Page<Request> page  = requestRepository.findAll(pageable);
        return ServerResponse.creatBySuccess(page);
    }

    /**
     *@Description:addRequest，创建请求
     *@Param:[request, id, hometown, graduatedFrom,gender,avatar]
     *@Return:com.zz.flight.common.ServerResponse<com.zz.flight.entity.Request>
     *@Author:zz
     */
    public ServerResponse<Request> addRequest(Request request,Long id,String hometown , String graduatedFrom,Integer gender,String avatar){
        if(request==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //检查id是否已经有request
        Request alreadyRequest = requestRepository.findByRequestUserIdAndStatus(id,Const.RequestStatus.REQUEST_VALID);
        if(alreadyRequest!=null) return ServerResponse.creatByErrorMessage("You have already applied");
        //设置 提出请求的人的name和id
        request.setRequestUserId(id);
        //设置家乡和graduatedFrom
        request.setHomeTown(hometown);
        //设置男女
        request.setGender(gender);
        //set头像
        request.setAvatar(avatar);
        request.setGraduatedFrom(graduatedFrom);
        //若果信息不全
        if(request.getAirport()==null||request.getFlightInfo()==null||request.getRequestUserId()==null||request.getTime()==null){
            return ServerResponse.creatByErrorMessage("Please fill all information");
        }
        request.setStatus(Const.RequestStatus.REQUEST_VALID);
        request.setLike(0);
        //设置status 和 更新时间
        Date now = new Date();
        request.setUpdateTime(now);
        request.setCreateTime(now);
        //save
        requestRepository.save(request);
        return ServerResponse.creatBySuccess("success",request);
    }
    /**
     *@Description:findRequestByLocation,按照地点查询
     *@Param:[pageIndex, pageSize, hometown, graduatedFrom]
     *@Return:com.zz.flight.common.ServerResponse<org.springframework.data.domain.Page>
     *@Author:zz
     */
    public ServerResponse<Page> findRequestByLocation(int pageIndex,int pageSize,String location){
        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        location = "%"+location+"%";
        Page<Request> requests = requestRepository.findAllByHomeTownLikeAndStatusOrGraduatedFromLikeAndStatus(location,Const.RequestStatus.REQUEST_VALID,location,Const.RequestStatus.REQUEST_VALID,pageable);
        return ServerResponse.creatBySuccess(requests);
    }


    /**
     *@Description:takeRequest,接受请求,其实就是点赞
     *@Param:[requestId, userId, charge]
     *@Return:com.zz.flight.common.ServerResponse
     *@Author:zz
     */
    public ServerResponse takeRequest(Long requestId,Long userId,Integer charge){
        Interest  preInterest = interestRepository.findByRequestIdAndVolunteerId(requestId,userId);
        if(preInterest!=null) return ServerResponse.creatByErrorMessage("You have already applied, please wait for response");
        Request request = requestRepository.findById(requestId).orElse(null);
        if(request==null) return ServerResponse.creatByErrorMessage("cant find request");
        //like count ++
        request.setLike(request.getLike()+1);
        Interest interest = new Interest();
        interest.setRequestId(request.getId());
        interest.setRequestUserId(request.getRequestUserId());
        interest.setVolunteerId(userId);
        interest.setCharge(charge);
        Date now = new Date();
        interest.setCreateTime(now);
        interest.setUpdateTime(now);
        interestRepository.save(interest);
        return ServerResponse.creatBySuccess("success");
    }



    /**
     *@Description:getRequestById按照requestID查找request
     *@Param:[id]
     *@Return:com.zz.flight.common.ServerResponse<com.zz.flight.entity.Request>
     *@Author:zz
     */
    public ServerResponse<Request> getRequestById(Long id){
        Request request = requestRepository.findById(id).orElse(null);
        if(request==null) return ServerResponse.creatByErrorMessage("找不到该信息");
        return ServerResponse.creatBySuccess(request);
    }


    /**
     *@Description:取消request按照requestID
     *@Param:[id, role, userId]
     *@Return:com.zz.flight.common.ServerResponse
     *@Author:zz
     */
    public ServerResponse cancelById(Long id,int role,Long userId){
        Request request = requestRepository.findById(id).orElse(null);
        if(request==null) return ServerResponse.creatByErrorMessage("找不到该信息");
        //不是管理员或者自己提出的需求
        if(role!=Const.Role.ROLE_ADMIN && !userId.equals(request.getRequestUserId())) return ServerResponse.creatByErrorMessage("不能操作");
        //设置成invalid
        request.setStatus(Const.RequestStatus.REQUEST_INVALID);
        request.setUpdateTime(new Date());
        requestRepository.save(request);
        return ServerResponse.creatBySuccessMessage("Success");
    }

    /**
     *@Description:modifyRequest
     *@Param:[request, userId]
     *@Return:com.zz.flight.common.ServerResponse<com.zz.flight.entity.Request>
     *@Author:zz
     */
    public ServerResponse<Request> modifyRequest(Request request,Long userId){
        //取出previous的request
        Request preRequest = requestRepository.findByRequestUserIdAndStatus(userId,Const.RequestStatus.REQUEST_VALID);
        if (preRequest == null) return ServerResponse.creatByErrorMessage("you haven't applied yet");
       //设置之前的值
        request.setId(preRequest.getId());
        request.setRequestUserId(preRequest.getRequestUserId());
        request.setLike(preRequest.getLike());
        request.setStatus(preRequest.getStatus());
        request.setUpdateTime(new Date());
        request.setCreateTime(null);
        //更新
        UpdateUtil.copyNullProperties(preRequest,request);
        Request newRequest = requestRepository.save(request);
        return ServerResponse.creatBySuccess(newRequest);
    }
}
