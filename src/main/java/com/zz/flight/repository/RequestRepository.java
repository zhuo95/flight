package com.zz.flight.repository;

import com.zz.flight.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


public interface RequestRepository extends JpaRepository<Request,Long> {

    Page<Request> findAllByStatus(int status,Pageable pageable);

    Request findByRequestUserIdAndStatus(Long id,int status);

    Page<Request> findAllByHomeTownLikeAndStatusOrGraduatedFromLikeAndStatus(String homeTown,int status1,String graduatedFrom,int status2,Pageable pageable);

    List<Request>  findAllByCreateTimeAfterAndStatus(Date date,int status);

}
