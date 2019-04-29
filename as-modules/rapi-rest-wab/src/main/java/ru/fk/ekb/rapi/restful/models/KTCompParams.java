package ru.fk.ekb.rapi.restful.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.fk.ekb.rapi.restful.srvc.DoubleContextualSerializer;
import ru.fk.ekb.rapi.restful.srvc.Precision;

public class KTCompParams {
    public String periodFrom;
    public String periodTo;
    public KTCompParam[] comps;
}
