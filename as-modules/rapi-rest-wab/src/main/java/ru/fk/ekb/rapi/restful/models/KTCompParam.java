package ru.fk.ekb.rapi.restful.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.fk.ekb.rapi.restful.srvc.DoubleContextualSerializer;
import ru.fk.ekb.rapi.restful.srvc.Precision;

public class KTCompParam {
    public Long compId;
    public String compName;
    public String pus;
}
