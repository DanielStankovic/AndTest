package com.androidb2c.microbs.androidb2c.Model;

public class SearchSuggestion {

    private String suggestion;
    private long timeMiliSec;

    public SearchSuggestion(String suggestion, long timeMiliSec) {
        this.suggestion = suggestion;
        this.timeMiliSec = timeMiliSec;
    }

    public SearchSuggestion() {
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public long getTimeMiliSec() {
        return timeMiliSec;
    }

    public void setTimeMiliSec(long timeMiliSec) {
        this.timeMiliSec = timeMiliSec;
    }
}
