package com.android.gk.Model;

public class Category {

    String CategoryId;
    String CategoryTitle;
    String CategoryImage;
    String CategorySummary;
    String LastUpdatedDate;
    String isDisplay;

    public Category(){

    }

    public Category(String categoryId, String categoryTitle, String categoryImage, String categorySummary, String lastUpdatedDate, String isdisplay) {
        CategoryId = categoryId;
        CategoryTitle = categoryTitle;
        CategoryImage = categoryImage;
        CategorySummary = categorySummary;
        LastUpdatedDate = lastUpdatedDate;
        isDisplay = isdisplay;
    }

    public String getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(String isDisplay) {
        this.isDisplay = isDisplay;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryTitle() {
        return CategoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        CategoryTitle = categoryTitle;
    }

    public String getCategoryImage() {
        return CategoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        CategoryImage = categoryImage;
    }

    public String getCategorySummary() {
        return CategorySummary;
    }

    public void setCategorySummary(String categorySummary) {
        CategorySummary = categorySummary;
    }

    public String getLastUpdatedDate() {
        return LastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        LastUpdatedDate = lastUpdatedDate;
    }
}
