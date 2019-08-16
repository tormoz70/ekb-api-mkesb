package ru.fk.ekb.rapi.restful.srvc;

import ru.bio4j.ng.service.api.HttpParamMap;

public class HttpParamMapImpl implements HttpParamMap {

    @Override
    public String username() {
        return "userName";
    }

    @Override
    public String password() {
        return "password";
    }

    @Override
    public String pageSize() {
        return "per_page";
    }

    @Override
    public String page() {
        return "page";
    }

    @Override
    public String offset() {
        return null;
    }

    @Override
    public String securityToken() {
        return null;
    }

    @Override
    public String pageSizeHeader() {
        return null;
    }

    @Override
    public String pageHeader() {
        return null;
    }

    @Override
    public String offsetHeader() {
        return null;
    }

    @Override
    public String securityTokenHeader() {
        return "X-SToken";
    }

    @Override
    public String clientHeader() {
        return null;
    }

    @Override
    public String clientVerHeader() {
        return null;
    }

}
