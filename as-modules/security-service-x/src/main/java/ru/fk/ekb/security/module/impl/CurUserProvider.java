package ru.fk.ekb.security.module.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class CurUserProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CurUserProvider.class);

    private final SecurityModuleImpl module;

    public CurUserProvider(SecurityModuleImpl module){
        this.module = module;
    }

    private Map<String, User> usersCache;
    private Map<String, User> getUserCache() {
        if(usersCache == null)
            usersCache = new HashMap<>();
        return usersCache;
    }

    public User loadUserFromDB(final String stokenOrUsrUid) throws Exception {
        if (isNullOrEmpty(stokenOrUsrUid))
            throw new BioError.Login.Unauthorized();
        final SQLDefinition sqlDefinition = module.getSQLDefinition("bio.get-user");
        final SQLContext sqlContext = module.getSQLContext();
        User result = sqlContext.execBatch((ctx) -> {
                    final List<Param> prms = new ArrayList<>();
            Paramus.setParamValue(prms, "p_stoken", stokenOrUsrUid);
            return ctx.createCursor()
                    .init(ctx.getCurrentConnection(), sqlDefinition.getSelectSqlDef().getPreparedSql(), sqlDefinition.getSelectSqlDef().getParamDeclaration())
                    .firstBean(prms, ctx.getCurrentUser(), User.class);
        }, null);
        return result;
    }

    private void storeUser2Cache(final String stoken, final User user) {
        getUserCache().put(stoken, user);
    }

    private User restoreUserFromCache(final String stoken, final String remoteIP, final String remoteClient) {
        User user = null;
        if(getUserCache().containsKey(stoken)) {
            user = getUserCache().get(stoken);
            user.setRemoteIP(remoteIP);
            user.setRemoteClient(remoteClient);
        }
        return user;
    }

    public User getUser(final String stoken, final String remoteIP, final String remoteClient) throws Exception {
        if (isNullOrEmpty(stoken))
            throw new BioError.Login.Unauthorized();
        User result = null;
        LOG.debug("User getting from cache by stoken({})...", stoken);
        result = restoreUserFromCache(stoken, remoteIP, remoteClient);
        if (result == null) {
            LOG.debug("User not found in cache by stoken({})!", stoken);
            LOG.debug("User loading from DB by stoken({})...", stoken);
            result = loadUserFromDB(stoken);
            LOG.debug("User loaded from DB by stoken({}): {}", Utl.buildBeanStateInfo(result, "User", "  "));
            if (result != null) {
                LOG.debug("User storing in cache by stoken({})...", stoken);
                storeUser2Cache(stoken, result);
                LOG.debug("User stored in cache by stoken({})!", stoken);
            }
        } else
            LOG.debug("User found in cache by stoken({}): {}", Utl.buildBeanStateInfo(result, "User", "  "));

        if(result == null)
            throw new BioError.Login.Unauthorized();
        else {
            result.setRemoteIP(remoteIP);
            result.setRemoteClient(remoteClient);
            return result;
        }

    }
}
