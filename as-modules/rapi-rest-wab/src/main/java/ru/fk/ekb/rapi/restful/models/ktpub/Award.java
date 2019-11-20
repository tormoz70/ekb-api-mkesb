package ru.fk.ekb.rapi.restful.models.ktpub;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Award {
    @JsonIgnore
    public long id;
    public String name;
    public String awardee;
}
