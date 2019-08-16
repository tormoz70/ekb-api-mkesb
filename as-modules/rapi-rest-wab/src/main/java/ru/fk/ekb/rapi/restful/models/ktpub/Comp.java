package ru.fk.ekb.rapi.restful.models.ktpub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.Prop;

import java.util.List;

public class Comp {
    @JsonIgnore
    @Prop(name="pcodes")
    public String pcodes;
    @JsonIgnore
    @Prop(name="filmnames")
    public String filmnames;
    @JsonIgnore
    @Prop(name="flags")
    public String flags;
    @JsonIgnore
    @Prop(name="comps_count")
    public String comps_count;

    @Prop(name="ogrn")
    public String id;
    @Prop(name="studia")
    public String name;
    @Prop(name="films")
    public String total_movies;
    @Prop(name="govsupp_rev")
    public String refundable_support;
    @Prop(name="govsupp_irr")
    public String nonrefundable_support;
    @Prop(name="summ")
    public String box_office;
    @Prop(name="tckts")
    public String audience;

    public List<CompPrj> movies;
}
