package com.android.gk.Model;

public class Comments {

    String QuoteId;
    String UserName;
    String CommentId;
    String CommentContent;
    String LastUpdatedDate;


    public Comments(){

    }

    public Comments(String commentId, String quoteId, String userName, String commentContent, String lastUpdatedDate) {
        QuoteId = quoteId;
        UserName = userName;
        CommentId = commentId;
        CommentContent = commentContent;
        LastUpdatedDate = lastUpdatedDate;
    }

    public String getQuoteId() {
        return QuoteId;
    }

    public void setQuoteId(String quoteId) {
        QuoteId = quoteId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getCommentId() {
        return CommentId;
    }

    public void setCommentId(String commentId) {
        CommentId = commentId;
    }

    public String getCommentContent() {
        return CommentContent;
    }

    public void setCommentContent(String commentContent) {
        CommentContent = commentContent;
    }

    public String getLastUpdatedDate() {
        return LastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        LastUpdatedDate = lastUpdatedDate;
    }
}
