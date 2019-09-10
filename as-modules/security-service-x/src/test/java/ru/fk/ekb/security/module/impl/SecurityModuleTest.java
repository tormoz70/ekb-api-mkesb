package ru.fk.ekb.security.module.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
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

@Test
public class SecurityModuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityModuleTest.class);

    @Test
    public void testSso() {

    }

}
