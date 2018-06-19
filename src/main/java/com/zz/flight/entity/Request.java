package com.zz.flight.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.lang.annotation.Documented;
import java.util.Date;

@Entity
@Table(name = "request",schema = "targetSchemaName")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requestUserId;

    private Integer numOfLike;

    private String airport;

    private Integer baggage;

    private String homeTown;

    private String graduatedFrom;

    private Integer gender;

    private String avatar;

    private Integer numOfPeople;

    private String destination;

    private String time;

    private String flightInfo;

    private String description;

    private Integer status;

    private Date updateTime;

    private Date createTime;

    public Request(){}


    public Request(Long id,Integer gender,String avatar,Integer baggage,Integer like,Integer numOfPeople ,String homeTown ,String graduatedFrom,String airport, Long requestUserId, String destination, String description, String flightInfo, Integer status, String time, Date updateTime, Date createTime){
        this.id = id;
        this.avatar = avatar;
        this.gender = gender;
        this.baggage = baggage;
        this.graduatedFrom = graduatedFrom;
        this.homeTown = homeTown;
        this.numOfLike = like;
        this.numOfPeople = numOfPeople;
        this.requestUserId = requestUserId;
        this.description = description;
        this.airport = airport;
        this.destination = destination;
        this.flightInfo = flightInfo;
        this.status = status;
        this.time = time;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(Long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(String flightInfo) {
        this.flightInfo = flightInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getBaggage() {
        return baggage;
    }

    public void setBaggage(Integer baggage) {
        this.baggage = baggage;
    }

    public Integer getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(Integer numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public Integer getLike() {
        return numOfLike;
    }

    public void setLike(Integer like) {
        this.numOfLike = like;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getGraduatedFrom() {
        return graduatedFrom;
    }

    public void setGraduatedFrom(String graduatedFrom) {
        this.graduatedFrom = graduatedFrom;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
