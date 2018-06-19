package com.zz.flight.entity;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "interest",schema = "targetSchemaName")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requestId;

    private Long volunteerId;

    private Long requestUserId;

    private String description;

    private Integer charge;

    private Date createTime;

    private Date updateTime;

    public Interest(){}

    public Interest(Long id,Long requestId,Long volunteerId,Long requestUserId,String description,Integer charge,Date createTime,Date updateTime){
        this.id = id;
        this.requestId = requestId;
        this.volunteerId = volunteerId;
        this.charge = charge;
        this.description = description;
        this.requestUserId = requestUserId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Long getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(Long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
