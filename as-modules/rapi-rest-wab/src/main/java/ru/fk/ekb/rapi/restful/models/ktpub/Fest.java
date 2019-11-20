package ru.fk.ekb.rapi.restful.models.ktpub;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

//import flexjson.JSON;

public class Fest {
    @JsonIgnore
    public String uid;
    public String name;
    @JsonIgnore
    public String iconName;
    public String icon;
    public List<Award> awards;
    public List<Award> nominations;
}
