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
package org.apache.camel.one.ds.demo.apps.consumer.internal;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.camel.one.ds.demo.apps.consumer.AppConsumer;
import org.apache.camel.one.ds.demo.services.consumer.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Add Class documentation for AppConsumerProvider
 *
 * @author sully6768
 */
@Component(
           name="org.apache.camel.one.ds.demo.apps.consumer",
           provide={})
public class AppConsumerProvider implements AppConsumer {

    private static final transient Logger LOG = LoggerFactory
            .getLogger(AppConsumerProvider.class);

    public static final String COMPONENT_LABEL = "DS Camel Consumer App";
    
    private ConsumerService consumerService;
    private ReadWriteLock rwl = new ReentrantReadWriteLock(true);
    
    public void handleMessage(Object message) {
        LOG.info("Message Received: " + message);
    }
    
    @Activate
    public synchronized void activate() throws Exception {
        LOG.info("Activating: " + COMPONENT_LABEL);
        try {
            rwl.writeLock().lock();
            consumerService.registerAppConsumer(this);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Deactivate
    public void deactivate() throws Exception {
        LOG.info("Deactivating: " + COMPONENT_LABEL);
        try {
            rwl.writeLock().lock();
            consumerService.unregisterAppConsumer(this);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Reference
    public void setProducerService(ConsumerService consumerService) {
        LOG.info("Binding ProducerService: " + consumerService);
        try {
            rwl.writeLock().lock();
            this.consumerService = consumerService;
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public void unsetProducerService(ConsumerService consumerService) {
        LOG.info("Unbinding ProducerService: " + consumerService);
        try {
            rwl.writeLock().lock();
            this.consumerService = null;
        } finally {
            rwl.writeLock().unlock();
        }
    }

}
