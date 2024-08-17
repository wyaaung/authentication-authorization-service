FROM docker.io/eclipse-temurin:21.0.3_9-jdk-alpine

RUN apk add --no-cache tzdata curl

ENV TZ=Europe/London
RUN ln -s -f ../usr/share/zoneinfo/$TZ /etc/localtime

WORKDIR /opt/rbac

EXPOSE 8080

ENTRYPOINT ["java", "-classpath", "lib/*:resources", "com.wyaaung.rbac.RbacApplication"]

ADD ./build/distributions/rbac.tar /opt/
RUN rm -rf /opt/rbac/bin
