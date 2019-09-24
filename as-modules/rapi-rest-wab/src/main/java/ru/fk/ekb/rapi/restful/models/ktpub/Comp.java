package ru.fk.ekb.rapi.restful.models.ktpub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.Prop;

import java.util.List;

public class Comp {
    @JsonIgnore
    public String pcodes;
    @JsonIgnore
    public String filmnames;
    @JsonIgnore
    public String flags;
    @JsonIgnore
    public String pus;
    @JsonIgnore
    public Long total_companies;

    public String id;
    public Long index_number;
    public String name;
    public Long total_movies;
    public String refundable_support;
    public String nonrefundable_support;
    public String box_office;
    public String audience;

    public List<CompPrj> movies;
}
