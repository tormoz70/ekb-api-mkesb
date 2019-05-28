package ru.fk.ekb.rapi.restful.srvc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.SQLDefinition;
import ru.bio4j.ng.service.types.RestApiAdapter;
import ru.fk.ekb.module.impl.EkbAppModule;
import ru.fk.ekb.rapi.restful.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.service.types.WrappedRequest;
import ru.fk.ekb.rapi.restful.srvc.RestSrvcBase;

@Path("/api")
public class APISrvc extends RestSrvcBase {

    @Override
    protected Class<? extends AppService> getAppServiceClass() {
        return EkbAppModule.class;
    }

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
        BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
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
        List<FilmStat> filmStats = _getList("api.film_stat", request, FilmStat.class, true);
        return filmStats;
    }

    @POST
    @Path("/kinoteka/comp-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<KTCompStat> kt_comp_stat_get(@Context HttpServletRequest request) throws Exception {
        User user = ((WrappedRequest)request).getUser();
        String prmsJson = ((WrappedRequest)request).getBioQueryParams().jsonData;
        KTCompParams params = null;
        if(!Strings.isNullOrEmpty(prmsJson))
            params = Jsons.decode(prmsJson, KTCompParams.class);
        if(params != null) {
            final AppService appService = getAppService();
            List<KTCompStat> rslt = _execBatch((SQLContext ctx, KTCompParams prms) -> {
                SQLDefinition sqlDef = appService.getSQLDefinition("api.kt_store_comps");
                for(KTCompParam prm : prms.comps)
                    RestApiAdapter.execLocal(sqlDef, prm, ctx);
                List<KTCompStat> r = _getList0(ctx, "api.kt_comp_stat", null, prms, true, KTCompStat.class);
                return r;
            }, params, user);
            return rslt;
        }
        return null;
    }

}
