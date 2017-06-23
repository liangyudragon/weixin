package com.tramp.wechat4j.wechat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 特殊账号
 * Created by chen on 2017/6/22.
 */
public class SpecialUser {
    @JsonProperty("Alias")
    private String alias;
    @JsonProperty("AppAccountFlag")
    private int appAccountFlag;
    @JsonProperty("AttrStatus")
    private int attrStatus;
    @JsonProperty("ChatRoomId")
    private int chatRoomId;
    @JsonProperty("City")
    private String city;
    @JsonProperty("ContactFlag")
    private int contactFlag;
    @JsonProperty("DisplayName")
    private String displayName;
    @JsonProperty("EncryChatRoomId")
    private String encryChatRoomId;
    @JsonProperty("HeadImgUrl")
    private String headImgUrl;
    @JsonProperty("HideInputBarFlag")
    private int hideInputBarFlag;
    @JsonProperty("IsOwner")
    private int isOwner;
    @JsonProperty("KeyWord")
    private String keyWord;
    @JsonProperty("MemberCount")
    private int memberCount;
    @JsonProperty("NickName")
    private String nickName;
    @JsonProperty("OwnerUin")
    private int ownerUin;
    @JsonProperty("PYInitial")
    private String pYInitial;
    @JsonProperty("PYQuanPin")
    private String pYQuanPin;
    @JsonProperty("Province")
    private String province;
    @JsonProperty("RemarkName")
    private String remarkName;
    @JsonProperty("RemarkPYInitial")
    private String remarkPYInitial;
    @JsonProperty("RemarkPYQuanPin")
    private String remarkPYQuanPin;
    @JsonProperty("Sex")
    private int sex;
    @JsonProperty("Signature")
    private String signature;
    @JsonProperty("SnsFlag")
    private int snsFlag;
    @JsonProperty("StarFriend")
    private int starFriend;
    @JsonProperty("Statues")
    private int statues;
    @JsonProperty("Uin")
    private int uin;
    @JsonProperty("UniFriend")
    private int uniFriend;
    @JsonProperty("UserName")
    private String userName;
    @JsonProperty("VerifyFlag")
    private int verifyFlag;
    @JsonProperty("MemberList")
    private List<?> memberList;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getAppAccountFlag() {
        return appAccountFlag;
    }

    public void setAppAccountFlag(int appAccountFlag) {
        this.appAccountFlag = appAccountFlag;
    }

    public int getAttrStatus() {
        return attrStatus;
    }

    public void setAttrStatus(int attrStatus) {
        this.attrStatus = attrStatus;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getContactFlag() {
        return contactFlag;
    }

    public void setContactFlag(int contactFlag) {
        this.contactFlag = contactFlag;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEncryChatRoomId() {
        return encryChatRoomId;
    }

    public void setEncryChatRoomId(String encryChatRoomId) {
        this.encryChatRoomId = encryChatRoomId;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getHideInputBarFlag() {
        return hideInputBarFlag;
    }

    public void setHideInputBarFlag(int hideInputBarFlag) {
        this.hideInputBarFlag = hideInputBarFlag;
    }

    public int getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getOwnerUin() {
        return ownerUin;
    }

    public void setOwnerUin(int ownerUin) {
        this.ownerUin = ownerUin;
    }

    public String getpYInitial() {
        return pYInitial;
    }

    public void setpYInitial(String pYInitial) {
        this.pYInitial = pYInitial;
    }

    public String getpYQuanPin() {
        return pYQuanPin;
    }

    public void setpYQuanPin(String pYQuanPin) {
        this.pYQuanPin = pYQuanPin;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public String getRemarkPYInitial() {
        return remarkPYInitial;
    }

    public void setRemarkPYInitial(String remarkPYInitial) {
        this.remarkPYInitial = remarkPYInitial;
    }

    public String getRemarkPYQuanPin() {
        return remarkPYQuanPin;
    }

    public void setRemarkPYQuanPin(String remarkPYQuanPin) {
        this.remarkPYQuanPin = remarkPYQuanPin;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getSnsFlag() {
        return snsFlag;
    }

    public void setSnsFlag(int snsFlag) {
        this.snsFlag = snsFlag;
    }

    public int getStarFriend() {
        return starFriend;
    }

    public void setStarFriend(int starFriend) {
        this.starFriend = starFriend;
    }

    public int getStatues() {
        return statues;
    }

    public void setStatues(int statues) {
        this.statues = statues;
    }

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }

    public int getUniFriend() {
        return uniFriend;
    }

    public void setUniFriend(int uniFriend) {
        this.uniFriend = uniFriend;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getVerifyFlag() {
        return verifyFlag;
    }

    public void setVerifyFlag(int verifyFlag) {
        this.verifyFlag = verifyFlag;
    }

    public List<?> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<?> memberList) {
        this.memberList = memberList;
    }
}
