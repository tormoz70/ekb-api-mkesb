package ru.fk.ekb.rapi.restful.srvc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.DateTimeParser;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.ABeanPage;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.types.RestApiAdapter;
import ru.bio4j.ng.service.types.RestHelper;
import ru.fk.ekb.rapi.restful.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.service.types.WrappedRequest;
import ru.fk.ekb.rapi.restful.models.ktpub.*;

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
            List<PuQty> puQties = RestHelper.getInstance().getListAll("api.region_stat", request, PuQty.class);
            return  puQties;
        }
        return null;
    }

    @GET
    @Path("/film-stat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FilmStat> film_stat_get(@Context HttpServletRequest request) throws Exception {
        List<FilmStat> filmStats = RestHelper.getInstance().getListAll("api.film_stat", request, FilmStat.class);
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
                List<KTCompStat> r = RestHelper.getInstance().getList0All(ctx, "api.kt_comp_stat", null, prms, KTCompStat.class);
                return r;
            }, params);
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
                List<KTFilmStat> r = RestHelper.getInstance().getList0All(ctx, "api.kt_film_stat", null, prms, KTFilmStat.class);
                return r;
            }, params);
            return rslt;
        }
        return null;
    }

    private static void _decodeSort(HttpServletRequest request, Sort.NullsPosition nullsPosition) throws Exception {
        String sortTypeParam = RestHelper.getInstance().getBioParamFromRequest("sortType", request, String.class);
        String sortParam = RestHelper.getInstance().getBioParamFromRequest("sort", request, String.class);
        Sort sort = new Sort();
        if(nullsPosition != null)
            sort.setNullsPosition(nullsPosition);
        sort.setFieldName(sortTypeParam);
        sort.setDirection(!Strings.isNullOrEmpty(sortParam) && sortParam.trim().toLowerCase().startsWith("desc") ? Sort.Direction.DESC : Sort.Direction.ASC);
        List<Sort> sorts = new ArrayList<>();
        sorts.add(sort);
        ((WrappedRequest)request).getBioQueryParams().sort = sorts;
    }

    private static void _replaceBioParam(HttpServletRequest request, String newParamName, String oldParamName, Object newParamValue) throws Exception {
        List<Param> prms = ((WrappedRequest)request).getBioQueryParams().bioParams;
        if(prms == null){
            prms = new ArrayList<>();
            ((WrappedRequest)request).getBioQueryParams().bioParams = prms;
        }
        Paramus.setParamValue(prms, newParamName, newParamValue);
        prms.remove(oldParamName);
    }

    private static void _replaceBioParam(HttpServletRequest request, String newParamName, String oldParamName) throws Exception {
        List<Param> prms = ((WrappedRequest)request).getBioQueryParams().bioParams;
        if(prms == null){
            prms = new ArrayList<>();
            ((WrappedRequest)request).getBioQueryParams().bioParams = prms;
        }
        Paramus.setParamValue(prms, newParamName, RestHelper.getInstance().getBioParamFromRequest(oldParamName, request, String.class));
        prms.remove(oldParamName);
    }

    @GET
    @Path("/ktpub/rating/movies")
    @Produces(MediaType.APPLICATION_JSON)
    public RspPrj getMovies(@Context HttpServletRequest request) throws Exception {
        WrappedRequest req = ((WrappedRequest)request);
        boolean forceAll = Strings.isNullOrEmpty(req.getBioQueryParams().pageSizeOrig);
        _decodeSort(request, Sort.NullsPosition.DEFAULT);
        final String bioCode = "api.ktpub.prjs-released";

        List<Prj> aBeanPage;
        if(forceAll)
            aBeanPage = RestHelper.getInstance().getListAll(bioCode, request, Prj.class);
        else
            aBeanPage = RestHelper.getInstance().getList(bioCode, request, Prj.class);

        RspPrj dataResult = new RspPrj();
        dataResult.movies = aBeanPage;
        Prj totals = dataResult.movies.get(0);
        dataResult.movies.remove(totals);
        dataResult.total_movies = totals.prjs_count;
        dataResult.total_refundable_support = totals.refundable_support;
        dataResult.total_nonrefundable_support = totals.nonrefundable_support;
        dataResult.total_budget = totals.budget;
        dataResult.total_box_office = totals.box_office;
        dataResult.total_audience = totals.audience;

        for(Prj prj : dataResult.movies) {
            prj.companies = new ArrayList<>();
            //[1107746636656]:АО "ВБД Груп"|-|[1047796532563]:ООО «Кинокомпания «Соливс»
            String[] compList = Strings.split(prj.comps, "|-|");
            for(String compItem : compList) {
                PrjComp pc = new PrjComp();
                pc.id = compItem.substring(1, compItem.indexOf("]"));
                pc.name = compItem.substring(compItem.indexOf("]")+2);
                prj.companies.add(pc);
            }
            prj.financing_source = new ArrayList<>();
            if(prj.subnMK){
                FSrc fsrc = new FSrc();
                fsrc.id = 0;
                fsrc.name = "Министерство культуры";
                fsrc.short_name = "МК";
                prj.financing_source.add(fsrc);
            }
            if(prj.subnFK){
                FSrc fsrc = new FSrc();
                fsrc.id = 1;
                fsrc.name = "Фонд кино";
                fsrc.short_name = "ФК";
                prj.financing_source.add(fsrc);
            }

        }

        return dataResult;
    }

    @GET
    @Path("/ktpub/rating/companies")
    @Produces(MediaType.APPLICATION_JSON)
    public RspComp getCompanies(@Context HttpServletRequest request) throws Exception {
        WrappedRequest req = ((WrappedRequest)request);
        boolean forceAll = Strings.isNullOrEmpty(req.getBioQueryParams().pageSizeOrig);
        _decodeSort(request, Sort.NullsPosition.DEFAULT);
        final String bioCode = "api.ktpub.comps";

        List<Comp> aBeanPage;
        if(forceAll)
            aBeanPage = RestHelper.getInstance().getListAll(bioCode, request, Comp.class);
        else
            aBeanPage = RestHelper.getInstance().getList(bioCode, request, Comp.class);

        RspComp dataResult = new RspComp();
        dataResult.companies = aBeanPage;
        Comp totals = dataResult.companies.get(0);
        dataResult.companies.remove(totals);
        dataResult.total_companies = totals.comps_count;
        dataResult.total_refundable_support = totals.refundable_support;
        dataResult.total_nonrefundable_support = totals.nonrefundable_support;
        dataResult.total_box_office = totals.box_office;
        dataResult.total_audience = totals.audience;

        for(Comp comp : dataResult.companies) {
            comp.movies = new ArrayList<>();
            //[1107746636656]:АО "ВБД Груп"|-|[1047796532563]:ООО «Кинокомпания «Соливс»
            String[] pcodesList = Strings.split(comp.pcodes, "|-|");
            String[] filmnamesList = Strings.split(comp.filmnames, "|-|");
            String[] flagsList = Strings.split(comp.flags, "|-|");
            for(int i=0; i<pcodesList.length; i++) {
                CompPrj pc = new CompPrj();
                pc.id = pcodesList[i];
                pc.name = filmnamesList[i];
                pc.financing_source = new ArrayList<>();
                boolean subnMK = flagsList[i].charAt(0) == '1';
                boolean subnFK = flagsList[i].charAt(1) == '1';
                if(subnMK){
                    FSrc fsrc = new FSrc();
                    fsrc.id = 0;
                    fsrc.name = "Министерство культуры";
                    fsrc.short_name = "МК";
                    pc.financing_source.add(fsrc);
                }
                if(subnFK){
                    FSrc fsrc = new FSrc();
                    fsrc.id = 1;
                    fsrc.name = "Фонд кино";
                    fsrc.short_name = "ФК";
                    pc.financing_source.add(fsrc);
                }
                comp.movies.add(pc);
            }

        }

        return dataResult;
    }

}
