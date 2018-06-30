package com.zz.flight.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user" ,schema = "targetSchemaName")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer gender; //选填

    private String avatar;

    private String graduatedFrom;//选填

    private String major; //选填

    private String homeTown;//选填

    private String wechat;

    private String actualName;//选填

    private String password;

    private String email; //作为账号

    private Integer emailChecked;

    private Integer status;

    private Integer role;

    private Integer receiveEmail;

    private Date createTime;

    private Date updateTime;

    public User(){}

    public User(String major,String wechat,Integer receiveEmail,String actualName,String avatar,Integer status,String homeTown,String graduatedFrom,Long id,Integer gender ,String email,Integer emailChecked ,String password,Integer role, Date createTime, Date updateTime){
        this.id = id;
        this.avatar = avatar;
        this.actualName = actualName;
        this.receiveEmail = receiveEmail;
        this.homeTown = homeTown;
        this.graduatedFrom = graduatedFrom;
        this.status = status;
        this.major = major;
        this.email = email;
        this.wechat = wechat;
        this.gender = gender;
        this.password = password;
        this.emailChecked = emailChecked;
        this.role = role;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getGraduatedFrom() {
        return graduatedFrom;
    }

    public void setGraduatedFrom(String graduatedFrom) {
        this.graduatedFrom = graduatedFrom;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getActualName() {
        return actualName;
    }

    public void setActualName(String actualName) {
        this.actualName = actualName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEmailChecked() {
        return emailChecked;
    }

    public void setEmailChecked(Integer emailChecked) {
        this.emailChecked = emailChecked;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getReceiveEmail() {
        return receiveEmail;
    }

    public void setReceiveEmail(Integer receiveEmail) {
        this.receiveEmail = receiveEmail;
    }
}
