package com.android.gk.Model;

public class Post {

    String PostId;
    String PostTitle;
    String PostContent;
    String PostCategory;
    String PostTags;
    String PostImage;
    String LastUpdatedDate;
    String PostStatus;
    String PostPublishStatus;
    String isLike;
    String numberOfLikes;
    String isTopReads;

    public Post(){

    }

    public Post(String postId, String postTitle, String postContent, String postCategory,
                String postTags, String postImage, String lastUpdatedDate, String postStatus,
                String postPublishStatus, String isLike, String numberOfLikes, String isTopReads) {
        PostId = postId;
        PostTitle = postTitle;
        PostContent = postContent;
        PostCategory = postCategory;
        PostTags = postTags;
        PostImage = postImage;
        LastUpdatedDate = lastUpdatedDate;
        PostStatus = postStatus;
        PostPublishStatus = postPublishStatus;
        this.isLike = isLike;
        this.numberOfLikes = numberOfLikes;
        this.isTopReads = isTopReads;
    }

    public String getIsTopReads() {
        return isTopReads;
    }

    public void setIsTopReads(String isTopReads) {
        this.isTopReads = isTopReads;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(String numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getPostTitle() {
        return PostTitle;
    }

    public void setPostTitle(String postTitle) {
        PostTitle = postTitle;
    }

    public String getPostContent() {
        return PostContent;
    }

    public void setPostContent(String postContent) {
        PostContent = postContent;
    }

    public String getPostCategory() {
        return PostCategory;
    }

    public void setPostCategory(String postCategory) {
        PostCategory = postCategory;
    }

    public String getPostTags() {
        return PostTags;
    }

    public void setPostTags(String postTags) {
        PostTags = postTags;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }

    public String getLastUpdatedDate() {
        return LastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        LastUpdatedDate = lastUpdatedDate;
    }

    public String getPostStatus() {
        return PostStatus;
    }

    public void setPostStatus(String postStatus) {
        PostStatus = postStatus;
    }

    public String getPostPublishStatus() {
        return PostPublishStatus;
    }

    public void setPostPublishStatus(String postPublishStatus) {
        PostPublishStatus = postPublishStatus;
    }
}
