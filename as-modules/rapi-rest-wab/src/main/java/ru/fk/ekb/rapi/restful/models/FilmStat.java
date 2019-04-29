package ru.fk.ekb.rapi.restful.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.fk.ekb.rapi.restful.srvc.DoubleContextualSerializer;
import ru.fk.ekb.rapi.restful.srvc.Precision;

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
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 2)
    public double summ;
}
