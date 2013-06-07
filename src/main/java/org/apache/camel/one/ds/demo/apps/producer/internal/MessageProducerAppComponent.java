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
package org.apache.camel.one.ds.demo.apps.producer.internal;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.camel.one.ds.demo.services.producer.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Add Class documentation for MessageProducerAppComponent
 *
 * @author sully6768
 */
@Component(name="org.apache.camel.one.ds.demo.apps.producer.internal")
public class MessageProducerAppComponent {

    private static final transient Logger LOG = LoggerFactory
            .getLogger(MessageProducerAppComponent.class);

    public static final String COMPONENT_LABEL = "DS Camel Demo Message Producer App Component";
    
    private ProducerService producerService;
    private ReadWriteLock rwl = new ReentrantReadWriteLock(true);
    private MessageProducerApp app;

    @Activate
    public synchronized void activate() throws Exception {
        LOG.info("Activating: " + COMPONENT_LABEL);

        try {
            rwl.writeLock().lock();
            app = new MessageProducerApp();
            app.setProducerService(producerService);
            app.setStarted(true);
            Thread t = new Thread(app);
            t.start();
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Deactivate
    public void deactivate() throws Exception {
        LOG.info("Deactivating: " + COMPONENT_LABEL);
        try {
            rwl.writeLock().lock();
            app.setStarted(false);
            app.setProducerService(null);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Reference
    public void setProducerService(ProducerService producerService, Map<String, ?> properties) {
        LOG.info("Binding ProducerService: " + producerService);
        try {
            rwl.writeLock().lock();
            this.producerService = producerService;
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public void unsetProducerService(ProducerService producerService, Map<?, ?> properties) {
        LOG.info("Unbinding ProducerService: " + producerService);
        try {
            rwl.writeLock().lock();
            this.producerService = null;
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
