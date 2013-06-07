/**
 * 
 */
package org.apache.camel.one.ds.demo.services.producer;

import java.util.Map;


/**
 * @author sully6768
 */
public interface ProducerService {
    void sendMessage(Object message);
    void sendMessage(Object message, Map<String, Object> headers);
}
