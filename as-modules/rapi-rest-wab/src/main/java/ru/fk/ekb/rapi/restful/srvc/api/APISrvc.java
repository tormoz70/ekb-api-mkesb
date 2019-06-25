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
import ru.bio4j.ng.service.types.RestHelper;
import ru.fk.ekb.rapi.restful.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.service.types.WrappedRequest;

@Path("/api")
public class APISrvc {


    @GET
    @Path("/region-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PuQty> region_stat_get(@Context HttpServletRequest request) throws Exception {
        List<PuQty> puQties = RestHelper.getInstance().getList("api.region_stat", request, PuQty.class);
        return puQties;
    }

    @POST
    @Path("/region-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public  List<PuQty> region_stat_post(@Context HttpServletRequest request) throws Exception {
        Logger LOG = LoggerFactory.getLogger(APISrvc.class);
        RequestRegionStat requestRegionStat = null;
        BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        if(!Strings.isNullOrEmpty(queryParams.jsonData)) {
            try {
                requestRegionStat = Jsons.decode(queryParams.jsonData, RequestRegionStat.class);
            } catch (Exception e) {
                LOG.error(String.format("Ошибка при получении Json-параметров запроса: %s", queryParams.jsonData), e);
            }
            RestHelper.getInstance().setBioParamToRequest("region", requestRegionStat.region, request);
            RestHelper.getInstance().setBioParamToRequest("cardNumbers", requestRegionStat.cardNumbers, request);
            RestHelper.getInstance().setBioParamToRequest("startDate", requestRegionStat.startDate, request);
            RestHelper.getInstance().setBioParamToRequest("endDate", requestRegionStat.endDate, request);
            List<PuQty> puQties = RestHelper.getInstance().getList("api.region_stat", request, PuQty.class, true);
            return  puQties;
        }
        return null;
    }

    @GET
    @Path("/film-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FilmStat> film_stat_get(@Context HttpServletRequest request) throws Exception {
        List<FilmStat> filmStats = RestHelper.getInstance().getList("api.film_stat", request, FilmStat.class, true);
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
            final AppService appService = RestHelper.getInstance().getAppService();
            List<KTCompStat> rslt = RestHelper.getInstance().execBatch((SQLContext ctx, KTCompParams prms) -> {
                SQLDefinition sqlDef = appService.getSQLDefinition("api.kt_store_comps");
                for(KTCompParam prm : prms.comps)
                    RestApiAdapter.execLocal(sqlDef, prm, ctx);
                List<KTCompStat> r = RestHelper.getInstance().getList0(ctx, "api.kt_comp_stat", null, prms, true, KTCompStat.class);
                return r;
            }, params, user);
            return rslt;
        }
        return null;
    }

    @POST
    @Path("/kinoteka/film-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<KTFilmStat> kt_film_stat_get(@Context HttpServletRequest request) throws Exception {
        User user = ((WrappedRequest)request).getUser();
        String prmsJson = ((WrappedRequest)request).getBioQueryParams().jsonData;
        KTFilmParams params = null;
        if(!Strings.isNullOrEmpty(prmsJson))
            params = Jsons.decode(prmsJson, KTFilmParams.class);
        if(params != null) {
            final AppService appService = RestHelper.getInstance().getAppService();
            List<KTFilmStat> rslt = RestHelper.getInstance().execBatch((SQLContext ctx, KTFilmParams prms) -> {
                SQLDefinition sqlDef = appService.getSQLDefinition("api.kt_store_pus");
                RestApiAdapter.execLocal(sqlDef, prms, ctx);
                List<KTFilmStat> r = RestHelper.getInstance().getList0(ctx, "api.kt_film_stat", null, prms, true, KTFilmStat.class);
                return r;
            }, params, user);
            return rslt;
        }
        return null;
    }

}
