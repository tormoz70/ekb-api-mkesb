package ru.fk.ekb.rapi.restful.srvc.api;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.ABeanPage;
import ru.fk.ekb.rapi.restful.models.*;
import ru.fk.ekb.rapi.restful.srvc.RestSrvcBase;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/api")
public class APISrvc extends RestSrvcBase {

    @GET
    @Path("/region-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PuQty> region_stat(@Context HttpServletRequest request) throws Exception {
        List<PuQty> puQties = _getList("api.region_stat", request, PuQty.class);
        return puQties;
    }
}
