# keycloak-spring-boot-demo
Integration of spring boot application with keycloak using spring security

## Start the server with Jetty3 + Spring boot 1

```mvn spring-boot:run -P jetty -Dserver.port=8899```

## Start the server with Jetty4 + Spring boot 2

Update parent version

Comment out ```<version>1.5.15.RELEASE</version>```

Uncomment ```<!--<version>2.1.9.RELEASE</version>-->```
        
```mvn spring-boot:run -P jetty -Dserver.port=8899```

## Start the server with Tomcat

```mvn spring-boot:run -P tomcat -Dserver.port=8899```
