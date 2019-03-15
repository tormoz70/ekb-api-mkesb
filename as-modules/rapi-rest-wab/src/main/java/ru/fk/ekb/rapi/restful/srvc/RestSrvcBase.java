package ru.fk.ekb.rapi.restful.srvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.RestServiceBase;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

public class RestSrvcBase extends RestServiceBase {
    private static final Logger LOG = LoggerFactory.getLogger(RestSrvcBase.class);

    @Context
    private ServletContext servletContext;

    @Override
    protected ServletContext getServletContext() {
        return servletContext;
    }
}
