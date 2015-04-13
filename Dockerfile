FROM zalando/openjdk:8u40-b09-4

MAINTAINER Zalando SE

COPY target/swagger-mock.jar /

EXPOSE 8181

COPY entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
