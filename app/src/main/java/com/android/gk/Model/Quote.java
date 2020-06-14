package com.android.gk.Model;

public class Quote {

    String QuoteId;
    String QuoteCatName;
    String QuoteImage;
    String TagName;
    String LastUpdatedDate;
    String isLike;
    String QuoteContent;
    String numberOfLikes;

    public Quote(){

    }

    public Quote(String quoteId, String quoteCatName, String quoteImage, String tagName, String lastUpdatedDate, String isLike, String quoteContent, String numberOfLikes) {
        QuoteId = quoteId;
        QuoteCatName = quoteCatName;
        QuoteImage = quoteImage;
        TagName = tagName;
        LastUpdatedDate = lastUpdatedDate;
        this.isLike = isLike;
        QuoteContent = quoteContent;
        this.numberOfLikes = numberOfLikes;
    }

    public String getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(String numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public String getQuoteId() {
        return QuoteId;
    }

    public void setQuoteId(String quoteId) {
        QuoteId = quoteId;
    }

    public String getQuoteCatName() {
        return QuoteCatName;
    }

    public void setQuoteCatName(String quoteCatName) {
        QuoteCatName = quoteCatName;
    }

    public String getQuoteImage() {
        return QuoteImage;
    }

    public void setQuoteImage(String quoteImage) {
        QuoteImage = quoteImage;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public String getLastUpdatedDate() {
        return LastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        LastUpdatedDate = lastUpdatedDate;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getQuoteContent() {
        return QuoteContent;
    }

    public void setQuoteContent(String quoteContent) {
        QuoteContent = quoteContent;
    }
}
