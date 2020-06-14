package com.android.gk.Model;

public class Favourite {

    String isLike;
    String isBookmarked;
    String favId;
    String PostTitle;
    String PostImage;

    public Favourite(){

    }

    public Favourite(String favId, String PostTitle, String PostImage, String isLike, String isBookmarked) {
        this.PostTitle = PostTitle;
        this.PostImage = PostImage;
        this.favId = favId;
        this.isLike = isLike;
        this.isBookmarked = isBookmarked;
    }

    public String getPostTitle() {
        return PostTitle;
    }

    public void setPostTitle(String postTitle) {
        PostTitle = postTitle;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }

    public String getFavId() {
        return favId;
    }

    public void setFavId(String favId) {
        this.favId = favId;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(String isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
}
