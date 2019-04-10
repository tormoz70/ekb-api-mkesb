package ru.fk.ekb.rapi.restful.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilmStat {
    public String puNum;
    public String filmName;
    public Integer orgId;
    public String orgName;
    public String region;
    public String city;
    public Integer holdingId;
    public String holdingName;
    public String showDate;
    public String showTime;
    public Integer tickets;
    public Integer sessions;
    @JsonProperty("sum")
    public double summ;
}
