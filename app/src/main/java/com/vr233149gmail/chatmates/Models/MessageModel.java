package com.vr233149gmail.chatmates.Models;

public class MessageModel {

    String uId;
    String message,messageId, imageUrl, pdfUrl,username;
    String timeStamp;

    public MessageModel(String uId, String message, String timeStamp,String username,String messageId) {
        this.uId = uId;
        this.message = message;
        this.timeStamp = timeStamp;
        this.username = username;
        this.messageId = messageId;

    }

    public MessageModel() {
    }

    public MessageModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
