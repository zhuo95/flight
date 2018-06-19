package com.zz.flight.service.impl;

import com.google.common.collect.Lists;
import com.zz.flight.common.Const;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.Interest;
import com.zz.flight.entity.Request;
import com.zz.flight.entity.User;
import com.zz.flight.repository.InterestRepository;
import com.zz.flight.repository.RequestRepository;
import com.zz.flight.repository.UserRepository;
import com.zz.flight.service.InterestService;
import com.zz.flight.vo.RequestUserShowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("InterestService")
public class InterestServiceImpl implements InterestService {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private InterestRepository interestRepository;
    @Autowired
    private UserRepository userRepository;


    /**
     *@Description:getRequestInfo,按照requestId,查找匹配结果并返回有哪些人愿意接机
     *@Param:[requestUserId]
     *@Return:com.zz.flight.common.ServerResponse<com.zz.flight.vo.RequestUserShowVo>
     *@Author:zz
     */
    public ServerResponse<RequestUserShowVo> getRequestInfo(Long requestUserId){
        Request request = requestRepository.findByRequestUserIdAndStatus(requestUserId, Const.RequestStatus.REQUEST_VALID);
        if(request==null) return ServerResponse.creatByErrorMessage("You haven't applied yet");
        List<Interest> interests = interestRepository.findAllByRequestId(request.getId());
        List<User> likedUsers =  Lists.newArrayList();
        for(Interest i : interests){
            Long likedUserId = i.getVolunteerId();
            User user = userRepository.findById(likedUserId).orElse(null);
            if(user!=null) likedUsers.add(user);
        }
        RequestUserShowVo requestUserShowVo = new RequestUserShowVo();
        requestUserShowVo.setLikedUser(likedUsers);
        requestUserShowVo.setRequest(request);
        return ServerResponse.creatBySuccess(requestUserShowVo);
    }


    //取消点赞
    /**
     *@Description:cancelRequest
     *@Param:[requestId, volunteerId]
     *@Return:com.zz.flight.common.ServerResponse
     *@Author:zz
     */
    public ServerResponse cancelRequest(Long requestId,Long volunteerId){
        Interest interest = interestRepository.findByRequestIdAndVolunteerId(requestId,volunteerId);
        if(interest==null) return ServerResponse.creatByErrorMessage("You didn't apply");
        interestRepository.deleteById(interest.getId());
        //like count--
        Request request = requestRepository.findById(requestId).orElse(null);
        request.setLike(request.getLike()-1);
        request.setUpdateTime(new Date());
        requestRepository.save(request);

        return ServerResponse.creatBySuccessMessage("Success");
    }


    /**
     *@Description:getIfLiked,查看是否点赞
     *@Param:[volunteerId, Requestid]
     *@Return:com.zz.flight.common.ServerResponse
     *@Author:zz
     */
    public ServerResponse getIfLiked( Long volunteerId ,Long requestid){
        Interest interest = interestRepository.findByRequestIdAndVolunteerId(requestid,volunteerId);
        if(interest==null) return ServerResponse.creatByErrorMessage("false");
        return ServerResponse.creatBySuccessMessage("true");
    }
}
