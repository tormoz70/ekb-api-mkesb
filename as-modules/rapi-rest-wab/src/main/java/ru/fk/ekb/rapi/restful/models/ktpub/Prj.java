package ru.fk.ekb.rapi.restful.models.ktpub;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import flexjson.JSON;
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
    public String prjs_count;
    @JsonIgnore
    public String comps;

    public String id;
    public Long index_number;
    public String name;
    public String release_date;
    public String refundable_support;
    public String nonrefundable_support;
    public String percent_support;
    public String budget;
    public String box_office;
    public String audience;

    public List<FSrc> financing_source;
    public List<PrjComp> companies;
}
