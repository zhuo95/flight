package com.zz.flight.service;

import com.zz.flight.common.ServerResponse;
import com.zz.flight.vo.RequestUserShowVo;

public interface InterestService {

    ServerResponse<RequestUserShowVo> getRequestInfo(Long requestUserId);

    ServerResponse cancelRequest(Long requestId,Long volunteerId);

    ServerResponse getIfLiked( Long volunteerId ,Long requestid);
}

