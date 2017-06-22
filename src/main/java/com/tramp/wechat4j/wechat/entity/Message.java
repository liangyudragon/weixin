package com.tramp.wechat4j.wechat.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 消息.
 */
public class Message {
    private String MsgId;
    private String ForwardFlag;
    private String NewMsgId;
    private RecommendInfo RecommendInfo;
    private String VoiceLength;
    private Integer MsgType;
    private String OriContent;
    private String Ticket;
    private String Url;
    private AppInfo AppInfo;
    private String SubMsgType;
    private String Content;
    private String AppMsgType;
    private String ImgWidth;
    private String PlayLength;
    private String StatusNotifyCode;
    private String StatusNotifyUserName;
    private String HasProductId;
    private String MediaId;
    private String ToUserName;
    private String FileSize;
    private String Status;
    private String FromUserName;
    private String ImgHeight;
    private String ImgStatus;
    private String FileName;
    private Long CreateTime;
    private Boolean groupMsg;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getGroupMsg() {
        return groupMsg;
    }

    public void setGroupMsg(Boolean groupMsg) {
        this.groupMsg = groupMsg;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getNewMsgId() {
        return NewMsgId;
    }

    public void setNewMsgId(String newMsgId) {
        NewMsgId = newMsgId;
    }

    public Message.RecommendInfo getRecommendInfo() {
        return RecommendInfo;
    }

    public void setRecommendInfo(Message.RecommendInfo recommendInfo) {
        RecommendInfo = recommendInfo;
    }

    public String getVoiceLength() {
        return VoiceLength;
    }

    public void setVoiceLength(String voiceLength) {
        VoiceLength = voiceLength;
    }

    public Integer getMsgType() {
        return MsgType;
    }

    public void setMsgType(Integer msgType) {
        MsgType = msgType;
    }

    public String getOriContent() {
        return OriContent;
    }

    public void setOriContent(String oriContent) {
        OriContent = oriContent;
    }

    public String getTicket() {
        return Ticket;
    }

    public void setTicket(String ticket) {
        Ticket = ticket;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public Message.AppInfo getAppInfo() {
        return AppInfo;
    }

    public void setAppInfo(Message.AppInfo appInfo) {
        AppInfo = appInfo;
    }

    public String getSubMsgType() {
        return SubMsgType;
    }

    public void setSubMsgType(String subMsgType) {
        SubMsgType = subMsgType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getAppMsgType() {
        return AppMsgType;
    }

    public void setAppMsgType(String appMsgType) {
        AppMsgType = appMsgType;
    }

    public String getImgWidth() {
        return ImgWidth;
    }

    public void setImgWidth(String imgWidth) {
        ImgWidth = imgWidth;
    }

    public String getPlayLength() {
        return PlayLength;
    }

    public void setPlayLength(String playLength) {
        PlayLength = playLength;
    }

    public String getStatusNotifyCode() {
        return StatusNotifyCode;
    }

    public void setStatusNotifyCode(String statusNotifyCode) {
        StatusNotifyCode = statusNotifyCode;
    }

    public String getStatusNotifyUserName() {
        return StatusNotifyUserName;
    }

    public void setStatusNotifyUserName(String statusNotifyUserName) {
        StatusNotifyUserName = statusNotifyUserName;
    }

    public String getHasProductId() {
        return HasProductId;
    }

    public void setHasProductId(String hasProductId) {
        HasProductId = hasProductId;
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getImgHeight() {
        return ImgHeight;
    }

    public void setImgHeight(String imgHeight) {
        ImgHeight = imgHeight;
    }

    public String getImgStatus() {
        return ImgStatus;
    }

    public void setImgStatus(String imgStatus) {
        ImgStatus = imgStatus;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public Long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Long createTime) {
        CreateTime = createTime;
    }

    public class AppInfo{
        private String Type;
        private String AppID;

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }

        public String getAppID() {
            return AppID;
        }

        public void setAppID(String appID) {
            AppID = appID;
        }
    }
    public class RecommendInfo{
        private String VerifyFlag;
        private String NickName;
        private String Scene;
        private String UserName;
        private String QQNum;
        private String Province;
        private String City;
        private String Signature;
        private String OpCode;
        private String Ticket;
        private String Alias;
        private String AttrStatus;
        private String Sex;
        private String Content;

        public String getVerifyFlag() {
            return VerifyFlag;
        }

        public void setVerifyFlag(String verifyFlag) {
            VerifyFlag = verifyFlag;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public String getScene() {
            return Scene;
        }

        public void setScene(String scene) {
            Scene = scene;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getQQNum() {
            return QQNum;
        }

        public void setQQNum(String QQNum) {
            this.QQNum = QQNum;
        }

        public String getProvince() {
            return Province;
        }

        public void setProvince(String province) {
            Province = province;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String city) {
            City = city;
        }

        public String getSignature() {
            return Signature;
        }

        public void setSignature(String signature) {
            Signature = signature;
        }

        public String getOpCode() {
            return OpCode;
        }

        public void setOpCode(String opCode) {
            OpCode = opCode;
        }

        public String getTicket() {
            return Ticket;
        }

        public void setTicket(String ticket) {
            Ticket = ticket;
        }

        public String getAlias() {
            return Alias;
        }

        public void setAlias(String alias) {
            Alias = alias;
        }

        public String getAttrStatus() {
            return AttrStatus;
        }

        public void setAttrStatus(String attrStatus) {
            AttrStatus = attrStatus;
        }

        public String getSex() {
            return Sex;
        }

        public void setSex(String sex) {
            Sex = sex;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }
    }

    public String getForwardFlag() {
        return ForwardFlag;
    }

    public void setForwardFlag(String forwardFlag) {
        ForwardFlag = forwardFlag;
    }
}
