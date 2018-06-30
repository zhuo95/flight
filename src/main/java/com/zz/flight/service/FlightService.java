package com.zz.flight.service;

import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightService {

    ServerResponse<Page> listAllByStatus(int status,int pageIndex,int pageSize);

    ServerResponse<Request> addRequest(Request request,Long id,String hometown,String graduatedFrom,Integer gender,String avatar);

    ServerResponse takeRequest(Long requestId,Long userId,Integer charge);

    ServerResponse<Request> getRequestById(Long id);

    ServerResponse<Page> listAll(int pageIndex, int pageSize);

    ServerResponse<Page> findRequestByLocation(int pageIndex,int pageSize,String location);

    ServerResponse cancelById(Long id,int role,Long userId);

    ServerResponse<Request> modifyRequest(Request request,Long userId);

    void emailTask(int hour);
}
