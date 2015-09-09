FROM zalando/openjdk:8u45-b14-6

MAINTAINER Zalando SE

COPY target/swagger-mock.jar /

EXPOSE 8181

COPY entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
