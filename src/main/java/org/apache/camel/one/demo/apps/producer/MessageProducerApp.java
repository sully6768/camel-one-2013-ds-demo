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
package org.apache.camel.one.demo.apps.producer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.one.demo.services.producer.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class MessageProducerApp implements Runnable {
    private static final transient Logger LOG = LoggerFactory.getLogger(MessageProducerApp.class);

    private ProducerService producerService;
    private boolean started = true;

    public void setProducerService(ProducerService producerService) {
        this.producerService = producerService;
    }

    public ProducerService getProducerService() {
        return producerService;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
    
    public boolean isStarted() {
        return started;
    }

    public void run() {
//        int counter = 0;
        while (isStarted()) {
//            if(!isStarted()) {
//                break;
//            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String message = "Hello Scott: " + sdf.format(new Date());
            LOG.info("Sending message: " + message);
            getProducerService().sendMessage(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.warn("Problem sleeping");
            }
//            if(counter++ == 50) {
//                break;
//            }
        }

    }

}
