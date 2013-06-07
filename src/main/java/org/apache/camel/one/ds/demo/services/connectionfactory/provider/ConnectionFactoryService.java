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
package org.apache.camel.one.ds.demo.services.connectionfactory.provider;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Add Class documentation for ConnectionFactoryService
 * 
 * @author <a href="mailto:sully6768@apache.org">Scott England-Sullivan</a>
 */
@Component(
           name="org.apache.camel.one.ds.demo.services.connectionfactory",
           servicefactory=false,
           configurationPolicy=ConfigurationPolicy.require)
public class ConnectionFactoryService extends PooledConnectionFactory implements ConnectionFactory {
    
    private static final transient Logger LOG = LoggerFactory.getLogger(ConnectionFactoryService.class);
    
    @Activate
    public void activate(Map<?, ?> properties) {
        String brokerUrl = (String)properties.get("brokerUrl");
        String username = (String)properties.get("username");
        String password = (String)properties.get("password");
        ActiveMQConnectionFactory acf = new ActiveMQConnectionFactory(username, password, brokerUrl);
        this.setConnectionFactory(acf);
        this.setMaxConnections(2);
        this.setMaximumActiveSessionPerConnection(200);
        this.start();
    }

    @Deactivate
    public void deactivate(Map<?, ?> properties) {
        this.stop();
    }

    @Reference(target = "(service.pid=org.apache.activemq.server)")
    public void setConnectionFactory(ManagedServiceFactory managedServiceFactory) {
        LOG.info("Managed Service found for ActiveMQ, Activating Component");
    }

    public void unsetConnectionFactory(ConnectionFactory connectionFactory) {
        LOG.info("Managed Service removed for ActiveMQ, Deactivating Component");
    }
}
