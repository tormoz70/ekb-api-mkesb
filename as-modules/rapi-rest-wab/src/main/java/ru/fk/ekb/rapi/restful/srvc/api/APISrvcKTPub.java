package ru.fk.ekb.rapi.restful.srvc.api;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.FilterAndSorter;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.service.types.RestHelper;
import ru.bio4j.ng.service.types.WrappedRequest;
import ru.fk.ekb.rapi.restful.models.ktpub.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

//import ru.bio4j.ng.commons.utils.Jsons;

@Path("/ktpub-api")
public class APISrvcKTPub {

    private static final String CS_EAIS_RES_FTP = "http://resources.fond-kino.ru/eais/images";

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
        Date ctime = new Date();
        prj.financing_source = new ArrayList<>();
        if (prj.subnMK) {
            FSrc fsrc = new FSrc();
            fsrc.id = 0;
            fsrc.name = "Министерство культуры";
            fsrc.short_name = "МК";
            fsrc.image = String.format("%s/big/financing_source_%02d.png?_dc=%d", CS_EAIS_RES_FTP, fsrc.id, ctime.getTime());
            prj.financing_source.add(fsrc);
        }
        if (prj.subnFK) {
            FSrc fsrc = new FSrc();
            fsrc.id = 1;
            fsrc.name = "Фонд кино";
            fsrc.short_name = "ФК";
            fsrc.image = String.format("%s/big/financing_source_%02d.png?_dc=%d", CS_EAIS_RES_FTP, fsrc.id, ctime.getTime());
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

    private static void _decodeReleaseStatus(final HttpServletRequest request) throws Exception {
        Integer releaseStatus = RestHelper.getInstance().getBioParamFromRequest("release_status", request, Integer.class);
        if (releaseStatus != null && releaseStatus == 1)
            RestHelper.getInstance().setBioParamToRequest("p_released", "1", request);
        else if (releaseStatus != null && releaseStatus == 2)
            RestHelper.getInstance().setBioParamToRequest("p_released", "0", request);
        else
            RestHelper.getInstance().setBioParamToRequest("p_released", null, request);
    }

    private static void _prepareAwards(Map<String, List<Fest>> festsMap, Prj prj) {
        if (festsMap.containsKey(prj.id)) {
            prj.festivals = festsMap.get(prj.id);
            for (Fest f : prj.festivals) {
                String iconStatus = (f.awards.size() > 0) ? "active" : "inactive";
                Date ctime = new Date();
                f.icon = String.format("%s/guspp/%s/%s?_dc=%d", CS_EAIS_RES_FTP, iconStatus, f.iconName, ctime.getTime());
            }
        }
    }

    public RspPrj _getProjects(final String bioCodePrjs, final String bioCodeAwards, final HttpServletRequest request) throws Exception {
        WrappedRequest req = ((WrappedRequest) request);
        boolean forceAll = Strings.isNullOrEmpty(req.getBioQueryParams().pageSizeOrig);
        _decodeSort(request, Sort.NullsPosition.DEFAULT);
        _decodeSupportId(request);

        Map<String, List<Fest>> festsMap = _loadFests(bioCodeAwards, request);

        List<Prj> aBeanPage;
        if (forceAll)
            aBeanPage = RestHelper.getInstance().getListAll(bioCodePrjs, request, Prj.class);
        else
            aBeanPage = RestHelper.getInstance().getList(bioCodePrjs, request, Prj.class);

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
                _prepareAwards(festsMap, prj);
            } catch (Exception e) {
                throw new Exception(String.format("Error on processing prg: %s(%s)", prj.id, prj.name), e);
            }

        }

        Prj totals = RestHelper.getInstance().getFirst(bioCodePrjs+"-ttl", request, Prj.class);
        dataResult.total_movies = totals.total_movies;
        dataResult.total_companies = totals.total_companies;
        dataResult.total_refundable_support = totals.refundable_support;
        dataResult.total_nonrefundable_support = totals.nonrefundable_support;
        dataResult.total_budget = totals.budget;
        dataResult.total_unfulfilledObligationsDebt = totals.unfulfilledObligationsDebt;
        dataResult.total_unfulfilledObligationsCost = totals.unfulfilledObligationsCost;
        dataResult.total_box_office = totals.box_office;
        dataResult.total_audience = totals.audience;
        dataResult.unfulfilledObligationsRelevance = RestHelper.getInstance().getScalar(
                "api.ktpub.get-gparam",
                Paramus.createParams("paramname", "unfulfilled-obligations-relevance"),
                String.class, null);

        return dataResult;
    }

    private static Fest findFest(List<Fest> prjFests, String festUid) {
        Fest rslt =  prjFests.stream().filter(f -> f.uid.equalsIgnoreCase(festUid)).findFirst().orElse(null);
        if(rslt == null) {
            rslt = new Fest();
            prjFests.add(rslt);
            rslt.awards = new ArrayList<>();
            rslt.nominations = new ArrayList<>();
            rslt.uid = festUid;
        }
        return rslt;
    }

    private static Award findAward(List<Award> awards, long awardId) {
        Award rslt =  awards.stream().filter(f -> f.id == awardId).findFirst().orElse(null);
        if(rslt == null) {
            rslt = new Award();
            rslt.id = awardId;
            awards.add(rslt);
        }
        return rslt;
    }

    public Map<String, List<Fest>> _loadFests(final String bioCode, final HttpServletRequest request) throws Exception {
        Map<String, List<Fest>> rslt = new HashMap<>();
        List<AwardRec> aBeans = RestHelper.getInstance().getListAll(bioCode, request, AwardRec.class);
        for (AwardRec awardRec : aBeans) {
            List<Fest> prjFests;
            if(rslt.containsKey(awardRec.pcode))
                prjFests = rslt.get(awardRec.pcode);
            else {
                prjFests = new ArrayList<>();
                rslt.put(awardRec.pcode, prjFests);
            }
            Fest curfest = findFest(prjFests, awardRec.fest_uid);
            curfest.name = awardRec.fest_name;
            curfest.iconName = awardRec.icon;
            Award curaward = awardRec.isnomination ? findAward(curfest.nominations, awardRec.faward_id) : findAward(curfest.awards, awardRec.faward_id);
            curaward.name = awardRec.award_aname;
            curaward.awardee = awardRec.awardee;
        }
        return rslt;
    }

    public RspPrj getMovie(String puNumber, HttpServletRequest request) throws Exception {
        RestHelper.getInstance().setBioParamToRequest("puNumber", puNumber, request);
        RspPrj rslt = new RspPrj();
        Map<String, List<Fest>> festsMap = _loadFests("api.ktpub.awards-released", request);
        rslt.movies = RestHelper.getInstance().getListAll("api.ktpub.prj-released", request, Prj.class);
        for (Prj prj : rslt.movies) {
            try {
                prj.companies = new ArrayList<>();
                String[] compList = Strings.split(prj.compList, "|-|");
                for (String compItem : compList) {
                    PrjComp pc = new PrjComp();
                    pc.id = compItem.substring(1, compItem.indexOf("]"));
                    pc.name = compItem.substring(compItem.indexOf("]") + 2);
                    prj.companies.add(pc);
                    _prepareAwards(festsMap, prj);
                }
                _decodeFinancingSource(prj);
            } catch (Exception e) {
                throw new Exception(String.format("Error on processing prg: %s(%s)", prj.id, prj.name), e);
            }

        }
        rslt.unfulfilledObligationsRelevance = RestHelper.getInstance().getScalar(
                "api.ktpub.get-gparam",
                Paramus.createParams("paramname", "unfulfilled-obligations-relevance"),
                String.class, null);
        return rslt;
    }

    @GET
    @Path("/rating/movies")
    @Produces(MediaType.APPLICATION_JSON)
    public RspPrj getMovies(@Context HttpServletRequest request) throws Exception {
        String puNum = RestHelper.getInstance().getBioParamFromRequest("puNumber", request, String.class);
        if(!Strings.isNullOrEmpty(puNum))
            return getMovie(puNum, request);
        return _getProjects("api.ktpub.prjs-released", "api.ktpub.awards-released", request);
    }

    @GET
    @Path("/rating/companies")
    @Produces(MediaType.APPLICATION_JSON)
    public RspComp getCompanies(@Context HttpServletRequest request) throws Exception {
        WrappedRequest req = ((WrappedRequest)request);
        boolean forceAll = Strings.isNullOrEmpty(req.getBioQueryParams().pageSizeOrig);
        _decodeSort(request, Sort.NullsPosition.DEFAULT);
        final String bioCode = "api.ktpub.comps";

        _decodeReleaseStatus(request);
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
            String[] pusList = comp.pus.length() > 0 ? Strings.split(comp.pus.substring(0, comp.pus.length()-2), "|-|") : new String[0];
            for(int i=0; i<pcodesList.length; i++) {
                CompPrj pc = new CompPrj();
                pc.id = pcodesList[i];
                pc.puNumber = (!Strings.isNullOrEmpty(pusList[i]) && !pusList[i].equals("*")) ? pusList[i] : null;
                pc.name = filmnamesList[i];
                pc.financing_source = new ArrayList<>();
                boolean subnMK = flagsList[i].charAt(0) == '1';
                boolean subnFK = flagsList[i].charAt(1) == '1';
                if(subnMK){
                    FSrc fsrc = new FSrc();
                    fsrc.id = 0;
                    fsrc.name = "Министерство культуры";
                    fsrc.short_name = "МК";
                    fsrc.image = String.format("%s/big/financing_source_%02d.png", CS_EAIS_RES_FTP, fsrc.id);
                    pc.financing_source.add(fsrc);
                }
                if(subnFK){
                    FSrc fsrc = new FSrc();
                    fsrc.id = 1;
                    fsrc.name = "Фонд кино";
                    fsrc.short_name = "ФК";
                    fsrc.image = String.format("%s/big/financing_source_%02d.png", CS_EAIS_RES_FTP, fsrc.id);
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
        dataResult.total_unfulfilledObligationsDebt = totals.unfulfilledObligationsDebt;
        dataResult.total_unfulfilledObligationsCost = totals.unfulfilledObligationsCost;
        dataResult.total_box_office = totals.box_office;
        dataResult.total_audience = totals.audience;

        return dataResult;
    }

    @GET
    @Path("/projects/inProduction")
    @Produces(MediaType.APPLICATION_JSON)
    public RspPrj getProjectsInProduction(@Context HttpServletRequest request) throws Exception {
        return _getProjects("api.ktpub.prjs-inProduction", "api.ktpub.awards-inProduction", request);
    }

    @GET
    @Path("/projects/unfulfilledObligations")
    @Produces(MediaType.APPLICATION_JSON)
    public RspPrj getProjectsUnfulfilledObligations(@Context HttpServletRequest request) throws Exception {
        return _getProjects("api.ktpub.prjs-unoblig", "api.ktpub.awards-unoblig", request);
    }

    @GET
    @Path("/search/hints")
    @Produces(MediaType.APPLICATION_JSON)
    public RspHint getHints(@Context HttpServletRequest request) throws Exception {
        WrappedRequest req = ((WrappedRequest)request);
        //hintType = 1-рейтиг фильмов, 2-рейтинг  компаний, 3-в производстве, 4-неисполненные обязательства
        int hintType = req.getBioQueryParam("hintType", int.class, 1);
        if(hintType == 1) {
            RestHelper.getInstance().setBioParamToRequest("p_released", "1", request);
            RestHelper.getInstance().setBioParamToRequest("p_unoblig", null, request);
        } else if(hintType == 2) {
            _decodeReleaseStatus(request);
            RestHelper.getInstance().setBioParamToRequest("p_unoblig", null, request);
        } else if(hintType == 3) {
            RestHelper.getInstance().setBioParamToRequest("p_released", "0", request);
            RestHelper.getInstance().setBioParamToRequest("p_unoblig", null, request);
        } else if(hintType == 4) {
            RestHelper.getInstance().setBioParamToRequest("p_released", null, request);
            RestHelper.getInstance().setBioParamToRequest("p_unoblig", "1", request);
        }

        _decodeSupportId(request);
        final String bioCode = "api.ktpub.hints";
        RspHint dataResult = new RspHint();
        dataResult.response = RestHelper.getInstance().getListAll(bioCode, request, Hint.class);
        return dataResult;
    }

}
