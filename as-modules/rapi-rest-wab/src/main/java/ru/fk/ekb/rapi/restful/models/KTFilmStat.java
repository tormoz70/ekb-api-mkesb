package ru.fk.ekb.rapi.restful.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.fk.ekb.rapi.restful.srvc.DoubleContextualSerializer;
import ru.fk.ekb.rapi.restful.srvc.Precision;

public class KTFilmStat {
    public String puNum;
    public String filmName;
    public Long tcktsTotal;
    public Long sessTotal;
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 2)
    public Double summTotal;
}
