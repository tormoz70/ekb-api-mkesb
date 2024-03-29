package ru.fk.ekb.module.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.oracle.SQLContextFactory;
import ru.bio4j.ng.model.transport.SQLContextConfig;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.HttpParamMap;
import ru.bio4j.ng.service.api.DbConfigProvider;
import ru.bio4j.ng.service.types.AppServiceBase;

import java.util.Dictionary;

@Component
@Instantiate
@Provides(specifications = EkbAppModule.class)
public class ApplicationImpl extends AppServiceBase implements EkbAppModule {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationImpl.class);

    @Requires
    private EventAdmin eventAdmin;
    @Requires
    private DbConfigProvider dbConfigProvider;
    @Override
    protected EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    @Context
    private BundleContext bundleContext;

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    @Override
    protected SQLContext createSQLContext() throws Exception {
        SQLContextConfig cfg = dbConfigProvider.getConfig();
        SQLContext context = SQLContextFactory.create(cfg);
/*
        context.addAfterEvent((SQLContext sender, SQLConnectionConnectedEvent.Attributes attrs) -> {
                Connection conn = attrs.getConnection();
                if (conn != null && attrs.getUser() != null) {
                    User user = attrs.getUser();
                    LOG.debug("onAfterGetConnection - start initialising session context...");
                    CallableStatement cs1 = attrs.getConnection().prepareCall("{call efond2_api.set_sess_context(?, ?, ?, ?, ?, ?)}");
                    cs1.setString(1, user.getInnerUid());
                    cs1.setString(2, user.getRemoteIP());
                    cs1.setString(3, user.getRoles());
                    cs1.setString(4, user.getOrgId());
                    cs1.setString(5, null);
                    cs1.setString(6, null);
                    cs1.execute();
                    cs1.close();
                    LOG.debug("onAfterGetConnection - OK. session context initialised!");
                }
        });
*/
        return context;
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "mkesb-config-updated");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");

        //registerRouteHandler(BioRoute.CRUD_DATASET_EXP.getAlias(), new ExpDatasetHandler());

        //registerHttpRequestProcessor("ekb.exp2.xls.get-result", new EkbExportGetResult(this));

        fireEventServiceStarted();
        LOG.debug("Started");
    }

}
