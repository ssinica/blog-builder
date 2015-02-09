FROM ubuntu:14.04
MAINTAINER Sergey Sinica <sergey.sinica@gmail.com>

RUN groupadd -r blog && useradd -r -g blog blog

RUN mkdir -p /home/blog && chown -R blog:blog /home/blog

RUN apt-get update && apt-get install -y \
   openjdk-7-jre \
   curl \
   wget \
   nano

ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64

# grab gosu for easy step-down from root
RUN gpg --keyserver pgp.mit.edu --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4
RUN curl -o /usr/local/bin/gosu -SL "https://github.com/tianon/gosu/releases/download/1.2/gosu-$(dpkg --print-architecture)" \
	&& curl -o /usr/local/bin/gosu.asc -SL "https://github.com/tianon/gosu/releases/download/1.2/gosu-$(dpkg --print-architecture).asc" \
	&& gpg --verify /usr/local/bin/gosu.asc \
	&& rm /usr/local/bin/gosu.asc \
	&& chmod +x /usr/local/bin/gosu

EXPOSE 8080

RUN    mkdir -p /app/config \
    && mkdir -p /app/in \
    && mkdir -p /app/out \
    && mkdir -p /app/lib

VOLUME /app/config
VOLUME /app/in
VOLUME /app/out
VOLUME /app/lib

COPY docker-entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
