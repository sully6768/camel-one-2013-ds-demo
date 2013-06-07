/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.one.ds.demo.services.consumer.provider;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.osgi.service.component.ComponentContext;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.one.ds.demo.apps.consumer.AppConsumer;
import org.apache.camel.one.ds.demo.services.consumer.ConsumerService;
import org.apache.camel.one.ds.demo.services.internal.AbstractJmsService;

/**
 * TODO Add Class documentation for ConsumerServiceProvider
 * 
 * @author sully6768
 */
@Component(
           name = "org.apache.camel.one.ds.demo.services.consumer",
           configurationPolicy=ConfigurationPolicy.require)
public class ConsumerServiceProvider extends AbstractJmsService implements ConsumerService {

    public static final String COMPONENT_LABEL = "DS Camel Consumer Component";

    private DefaultCamelContext camelContext;
    private ConnectionFactory connectionFactory;
    private Map<AppConsumer, RouteBuilder> consumers = new HashMap<AppConsumer, RouteBuilder>();
    private String endpointUri;

    public void registerAppConsumer(final AppConsumer appConsumer) {
        try {
            rwl.writeLock().lock();
            
            LOG.info("Building RoutesBuilder");
            RouteBuilder route = new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(endpointUri).bean(appConsumer, "handleMessage");
                }
            };
            LOG.info("Binding RoutesBuilder ID to the Camel Context");
            consumers.put(appConsumer, route);
            route.addRoutesToCamelContext(camelContext);

        } catch (Exception e) {
            LOG.error("TODO Auto-generated catch block", e);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public void unregisterAppConsumer(AppConsumer appConsumer) {

        LOG.info("Unbinding AppConsumer");
        RouteBuilder route = null;
        try {
            rwl.writeLock().lock();
            route = consumers.remove(appConsumer);
            if(camelContext != null) {
                List<RouteDefinition> routes = route.getRouteCollection().getRoutes();
                for (RouteDefinition definition : routes) {
                    LOG.info("Removing Route: " + definition.getId());
                    camelContext.stopRoute(definition);
                    camelContext.removeRouteDefinition(definition);
                }
            }

        } catch (Exception e) {
            LOG.error("Error removing route: " + route, e);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Activate
    public void activate(Map<?, ?> properties) throws Exception {
        
        LOG.info("Activating: " + COMPONENT_LABEL);
        endpointUri = (String)properties.get("endpointUri");
        
        try {
            rwl.writeLock().lock();

            LOG.info("Creating the Camel Context");
            camelContext = new DefaultCamelContext();
            LOG.info("Activating the ActiveMQComponent");
            ActiveMQComponent amqc = getConnectionFactoryInstance(connectionFactory);
            camelContext.addComponent("activemq", amqc);
            LOG.info("Starting the Camel Context");
            camelContext.start();
        } catch (Exception e) {
            LOG.error("Error starting up the Camel Context", e);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Deactivate
    public void deactivate() throws Exception {
        LOG.info("Deactivating: " + COMPONENT_LABEL);
        try {
            rwl.writeLock().lock();
            camelContext.stop();
            camelContext = null;
        } catch (Exception e) {
            // e.printStackTrace();
            LOG.error("Error shutting down the Camel Context", e);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Reference
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        try {
            rwl.writeLock().lock();
            this.connectionFactory = connectionFactory;
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public void unsetConnectionFactory(ConnectionFactory connectionFactory, ComponentContext componentContext) {
        try {
            rwl.writeLock().lock();
            this.connectionFactory = null;
        } finally {
            rwl.writeLock().unlock();
        }
    }


}
