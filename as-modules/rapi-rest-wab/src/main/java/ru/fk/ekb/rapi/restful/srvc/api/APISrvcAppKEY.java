package ru.fk.ekb.rapi.restful.srvc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jecksons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.OdacService;
import ru.bio4j.ng.service.types.RestApiAdapter;
import ru.bio4j.ng.service.types.RestHelper;
import ru.bio4j.ng.service.types.WrappedRequest;
import ru.fk.ekb.rapi.restful.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

//import ru.bio4j.ng.commons.utils.Jsons;

@Path("/appkey-api")
public class APISrvcAppKEY {


    @POST
    @Path("/reg")
    @Produces(MediaType.APPLICATION_JSON)
    public ABean kt_film_stat_get(@Context HttpServletRequest request) {
        ABean prms = Jecksons.getInstance().decodeABean(((WrappedRequest)request).getBioQueryParams().jsonData);
        RestHelper.getInstance().exec("api.appkey.regapp", prms);
        return prms;
    }

}
