package ru.fk.ekb.rapi.restful.srvc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.ABeanPage;
import ru.fk.ekb.rapi.restful.models.*;
import ru.fk.ekb.rapi.restful.srvc.RestSrvcBase;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.service.types.BioWrappedRequest;

@Path("/api")
public class APISrvc extends RestSrvcBase {

    @GET
    @Path("/region-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PuQty> region_stat_get(@Context HttpServletRequest request) throws Exception {
        List<PuQty> puQties = _getList("api.region_stat", request, PuQty.class);
        return puQties;
    }

    @POST
    @Path("/region-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public  List<PuQty> region_stat_post(@Context HttpServletRequest request) throws Exception {
        Logger LOG = LoggerFactory.getLogger(RestSrvcBase.class);
        RequestRegionStat requestRegionStat = null;
        BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        if(!Strings.isNullOrEmpty(queryParams.jsonData)) {
            try {
                requestRegionStat = Jsons.decode(queryParams.jsonData, RequestRegionStat.class);
            } catch (Exception e) {
                LOG.error(String.format("Ошибка при получении Json-параметров запроса: %s", queryParams.jsonData), e);
            }
            _setBioParamToRequest("region", requestRegionStat.region, request);
            _setBioParamToRequest("cardNumbers", requestRegionStat.cardNumbers, request);
            _setBioParamToRequest("startDate", requestRegionStat.startDate, request);
            _setBioParamToRequest("endDate", requestRegionStat.endDate, request);
            List<PuQty> puQties = _getList("api.region_stat", request, PuQty.class, true);
            return  puQties;
        }
        return null;
    }

    @GET
    @Path("/film-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FilmStat> film_stat_get(@Context HttpServletRequest request) throws Exception {
        List<FilmStat> filmStats = _getList("api.film_stat", request, FilmStat.class);
        return filmStats;
    }

}
