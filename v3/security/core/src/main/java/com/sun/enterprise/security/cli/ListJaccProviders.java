/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.enterprise.security.cli;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import com.sun.enterprise.config.serverbeans.JaccProvider;
import com.sun.enterprise.config.serverbeans.SecurityService;
import com.sun.enterprise.config.serverbeans.Server;
import com.sun.enterprise.util.LocalStringManagerImpl;
import com.sun.enterprise.util.SystemPropertyConstants;
import java.beans.PropertyVetoException;
import java.util.List;
import org.glassfish.api.ActionReport;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.Cluster;
import org.glassfish.api.admin.RuntimeType;
import org.glassfish.api.admin.ServerEnvironment;
import org.glassfish.config.support.CommandTarget;
import org.glassfish.config.support.TargetType;
import org.jvnet.hk2.annotations.Inject;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.component.PerLookup;
import org.jvnet.hk2.config.ConfigSupport;
import org.jvnet.hk2.config.SingleConfigCode;
import org.jvnet.hk2.config.TransactionFailure;

/**
 *Usage: list-jacc-providers
 *         [--help] [--user admin_user] [--passwordfile file_name]
 *         [ --target  target_name]
 *
 */
@Service(name="list-jacc-providers")
@Scoped(PerLookup.class)
@I18n("list.jacc.provider")
@Cluster({RuntimeType.DAS, RuntimeType.INSTANCE})
@TargetType({CommandTarget.DAS,CommandTarget.STANDALONE_INSTANCE,CommandTarget.CLUSTER, CommandTarget.CONFIG})
public class ListJaccProviders implements AdminCommand {

    final private static LocalStringManagerImpl localStrings =
        new LocalStringManagerImpl(DeleteJaccProvider.class);

    @Param(name = "target", optional = true, defaultValue =
    SystemPropertyConstants.DEFAULT_SERVER_INSTANCE_NAME)
    private String target;

    @Inject(name = ServerEnvironment.DEFAULT_INSTANCE_NAME)
    private Config config;
    @Inject
    private Domain domain;

    @Override
    public void execute(AdminCommandContext context) {
        final ActionReport report = context.getActionReport();
        Server targetServer = domain.getServerNamed(target);
        if (targetServer != null) {
            config = domain.getConfigNamed(targetServer.getConfigRef());
        }
        com.sun.enterprise.config.serverbeans.Cluster cluster = domain.getClusterNamed(target);
        if (cluster != null) {
            config = domain.getConfigNamed(cluster.getConfigRef());
        }
        SecurityService securityService = config.getSecurityService();

        List<JaccProvider> jaccProviders = securityService.getJaccProvider();
        JaccProvider jprov = null;
        for (JaccProvider jaccProv : jaccProviders) {
            ActionReport.MessagePart part = report.getTopMessagePart().addChild();
            part.setMessage(jaccProv.getName());
        }
        report.setActionExitCode(ActionReport.ExitCode.SUCCESS);

    }

}
