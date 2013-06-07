package org.apache.camel.one.ds.demo.services.producer.provider;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.one.ds.demo.services.internal.AbstractJmsService;
import org.apache.camel.one.ds.demo.services.producer.ProducerService;


@Component(
           name="org.apache.camel.one.ds.demo.services.producer",
           servicefactory=true,
           configurationPolicy=ConfigurationPolicy.require)
public class ProducerServiceProvider extends AbstractJmsService implements ProducerService {

    public static final String COMPONENT_LABEL = "DS Camel Producer Service";

    private DefaultCamelContext camelContext;
    private ProducerTemplate producer;

    public void sendMessage(Object message) {

        try {
            rwl.readLock().lock();
            producer.sendBody(message);
        } catch (Exception e) {
            LOG.error("Exception Sending Message: " + message, e);
        } finally {
            rwl.readLock().unlock();
        }
    }

    public void sendMessage(Object message, Map<String, Object> headers) {

        try {
            rwl.readLock().lock();
            producer.sendBodyAndHeaders(message, headers);
        } catch (Exception e) {
            LOG.error("Exception Sending Message: " + message, e);
        } finally {
            rwl.readLock().unlock();
        }
    }
    
    @Activate
    public synchronized void activate(Map<?, ?> properties) throws Exception {
        LOG.info("Activating: " + COMPONENT_LABEL);

        try {
            rwl.writeLock().lock();        
            LOG.info(properties.toString());

            LOG.info("Activating the Producer Endpoint");
            producer = camelContext.createProducerTemplate();
            producer.setDefaultEndpointUri((String)properties.get("endpointUri"));
            producer.start();
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Deactivate
    public void deactivate(Map<?, ?> properties) throws Exception {
        LOG.info("Deactivating: " + COMPONENT_LABEL);
        try {
            rwl.writeLock().lock();
            producer.stop();
            producer = null;
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Reference
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        try {
            rwl.writeLock().lock();
            if (camelContext == null) {
                LOG.info("Creating the Camel Context");
                camelContext = new DefaultCamelContext();
                LOG.info("Activating the ActiveMQComponent");
                ActiveMQComponent amqc = getConnectionFactoryInstance(connectionFactory);
                camelContext.addComponent("activemq", amqc);
                LOG.info("Starting the Camel Context");
                camelContext.start();
            }
        } catch (Exception e) {
            LOG.error("Error starting up the Camel Context", e);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public void unsetConnectionFactory(ConnectionFactory connectionFactory) {
        try {
            rwl.writeLock().lock();
                LOG.info(" Yes, shut it down.");
                camelContext.stop();
                camelContext = null;
        } catch (Exception e) {
            // e.printStackTrace();
            LOG.error("Error shutting down the Camel Context", e);
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
