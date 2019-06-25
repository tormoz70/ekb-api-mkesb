package ru.fk.ekb.rapi.restful.srvc;

import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.FCloudApi;
import ru.bio4j.ng.service.api.SecurityService;
import ru.bio4j.ng.service.types.AppServiceTypeGetters;
import ru.fk.ekb.module.impl.EkbAppModule;

public class EkbAppServiceTypes implements AppServiceTypeGetters {

    @Override
    public Class<? extends AppService> getAppServiceClass() {
        return EkbAppModule.class;
    }
    @Override
    public Class<? extends FCloudApi> getFCloudApiClass() {
        return FCloudApi.class;
    }
    @Override
    public Class<? extends SecurityService> getSecurityServiceClass() {
        return SecurityService.class;
    }

}
