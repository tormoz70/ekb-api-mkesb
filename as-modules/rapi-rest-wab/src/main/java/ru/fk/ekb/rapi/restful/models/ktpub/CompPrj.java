package ru.fk.ekb.rapi.restful.models.ktpub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.Prop;

import java.util.List;

public class CompPrj {
    public String id;
    public String puNumber;
    public String name;
    public List<FSrc> financing_source;
}
