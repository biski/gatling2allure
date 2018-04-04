package com.biski.objects;

/**
 * Created by wojciech on 14.01.18.
 */
public class Session {
    String scenarioName;
    String userid;
    String attributes;
    Long startDate;

    public Session(String scenario, String userid, String attributes, Long startDate) {
        this.scenarioName = scenario;
        this.userid = userid;
        this.attributes = attributes;
        this.startDate = startDate;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public String getUserId() {
        return userid;
    }

    public String getAttributes() {
        return attributes;
    }

    public Long getStartDate() {
        return startDate;
    }
}
