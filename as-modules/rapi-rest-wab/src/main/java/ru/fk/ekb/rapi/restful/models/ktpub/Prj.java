package ru.fk.ekb.rapi.restful.models.ktpub;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import flexjson.JSON;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.Prop;
import ru.fk.ekb.rapi.restful.srvc.DoubleContextualSerializer;
import ru.fk.ekb.rapi.restful.srvc.Precision;

import java.util.List;

public class Prj {
    @JsonIgnore
    public Boolean subnMK;
    @JsonIgnore
    public Boolean subnFK;
    @JsonIgnore
    public String compList;
    @JsonIgnore
    public Long total_movies;

    public Long total_companies;
    public String id;
    public String puNumber;
    public Long index_number;
    public String name;
    public String release_date;
    public String refundable_support;
    public String nonrefundable_support;
    public String percent_support;
    public String budget;
    public String unfulfilledObligationsDebt;
    public String unfulfilledObligationsCost;
    public String unfulfilledObligationsDesc;
    public String box_office;
    public String audience;

    public List<FSrc> financing_source;
    public List<PrjComp> companies;
    public List<Fest> festivals;
}
