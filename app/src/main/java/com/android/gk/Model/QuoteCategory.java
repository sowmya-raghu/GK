package com.android.gk.Model;

public class QuoteCategory {

    String QuoteCatId;
    String QuoteCatName;

    public QuoteCategory(){

    }

    public QuoteCategory(String quoteCatId, String quoteCatName) {
        QuoteCatId = quoteCatId;
        QuoteCatName = quoteCatName;
    }

    public String getQuoteCatId() {
        return QuoteCatId;
    }

    public void setQuoteCatId(String quoteCatId) {
        QuoteCatId = quoteCatId;
    }

    public String getQuoteCatName() {
        return QuoteCatName;
    }

    public void setQuoteCatName(String quoteCatName) {
        QuoteCatName = quoteCatName;
    }
}
