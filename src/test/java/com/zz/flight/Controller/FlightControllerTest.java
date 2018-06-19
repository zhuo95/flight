package com.zz.flight.Controller;


import com.zz.flight.entity.User;
import com.zz.flight.repository.UserRepository;
import com.zz.flight.service.FlightService;
import com.zz.flight.service.UserService;;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FlightControllerTest {
    @Autowired
    UserService userService;
    @Autowired
    FlightService flightService;
    @Autowired
    UserRepository userRepository;


    @Test
    public void testGetList(){

        System.out.println("========================================================================================");
        System.out.println();
        System.out.println("=========================================================================================");
    }
}
