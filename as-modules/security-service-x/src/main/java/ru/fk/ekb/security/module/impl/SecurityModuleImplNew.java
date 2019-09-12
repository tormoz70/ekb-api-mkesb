package ru.fk.ekb.security.module.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.SecurityServiceBase;
import ru.bio4j.ng.service.types.SsoClient;

import java.util.Dictionary;

@Component
@Instantiate
@Provides(specifications = SecurityService.class)
public class SecurityModuleImplNew extends SecurityServiceBase implements SecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityModuleImplNew.class);

    private volatile SsoClient ssoClient;

    private synchronized void initSsoClient() {
        if(ssoClient == null)
            ssoClient = SsoClient.create("http://localhost:8099/sso");
    }

    private SsoClient getSsoClient() {
        initSsoClient();
        return ssoClient;
    }

    @Requires
    private EventAdmin eventAdmin;

    @Override
    protected EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    private BundleContext bundleContext;

    @Context
    public void setBundleContext(BundleContext context) {
        this.bundleContext = context;
        LOG.debug("Field \"bundleContext\" - updated!");
    }

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    @Override
    protected SQLContext createSQLContext() throws Exception {
        return null;
    }

    @Override
    public User login(final BioQueryParams qprms) throws Exception {
        return getSsoClient().login(qprms);
    }

    @Override
    public User getUser(final BioQueryParams qprms) throws Exception {
        return getSsoClient().curUser(qprms);
    }

    @Override
    public User restoreUser(String stokenOrUsrUid) throws Exception {
        return getSsoClient().restoreUser(stokenOrUsrUid, null, null);
    }

    @Override
    public void logoff(final BioQueryParams qprms) throws Exception {
        getSsoClient().logoff(qprms);
    }

    @Override
    public Boolean loggedin(final BioQueryParams qprms) throws Exception {
        return getSsoClient().loggedin(qprms);
    }


    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "security-config-updated");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        //fireEventModuleUpdated();
        LOG.debug("Started");
    }

}
