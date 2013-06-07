camel-one-2013-ds-demo
======================

Using Karaf 2.3.2-SNAPSHOT

Add the following components in the order displayed:

features:install scr
install mvn:org.apache.karaf.scr/org.apache.karaf.scr.examples.service/2.3.1
features:chooseurl camel 2.11.0
features:chooseurl activemq 5.8.0
features:install activemq-broker
features:install camel-core
features:install camel-jms
install mvn:org.apache.camel/camel-sjms/2.11.0
webconsole-scr


Purge any configurations:
------------------------
config:delete org.apache.camel.one.ds.demo.services.consumer
config:delete org.apache.camel.one.ds.demo.services.producer
config:delete org.apache.camel.one.ds.demo.services.connectionfactory

Add the ConnectionFactory Service Config 
------------------------ 
config:edit org.apache.camel.one.ds.demo.services.connectionfactory
config:propset brokerUrl tcp://localhost:61616
config:propset username karaf
config:propset password karaf
config:update

Add the Consumer Service Config 
------------------------ 
config:edit org.apache.camel.one.ds.demo.services.consumer
config:propset endpointUri sjms:queue:camelone.demo.test.queue
config:update

Add the Producer Service Config 
------------------------ 
config:edit org.apache.camel.one.ds.demo.services.producer
config:propset endpointUri sjms:queue:camelone.demo.test.queue
config:update


