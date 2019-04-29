package ru.fk.ekb.rapi.restful.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.fk.ekb.rapi.restful.srvc.DoubleContextualSerializer;
import ru.fk.ekb.rapi.restful.srvc.Precision;

public class KTCompStat {
    public Long ktcompId;
    public Long fkTcktsReturn00;
    public Long fkTcktsReturn01;
    public Long fkTcktsReturn11;
    public Long fkTcktsTotal;
    public Long fkSessReturn00;
    public Long fkSessReturn01;
    public Long fkSessReturn11;
    public Long fkSessTotal;
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 2)
    public Double fkSummReturn00;
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 2)
    public Double fkSummReturn01;
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 2)
    public Double fkSummReturn11;
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 2)
    public Double fkSummTotal;
    public Long fkFilmsReturn00;
    public Long fkFilmsReturn01;
    public Long fkFilmsReturn11;
    public Long fkFilmsTota;
    public Long tcktsTotal;
    public Long sessTotal;
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 2)
    public Double summTotal;
    public Long filmsTotal;
}
