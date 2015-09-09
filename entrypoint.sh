#!/bin/sh

file=/swagger/$1

if [ ! -f $file ]; then
	echo "Usage:  docker run -it --rm -p <port>:8181 -v <swagger-directory>:/swagger zalando/swagger-mock <swagger-file>" >&2
	echo >&2
	echo "Example:" >&2
	echo "    docker run -it --rm -p 8181:8181 -v \$PWD:/swagger zalando/swagger-mock myapp.yaml" >&2
	exit 1
fi

exec java -jar /swagger-mock.jar $file
