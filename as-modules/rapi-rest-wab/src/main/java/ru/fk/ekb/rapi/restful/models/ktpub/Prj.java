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
    @Prop(name = "subn_mk")
    public Boolean subnMK;
    @JsonIgnore
    @Prop(name="subn_fk")
    public Boolean subnFK;
    @JsonIgnore
    @Prop(name="prjs_count")
    public String prjs_count;
    @JsonIgnore
    @Prop(name="studias")
    public String comps;

    @Prop(name = "pcode")
    public String id;
    @Prop(name="filmname")
    public String name;
    @Prop(name="startdate")
    public String release_date;
    @Prop(name="govsupp_rev")
    public String refundable_support;
    @Prop(name="govsupp_irr")
    public String nonrefundable_support;
    @Prop(name="govsupp_pct")
    public String percent_support;
    @Prop(name="budget")
    public String budget;
    @Prop(name="summ")
    public String box_office;
    @Prop(name="tckts")
    public String audience;

    public List<FSrc> financing_source;
    public List<PrjComp> companies;
}
