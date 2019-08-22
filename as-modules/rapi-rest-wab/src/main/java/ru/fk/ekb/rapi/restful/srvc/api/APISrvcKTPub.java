package ru.fk.ekb.rapi.restful.srvc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.types.RestApiAdapter;
import ru.bio4j.ng.service.types.RestHelper;
import ru.bio4j.ng.service.types.WrappedRequest;
import ru.fk.ekb.rapi.restful.models.*;
import ru.fk.ekb.rapi.restful.models.ktpub.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/ktpub-api")
public class APISrvcKTPub {


    private static void _decodeSort(HttpServletRequest request, Sort.NullsPosition nullsPosition) throws Exception {
        String sortTypeParam = RestHelper.getInstance().getBioParamFromRequest("sortType", request, String.class);
        String sortParam = RestHelper.getInstance().getBioParamFromRequest("sort", request, String.class);
        if(!Strings.isNullOrEmpty(sortTypeParam) && !Strings.isNullOrEmpty(sortParam)) {
            Sort sort = new Sort();
            if (nullsPosition != null)
                sort.setNullsPosition(nullsPosition);
            sort.setFieldName(sortTypeParam);
            sort.setDirection(!Strings.isNullOrEmpty(sortParam) && sortParam.trim().toLowerCase().startsWith("desc") ? Sort.Direction.DESC : Sort.Direction.ASC);
            List<Sort> sorts = new ArrayList<>();
            sorts.add(sort);
            ((WrappedRequest) request).getBioQueryParams().sort = sorts;
        }
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

    private static void _decodeFinancingSource(Prj prj) throws Exception {
        prj.financing_source = new ArrayList<>();
        if (prj.subnMK) {
            FSrc fsrc = new FSrc();
            fsrc.id = 0;
            fsrc.name = "Министерство культуры";
            fsrc.short_name = "МК";
            fsrc.image = String.format("http://resources.fond-kino.ru/eais/images/big/financing_source_%02d.png", fsrc.id);
            prj.financing_source.add(fsrc);
        }
        if (prj.subnFK) {
            FSrc fsrc = new FSrc();
            fsrc.id = 1;
            fsrc.name = "Фонд кино";
            fsrc.short_name = "ФК";
            fsrc.image = String.format("http://resources.fond-kino.ru/eais/images/big/financing_source_%02d.png", fsrc.id);
            prj.financing_source.add(fsrc);
        }
    }

    private static void _decodeSupportId(final HttpServletRequest request) throws Exception {
        Integer supportId = RestHelper.getInstance().getBioParamFromRequest("support_id", request, Integer.class);
        if (supportId == null)
            RestHelper.getInstance().setBioParamToRequest("p_subn", null, request);
        else {
            if (supportId == 0)
                RestHelper.getInstance().setBioParamToRequest("p_subn", "10", request);
            else if (supportId == 1)
                RestHelper.getInstance().setBioParamToRequest("p_subn", "01", request);
            else
                RestHelper.getInstance().setBioParamToRequest("p_subn", "99", request);
        }
    }

    public RspPrj _getProjects(final String bioCode, final HttpServletRequest request) throws Exception {
        WrappedRequest req = ((WrappedRequest) request);
        boolean forceAll = Strings.isNullOrEmpty(req.getBioQueryParams().pageSizeOrig);
        _decodeSort(request, Sort.NullsPosition.DEFAULT);

        _decodeSupportId(request);

        List<Prj> aBeanPage;
        if (forceAll)
            aBeanPage = RestHelper.getInstance().getListAll(bioCode, request, Prj.class);
        else
            aBeanPage = RestHelper.getInstance().getList(bioCode, request, Prj.class);

        RspPrj dataResult = new RspPrj();
        dataResult.movies = aBeanPage;

        for (Prj prj : dataResult.movies) {
            try {
                prj.companies = new ArrayList<>();
                String[] compList = Strings.split(prj.compList, "|-|");
                for (String compItem : compList) {
                    PrjComp pc = new PrjComp();
                    pc.id = compItem.substring(1, compItem.indexOf("]"));
                    pc.name = compItem.substring(compItem.indexOf("]") + 2);
                    prj.companies.add(pc);
                }
                _decodeFinancingSource(prj);
            } catch (Exception e) {
                throw new Exception(String.format("Error on processing prg: %s(%s)", prj.id, prj.name), e);
            }

        }

        Prj totals = RestHelper.getInstance().getFirst(bioCode+"-ttl", request, Prj.class);
        dataResult.total_movies = totals.total_movies;
        dataResult.total_companies = totals.total_companies;
        dataResult.total_refundable_support = totals.refundable_support;
        dataResult.total_nonrefundable_support = totals.nonrefundable_support;
        dataResult.total_budget = totals.budget;
        dataResult.total_box_office = totals.box_office;
        dataResult.total_audience = totals.audience;

        return dataResult;
    }

    @GET
    @Path("/rating/movies")
    @Produces(MediaType.APPLICATION_JSON)
    public RspPrj getMovies(@Context HttpServletRequest request) throws Exception {
        return _getProjects("api.ktpub.prjs-released", request);
    }

    @GET
    @Path("/rating/companies")
    @Produces(MediaType.APPLICATION_JSON)
    public RspComp getCompanies(@Context HttpServletRequest request) throws Exception {
        WrappedRequest req = ((WrappedRequest)request);
        boolean forceAll = Strings.isNullOrEmpty(req.getBioQueryParams().pageSizeOrig);
        _decodeSort(request, Sort.NullsPosition.DEFAULT);
        final String bioCode = "api.ktpub.comps";

        Integer releaseStatus = RestHelper.getInstance().getBioParamFromRequest("release_status", request, Integer.class);
        if (releaseStatus != null && releaseStatus == 1)
            RestHelper.getInstance().setBioParamToRequest("p_released", "1", request);
        else if (releaseStatus != null && releaseStatus == 2)
            RestHelper.getInstance().setBioParamToRequest("p_released", "0", request);
        else
            RestHelper.getInstance().setBioParamToRequest("p_released", null, request);

        _decodeSupportId(request);


        List<Comp> aBeanPage;
        if(forceAll)
            aBeanPage = RestHelper.getInstance().getListAll(bioCode, request, Comp.class);
        else
            aBeanPage = RestHelper.getInstance().getList(bioCode, request, Comp.class);

        RspComp dataResult = new RspComp();
        dataResult.companies = aBeanPage;

        for(Comp comp : dataResult.companies) {
            comp.movies = new ArrayList<>();
            //[1107746636656]:АО "ВБД Груп"|-|[1047796532563]:ООО «Кинокомпания «Соливс»
            String[] pcodesList = comp.pcodes.length() > 0 ? Strings.split(comp.pcodes.substring(0, comp.pcodes.length()-2), "|-|") : new String[0];
            String[] filmnamesList = comp.filmnames.length() > 0 ? Strings.split(comp.filmnames.substring(0, comp.filmnames.length()-2), "|-|") : new String[0];
            String[] flagsList = comp.flags.length() > 0 ? Strings.split(comp.flags.substring(0, comp.flags.length()-2), "|-|") : new String[0];
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
                    fsrc.image = String.format("http://resources.fond-kino.ru/eais/images/big/financing_source_%02d.png", fsrc.id);
                    pc.financing_source.add(fsrc);
                }
                if(subnFK){
                    FSrc fsrc = new FSrc();
                    fsrc.id = 1;
                    fsrc.name = "Фонд кино";
                    fsrc.short_name = "ФК";
                    fsrc.image = String.format("http://resources.fond-kino.ru/eais/images/big/financing_source_%02d.png", fsrc.id);
                    pc.financing_source.add(fsrc);
                }
                comp.movies.add(pc);
            }

        }

        Comp totals = RestHelper.getInstance().getFirst(bioCode+"-ttl", request, Comp.class);
        dataResult.total_movies = totals.total_movies;
        dataResult.total_companies = totals.total_companies;
        dataResult.total_refundable_support = totals.refundable_support;
        dataResult.total_nonrefundable_support = totals.nonrefundable_support;
        dataResult.total_box_office = totals.box_office;
        dataResult.total_audience = totals.audience;

        return dataResult;
    }

    @GET
    @Path("/projects/inProduction")
    @Produces(MediaType.APPLICATION_JSON)
    public RspPrj getProjectsInProduction(@Context HttpServletRequest request) throws Exception {
        return _getProjects("api.ktpub.prjs-inProduction", request);
    }

    @GET
    @Path("/projects/unfulfilledObligations")
    @Produces(MediaType.APPLICATION_JSON)
    public RspPrj getProjectsUnfulfilledObligations(@Context HttpServletRequest request) throws Exception {
        return _getProjects("api.ktpub.prjs-unoblig", request);
    }

    @GET
    @Path("/search/hints")
    @Produces(MediaType.APPLICATION_JSON)
    public RspHint getHints(@Context HttpServletRequest request) throws Exception {
        WrappedRequest req = ((WrappedRequest)request);
        final String bioCode = "api.ktpub.hints";
        RspHint dataResult = new RspHint();
        dataResult.response = RestHelper.getInstance().getListAll(bioCode, request, Hint.class);
        return dataResult;
    }

}
