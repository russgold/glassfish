/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package com.sun.enterprise.config.serverbeans;

import com.sun.enterprise.config.serverbeans.customvalidators.NotTargetKeyword;
import com.sun.enterprise.config.serverbeans.customvalidators.NotDuplicateTargetName;
import com.sun.enterprise.config.serverbeans.customvalidators.ConfigRefConstraint;
import com.sun.enterprise.config.serverbeans.customvalidators.ConfigRefValidator;
import com.sun.enterprise.util.LocalStringManagerImpl;
import com.sun.enterprise.util.io.FileUtils;
import com.sun.logging.LogDomains;
import java.io.*;
import org.glassfish.api.ActionReport;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.api.admin.*;
import org.glassfish.config.support.*;
import static org.glassfish.config.support.Constants.NAME_SERVER_REGEX;

import com.sun.enterprise.config.serverbeans.BindableResource;
import com.sun.enterprise.config.serverbeans.ResourceRef;
import org.jvnet.hk2.annotations.Inject;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.component.Habitat;
import org.jvnet.hk2.component.PerLookup;
import org.jvnet.hk2.config.*;
import org.jvnet.hk2.component.Injectable;
import org.glassfish.api.admin.config.Named;
import org.glassfish.api.admin.config.PropertyDesc;
import org.glassfish.api.admin.config.ReferenceContainer;

import java.beans.PropertyVetoException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.api.admin.config.PropertiesDesc;
import org.jvnet.hk2.config.types.Property;
import org.jvnet.hk2.config.types.PropertyBag;

import org.glassfish.quality.ToDo;

import javax.validation.Payload;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * A cluster defines a homogeneous set of server instances that share the same
 * applications, resources, and configuration.
 */
@Configured
@SuppressWarnings("unused")
@ConfigRefConstraint(message="{configref.invalid}", payload= ConfigRefValidator.class)
@NotDuplicateTargetName(message="{cluster.duplicate.name}", payload=Cluster.class)
public interface Cluster extends ConfigBeanProxy, Injectable, PropertyBag, Named, SystemPropertyBag, ReferenceContainer, RefContainer, Payload {

    /**
     * Sets the cluster name
     * @param value cluster name
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Param(name="name", primary = true)
    @Override
    public void setName(String value) throws PropertyVetoException;

    @NotTargetKeyword(message="{cluster.reserved.name}", payload=Cluster.class)
    @Pattern(regexp=NAME_SERVER_REGEX, message="{cluster.invalid.name}", payload=Cluster.class)
    @Override
    public String getName();

    /**
     * points to a named config. All server instances in the cluster
     * will share this config.
     *
     * @return a named config name
     */
    @Attribute
    @NotNull
    @Pattern(regexp=NAME_SERVER_REGEX)
    String getConfigRef();

    /**
     * Sets the value of the configRef property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Param(name="config", optional=true)
    @I18n("generic.config")
    void setConfigRef(String value) throws PropertyVetoException;

    /**
     * Gets the value of the gmsEnabled property.
     *
     * When "gms-enabled" is set to "true", the GMS services will be
     * started as a lifecycle module in each the application server in the
     * cluster.
     *
     * @return true | false as a string, null means false
     */
    @Attribute (defaultValue="true", dataType=Boolean.class, required=true)
    @NotNull
    String getGmsEnabled();

    /**
     * Sets the value of the gmsEnabled property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Param(name="gmsenabled", optional=true)
    void setGmsEnabled(String value) throws PropertyVetoException;

    /**
     * Gets the value of the gmsMulticastPort property.
     *
     * This is the communication port GMS uses to listen for group  events.
     * This should be a valid port number.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    @Min(value=2048)
    @Max(value=32000)
    @NotNull
    String getGmsMulticastPort();

    /**
     * Sets the value of the gmsMulticastPort property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Param(name="multicastport", optional=true, alias="heartbeatport")
    void setGmsMulticastPort(String value) throws PropertyVetoException;

    /**
     * Gets the value of the gmsMulticastAddress property.
     *
     * This is the address (only multicast supported) at which GMS will
     * listen for group events. Must be unique for each cluster.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    @NotNull
    String getGmsMulticastAddress();

    /**
     * Sets the value of the gmsMulticastAddress property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Param(name="multicastaddress", optional=true, alias="heartbeataddress")
    void setGmsMulticastAddress(String value) throws PropertyVetoException;

    /**
     * Gets the value of the gmsBindInterfaceAddress property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    String getGmsBindInterfaceAddress();

    /**
     * Sets the value of the gmsBindInterfaceAddress property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Param(name="bindaddress", optional=true)
    void setGmsBindInterfaceAddress(String value) throws PropertyVetoException;

    /**
     * Gets the value of the heartbeatEnabled property.
     *
     * When "heartbeat-enabled" is set to "true", the GMS services will be
     * started as a lifecycle module in each the application server in the
     * cluster.When "heartbeat-enabled" is set to "false", GMS will not be
     * started and its services will be unavailable. Clusters should function
     * albeit with reduced functionality.
     *
     * @return true | false as a string, null means false
     */
    @Deprecated
    @Attribute
    String getHeartbeatEnabled();

    /**
     * Sets the value of the heartbeatEnabled property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Deprecated
    void setHeartbeatEnabled(String value) throws PropertyVetoException;

    /**
     * Gets the value of the heartbeatPort property.
     *
     * This is the communication port GMS uses to listen for group  events.
     * This should be a valid port number.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    //@Min(value=2048)
    //@Max(value=49151)
    @Deprecated
    String getHeartbeatPort();

    /**
     * Sets the value of the heartbeatPort property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Deprecated
    void setHeartbeatPort(String value) throws PropertyVetoException;

    /**
     * Gets the value of the heartbeatAddress property.
     *
     * This is the address (only multicast supported) at which GMS will
     * listen for group events.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    @Deprecated
    String getHeartbeatAddress();

    /**
     * Sets the value of the heartbeatAddress property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    @Deprecated
    void setHeartbeatAddress(String value) throws PropertyVetoException;

    /**
     * Gets the value of the serverRef property.
     *
     * List of servers in the cluster
     *
     * @return list of configured {@link ServerRef }
     */
    @Element
    List<ServerRef> getServerRef();

    /**
     * Gets the value of the systemProperty property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the systemProperty property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSystemProperty().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link SystemProperty }
     */
    @Element
    @ToDo(priority=ToDo.Priority.IMPORTANT, details="Provide PropertyDesc for legal system props" )
    @Param(name="systemproperties",optional=true)
    @Override
    List<SystemProperty> getSystemProperty();

    /**
     *	Properties as per {@link org.jvnet.hk2.config.types.PropertyBag}
     */
    @ToDo(priority=ToDo.Priority.IMPORTANT, details="Complete PropertyDesc for legal props" )
    @PropertiesDesc(props={
        @PropertyDesc(name="GMS_LISTENER_PORT", defaultValue = "9090",
            description = "GMS listener port")
    })
    @Element
    @Param(name="properties", optional=true)
    @Override
    List<Property> getProperty();

    /**
     * Returns the cluster configuration reference
     * @return the config-ref attribute
     */
    @DuckTyped
    @Override
    String getReference();

    @DuckTyped
    List<Server> getInstances();

    @DuckTyped
    public ServerRef getServerRefByRef(String ref);

    // four trivial methods that ReferenceContainer's need to implement
    @DuckTyped
    @Override
    boolean isCluster();

    @DuckTyped
    @Override
    boolean isServer();

    @DuckTyped
    @Override
    boolean isDas();

    @DuckTyped
    @Override
    boolean isInstance();

    @DuckTyped
    ApplicationRef getApplicationRef(String appName);

    @DuckTyped
    ResourceRef getResourceRef(String refName);

    @DuckTyped
    boolean isResourceRefExists(String refName);

    @DuckTyped
    void createResourceRef(String enabled, String refName) throws TransactionFailure;

    @DuckTyped
    void deleteResourceRef(String refName) throws TransactionFailure;


    class Duck {
        public static boolean isCluster(Cluster me) { return true; }
        public static boolean isServer(Cluster me)  { return false; }
        public static boolean isInstance(Cluster me) { return false; }
        public static boolean isDas(Cluster me) { return false; }

            public static String getReference(Cluster cluster) {
            return cluster.getConfigRef();
        }

        public static List<Server> getInstances(Cluster cluster) {

            Dom clusterDom = Dom.unwrap(cluster);
            Domain domain =
                    clusterDom.getHabitat().getComponent(Domain.class);

            ArrayList<Server> instances = new ArrayList<Server>();
            for (ServerRef sRef : cluster.getServerRef()) {
                Server svr =  domain.getServerNamed(sRef.getRef());
                // the instance's domain.xml only has its own server 
                // element and not other server elements in the cluster 
                if (svr != null) {
                    instances.add(domain.getServerNamed(sRef.getRef()));
                }
            }
            return instances;
        }

        public static ServerRef getServerRefByRef(Cluster c, String name) {
            for (ServerRef ref : c.getServerRef()) {
                if (ref.getRef().equals(name)) {
                    return ref;
                }
            }
            return null;
        }

        public static ApplicationRef getApplicationRef(Cluster cluster,
                String appName) {
            for (ApplicationRef appRef : cluster.getApplicationRef()) {
                if (appRef.getRef().equals(appName)) {
                    return appRef;
                }
            }
            return null;
        }

        public static ResourceRef getResourceRef(Cluster cluster, String refName) {
            for (ResourceRef ref : cluster.getResourceRef()) {
                if (ref.getRef().equals(refName)) {
                    return ref;
                }
            }
            return null;
        }

        
        public static boolean isResourceRefExists(Cluster cluster, String refName) {
            return getResourceRef(cluster, refName) != null;
        }

        public static void deleteResourceRef(Cluster cluster, String refName) throws TransactionFailure {
            final ResourceRef ref = getResourceRef(cluster, refName);
            if (ref != null) {
                ConfigSupport.apply(new SingleConfigCode<Cluster>() {

                    public Object run(Cluster param) {
                        return param.getResourceRef().remove(ref);
                    }
                }, cluster);
            }
        }

        public static void createResourceRef(Cluster cluster, final String enabled, final String refName) throws TransactionFailure {

            ConfigSupport.apply(new SingleConfigCode<Cluster>() {

                public Object run(Cluster param) throws PropertyVetoException, TransactionFailure {

                    ResourceRef newResourceRef = param.createChild(ResourceRef.class);
                    newResourceRef.setEnabled(enabled);
                    newResourceRef.setRef(refName);
                    param.getResourceRef().add(newResourceRef);
                    return newResourceRef;
                }
            }, cluster);
        }

    }

    @Service
    @Scoped(PerLookup.class)
    class Decorator implements CreationDecorator<Cluster> {

        @Param(name="config", optional=true)
        String configRef=null;

        @Param(optional = true,obsolete=true)
        String hosts=null;

        @Param(optional = true,obsolete=true)
        String haagentport;

        @Param(optional = true,obsolete=true)
        String haadminpassword=null;

        @Param(optional = true,obsolete=true)
        String haadminpasswordfile=null;

        @Param(optional = true,obsolete=true)
        String devicesize=null;

        @Param(optional = true,obsolete=true)
        String haproperty=null;

        @Param(optional = true,obsolete=true)
        String autohadb=null;

        @Param(optional = true,obsolete=true)
        String portbase=null;

        @Inject
        Habitat habitat;

        @Inject
        ServerEnvironment env;

        @Inject
        Domain domain;

        @Inject
        CommandRunner runner;

        /**
         * Decorates the newly CRUD created cluster configuration instance.
         * tasks :
         *      - ensures that it references an existing configuration
         *      - creates a new config from the default-config if no config-ref
         *        was provided.
         *      - check for deprecated parameters.
         *
         * @param context administration command context
         * @param instance newly created configuration element
         * @throws TransactionFailure
         * @throws PropertyVetoException
         */
        @Override
        public void decorate(AdminCommandContext context, final Cluster instance) throws TransactionFailure, PropertyVetoException {
            Logger logger = LogDomains.getLogger(Cluster.class, LogDomains.ADMIN_LOGGER);
            LocalStringManagerImpl localStrings = new LocalStringManagerImpl(Cluster.class);
            Transaction t = Transaction.getTransaction(instance);
            //check if cluster software is installed else fail , see issue 12023
            final CopyConfig command = (CopyConfig) runner
                    .getCommand("copy-config", context.getActionReport(), context.getLogger());
            if (command == null ) {
                throw new TransactionFailure(localStrings.getLocalString("cannot.execute.command",
                        "Cluster software is not installed"));
            }
            final String instanceName = instance.getName();
            if (instance.getGmsBindInterfaceAddress() == null) {
                instance.setGmsBindInterfaceAddress(String.format(
                    "${GMS-BIND-INTERFACE-ADDRESS-%s}",
                    instanceName));
            }
            if (instance.getGmsMulticastAddress() == null) {
                instance.setGmsMulticastAddress(generateHeartbeatAddress());
            }
            if (instance.getGmsMulticastPort() == null) {
                instance.setGmsMulticastPort(generateHeartbeatPort());
            }

            Property gmsListenerPort = instance.createChild(Property.class);
            gmsListenerPort.setName("GMS_LISTENER_PORT");
            gmsListenerPort.setValue(String.format("${GMS_LISTENER_PORT-%s}", instanceName));
            instance.getProperty().add(gmsListenerPort);

            if (configRef==null) {
                Config config = habitat.getComponent(Config.class, "default-config");
                if (config==null) {
                    config = habitat.getAllByContract(Config.class).iterator().next();
                    logger.warning(localStrings.getLocalString(Cluster.class,
                            "Cluster.no_default_config_found",
                            "No default config found, using config {0} as the default config for the cluster {1}",
                            config.getName(), instance.getName()));
                }

                Configs configs = domain.getConfigs();
                Configs writableConfigs = t.enroll(configs);
                final String configName = instance.getName() + "-config";
                instance.setConfigRef(configName);
                command.copyConfig(writableConfigs,config,configName,logger);
                

            }  else {

                // cluster using specified config
                Config specifiedConfig = domain.getConfigs().getConfigByName(configRef);
                if (specifiedConfig == null) {
                    throw new TransactionFailure(localStrings.getLocalString(
                            "noSuchConfig", "Configuration {0} does not exist.", configRef));
                }
            }

            Resources resources = domain.getResources();
            for (Resource resource : resources.getResources()) {
                if (resource.getObjectType().equals("system-all") || resource.getObjectType().equals("system-instance")) {
                    String name=null;
                    if (resource instanceof BindableResource) {
                        name = ((BindableResource) resource).getJndiName();
                    }
                    if (resource instanceof Named) {
                        name = ((Named) resource).getName();
                    }
                    if (name==null) {
                        throw new TransactionFailure("Cannot add un-named resources to the new server instance");
                    }
                    ResourceRef newResourceRef = instance.createChild(ResourceRef.class);
                    newResourceRef.setRef(name);
                    instance.getResourceRef().add(newResourceRef);
                }
            }
            for (Application application : domain.getApplications().getApplications()) {
                if (application.getObjectType().equals("system-all") || application.getObjectType().equals("system-instance")) {
                    ApplicationRef newAppRef = instance.createChild(ApplicationRef.class);
                    newAppRef.setRef(application.getName());
                    instance.getApplicationRef().add(newAppRef);
                }
            }

            if (hosts!=null ||
                    haagentport!=null ||
                    haadminpassword!=null ||
                    haadminpasswordfile!=null ||
                    devicesize!=null ||
                    haproperty!=null ||
                    autohadb!=null ||
                    portbase!=null
                    ) {
               context.getActionReport().setActionExitCode(ActionReport.ExitCode.WARNING);
               context.getActionReport().setMessage("Obsolete options used.");
            }
        }

    private String generateHeartbeatPort() {
        final int MIN_GMS_MULTICAST_PORT = 2048;
        final int MAX_GMS_MULTICAST_PORT = 32000;

        int portInterval = MAX_GMS_MULTICAST_PORT - MIN_GMS_MULTICAST_PORT;
        return Integer.valueOf(Math.round((float)(Math.random() * portInterval)) + MIN_GMS_MULTICAST_PORT).toString();
    }

    private String generateHeartbeatAddress () {
            final int MAX_GMS_MULTICAST_ADDRESS_SUBRANGE = 255;

            final StringBuffer heartbeatAddressBfr = new StringBuffer( "228.9.");
            heartbeatAddressBfr.append(Math.round(Math.random()*MAX_GMS_MULTICAST_ADDRESS_SUBRANGE))
                            .append('.')
                            .append(Math.round(Math.random()*MAX_GMS_MULTICAST_ADDRESS_SUBRANGE));
            return heartbeatAddressBfr.toString();
        }
    }

    @Service
    @Scoped(PerLookup.class)
    class DeleteDecorator implements DeletionDecorator<Clusters, Cluster> {

        @Param(name="nodeagent", optional=true,obsolete=true)
        String nodeagent;

        // for backward compatibility, ignored.
        @Param(name="autohadboverride", optional=true,obsolete=true)
        String autohadboverride;

        @Inject
        private Domain domain;

        @Inject
        Configs configs;
        
        @Inject
        private ServerEnvironment env;

        @Override
        public void decorate(AdminCommandContext context, Clusters parent, Cluster child) throws
                PropertyVetoException, TransactionFailure{
            Logger logger = LogDomains.getLogger(Cluster.class, LogDomains.ADMIN_LOGGER);
            LocalStringManagerImpl localStrings = new LocalStringManagerImpl(Cluster.class);
            final ActionReport report = context.getActionReport();
            String instanceConfig = child.getConfigRef();
            final Config config = configs.getConfigByName(instanceConfig);
            Transaction t = Transaction.getTransaction(parent);

            //check if the cluster contains instances throw error that cluster
            //cannot be deleted
            //issue 12172
            List<ServerRef> serverRefs = child.getServerRef();
            StringBuffer namesOfServers = new StringBuffer();
            if (serverRefs.size() > 0) {
                for (ServerRef serverRef: serverRefs){
                    namesOfServers.append(new StringBuffer( serverRef.getRef()).append( ','));
                }

                final String msg = localStrings.getLocalString(
                        "Cluster.hasInstances",
                        "Cluster {0} contains server instances {1} and must not contain any instances"
                        ,child.getName() ,namesOfServers.toString()
                );

                logger.log(Level.SEVERE, msg);
                throw new TransactionFailure(msg);
            }

            // check if the config is null or still in use by some other
            // ReferenceContainer or is not <cluster-name>-config -- if so just return...
            if(config == null || domain.getReferenceContainersOf(config).size() > 1 || !instanceConfig.equals(child.getName() + "-config"))
                return;


            try {
                File configConfigDir = new File(env.getConfigDirPath(), config.getName());
                FileUtils.whack(configConfigDir);
            }
            catch(Exception e) {
                // no big deal - just ignore
            }

            try {
                if (t != null) {
                    Configs c = t.enroll(configs);
                    List<Config> configList = c.getConfig();
                    configList.remove(config);
                }
            } catch (TransactionFailure ex) {
                logger.log(Level.SEVERE,
                        localStrings.getLocalString("deleteConfigFailed",
                                "Unable to remove config {0}", instanceConfig), ex);
                String msg = ex.getMessage() != null ? ex.getMessage()
                        : localStrings.getLocalString("deleteConfigFailed",
                        "Unable to remove config {0}", instanceConfig);
                report.setMessage(msg);
                report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                report.setFailureCause(ex);
                throw ex;
            }
        }
    }
}
