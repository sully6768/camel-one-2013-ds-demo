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
package org.apache.camel.one.ds.demo.services.internal;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.jms.ConnectionFactory;

import org.apache.camel.component.sjms.SjmsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Add Class documentation for AbstractJmsService
 *
 * @author sully6768
 */
public class AbstractJmsService {

    protected transient Logger LOG = LoggerFactory.getLogger(getClass());
    
    protected ReadWriteLock rwl = new ReentrantReadWriteLock(true);

    protected SjmsComponent getConnectionFactoryInstance(ConnectionFactory connectionFactory) {
        SjmsComponent sjms = new SjmsComponent();
        sjms.setConnectionFactory(connectionFactory);
        return sjms;
    }

}
