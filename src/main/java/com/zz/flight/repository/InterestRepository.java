package com.zz.flight.repository;

import com.zz.flight.entity.Interest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest,Long> {

    List<Interest> findAllByRequestUserId(Long requestUserId);

    List<Interest> findAllByRequestId(Long requestId);

    Interest findByRequestIdAndVolunteerId(Long requestId,Long volunteerId);

}
