package ru.fk.ekb.security.module.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.database.api.SQLStoredProc;
import ru.bio4j.ng.database.oracle.SQLContextFactory;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.api.DbConfigProvider;
import ru.bio4j.ng.service.api.SecurityService;
import ru.bio4j.ng.service.types.SecurityServiceBase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

@Component
@Instantiate
@Provides(specifications = SecurityService.class)
public class SecurityModuleImpl extends SecurityServiceBase implements SecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityModuleImpl.class);

    private CurUserProvider curUserProvider;

    private CurUserProvider getCurUserProvider() {
        if(curUserProvider == null)
            curUserProvider = new CurUserProvider(this);
        return curUserProvider;
    }

    @Requires
    private EventAdmin eventAdmin;
    @Requires
    private DbConfigProvider dbConfigProvider;

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

    protected SQLContext createSQLContext() throws Exception {
        SQLContextConfig config = dbConfigProvider.getConfig();
        return SQLContextFactory.create(config);
    }

    @Override
    public User login(final BioQueryParams qprms) throws Exception {
        final String login = qprms.login;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        if (isNullOrEmpty(login))
            throw new BioError.Login.Unauthorized();
        LOG.debug("User {} logging in...", login);

        final SQLDefinition sqlDefinition = this.getSQLDefinition("bio.login");
        final SQLContext context = this.getSQLContext();
        try {
            final List<Param> params = new ArrayList<Param>();
            params.add(Param.builder().name("p_login").value(login).build());
            params.add(Param.builder().name("p_remote_ip").value(remoteIP).build());
            params.add(Param.builder().name("p_remote_client").value(remoteClient).build());
            params.add(Param.builder().name("v_stoken").type(MetaType.STRING).direction(Param.Direction.OUT).build());
            qprms.stoken = context.execBatch((ctx) -> {
                SQLStoredProc prc = ctx.createStoredProc();
                prc.init(ctx.getCurrentConnection(), sqlDefinition.getExecSqlDef().getPreparedSql(), sqlDefinition.getExecSqlDef().getParamDeclaration()).execSQL(params, ctx.getCurrentUser());
                return Paramus.paramValue(params, "v_stoken", String.class, null);
            }, null);


            return getUser(qprms);
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 20401:
                    throw new BioError.Login.Unauthorized();
                case 20402:
                    throw new BioError.Login.Unauthorized();
                case 20403:
                    throw new BioError.Login.Unauthorized();
                case 20404:
                    throw new BioError.Login.Unauthorized();
                default:
                    throw ex;
            }
        }
    }

    @Override
    public User restoreUser(String stokenOrUsrUid) throws Exception {
        return getCurUserProvider().loadUserFromDB(stokenOrUsrUid);
    }

    public User getUser(final BioQueryParams qprms) throws Exception {
        final String stoken = qprms.stoken;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        Boolean isLoggedin = loggedin(qprms);
        if(isLoggedin)
            return getCurUserProvider().getUser(stoken, remoteIP, remoteClient);
        throw new BioError.Login.Unauthorized();
    }

    @Override
    public void logoff(final BioQueryParams qprms) throws Exception {
        final String remoteIP = qprms.remoteIP;
        final String stoken = qprms.stoken;
        final SQLDefinition sqlDefinition = this.getSQLDefinition("bio.logoff");
        final SQLContext context = this.getSQLContext();

        String rslt = context.execBatch((ctx) -> {
            List<Param> prms = new ArrayList<>();
            Paramus.setParamValue(prms, "p_stoken", stoken);
            Paramus.setParamValue(prms, "p_remote_ip", remoteIP);
            SQLStoredProc sp = ctx.createStoredProc();
            sp.init(ctx.getCurrentConnection(), sqlDefinition.getExecSqlDef().getPreparedSql(), sqlDefinition.getExecSqlDef().getParamDeclaration())
                    .execSQL(prms, ctx.getCurrentUser());
            try (Paramus paramus = Paramus.set(prms)) {
                return paramus.getValueAsStringByName("v_rslt", true);
            }
        }, null);

        LOG.debug("Logoff rslt: {}", rslt);
    }

    @Override
    public Boolean loggedin(final BioQueryParams qprms) throws Exception {
        final String stoken = qprms.stoken;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        if (isNullOrEmpty(stoken))
            throw new BioError.Login.Unauthorized();
        final SQLDefinition sqlDefinition = this.getSQLDefinition("bio.loggedin");
        final SQLContext context = this.getSQLContext();

        String rslt = context.execBatch((ctx) -> {
            List<Param> prms = new ArrayList<>();
            Paramus.setParamValue(prms, "p_stoken", stoken);
            Paramus.setParamValue(prms, "p_remote_ip", remoteIP);
            Paramus.setParamValue(prms, "p_remote_client", remoteClient);
            SQLStoredProc sp = ctx.createStoredProc();
            sp.init(ctx.getCurrentConnection(), sqlDefinition.getExecSqlDef().getPreparedSql(), sqlDefinition.getExecSqlDef().getParamDeclaration())
                    .execSQL(prms, null);
            return Paramus.paramValue(prms, "v_rslt", String.class, null);
        }, null);
        LOG.debug("Loggedin rslt: {}", rslt);
        return Strings.compare(rslt, "OK", true);
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
