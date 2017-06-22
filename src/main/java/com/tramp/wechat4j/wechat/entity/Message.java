package com.tramp.wechat4j.wechat.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 消息.
 */
public class Message {
    private String MsgId;
    private Integer ForwardFlag;
    private String NewMsgId;
    private RecommendInfo RecommendInfo;
    private Integer VoiceLength;
    private Integer MsgType;
    private String OriContent;
    private String Ticket;
    private String Url;
    private AppInfo AppInfo;
    private Integer SubMsgType;
    private String Content;
    private Integer AppMsgType;
    private Integer ImgWidth;
    private Integer PlayLength;
    private Integer StatusNotifyCode;
    private String StatusNotifyUserName;
    private Integer HasProductId;
    private String MediaId;
    private String ToUserName;
    private String FileSize;
    private Integer Status;
    private String FromUserName;
    private Integer ImgHeight;
    private Integer ImgStatus;
    private String FileName;
    private Long CreateTime;

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

    public Integer getVoiceLength() {
        return VoiceLength;
    }

    public void setVoiceLength(Integer voiceLength) {
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

    public Integer getSubMsgType() {
        return SubMsgType;
    }

    public void setSubMsgType(Integer subMsgType) {
        SubMsgType = subMsgType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Integer getAppMsgType() {
        return AppMsgType;
    }

    public void setAppMsgType(Integer appMsgType) {
        AppMsgType = appMsgType;
    }

    public Integer getImgWidth() {
        return ImgWidth;
    }

    public void setImgWidth(Integer imgWidth) {
        ImgWidth = imgWidth;
    }

    public Integer getPlayLength() {
        return PlayLength;
    }

    public void setPlayLength(Integer playLength) {
        PlayLength = playLength;
    }

    public Integer getStatusNotifyCode() {
        return StatusNotifyCode;
    }

    public void setStatusNotifyCode(Integer statusNotifyCode) {
        StatusNotifyCode = statusNotifyCode;
    }

    public String getStatusNotifyUserName() {
        return StatusNotifyUserName;
    }

    public void setStatusNotifyUserName(String statusNotifyUserName) {
        StatusNotifyUserName = statusNotifyUserName;
    }

    public Integer getHasProductId() {
        return HasProductId;
    }

    public void setHasProductId(Integer hasProductId) {
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

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public Integer getImgHeight() {
        return ImgHeight;
    }

    public void setImgHeight(Integer imgHeight) {
        ImgHeight = imgHeight;
    }

    public Integer getImgStatus() {
        return ImgStatus;
    }

    public void setImgStatus(Integer imgStatus) {
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
        private Integer Type;
        private String AppID;

        public Integer getType() {
            return Type;
        }

        public void setType(Integer type) {
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
        private Integer VerifyFlag;
        private String NickName;
        private Integer Scene;
        private String UserName;
        private Integer QQNum;
        private String Province;
        private String City;
        private String Signature;
        private Integer OpCode;
        private String Ticket;
        private String Alias;
        private Integer AttrStatus;
        private Integer Sex;
        private String Content;

        public Integer getVerifyFlag() {
            return VerifyFlag;
        }

        public void setVerifyFlag(Integer verifyFlag) {
            VerifyFlag = verifyFlag;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public Integer getScene() {
            return Scene;
        }

        public void setScene(Integer scene) {
            Scene = scene;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public Integer getQQNum() {
            return QQNum;
        }

        public void setQQNum(Integer QQNum) {
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

        public Integer getOpCode() {
            return OpCode;
        }

        public void setOpCode(Integer opCode) {
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

        public Integer getAttrStatus() {
            return AttrStatus;
        }

        public void setAttrStatus(Integer attrStatus) {
            AttrStatus = attrStatus;
        }

        public Integer getSex() {
            return Sex;
        }

        public void setSex(Integer sex) {
            Sex = sex;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }
    }

    public Integer getForwardFlag() {
        return ForwardFlag;
    }

    public void setForwardFlag(Integer forwardFlag) {
        ForwardFlag = forwardFlag;
    }
}
