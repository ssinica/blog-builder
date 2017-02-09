FROM maven:3-jdk-8

RUN mkdir -p /source /result /config
VOLUME /source /result /config
EXPOSE 8080

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD . /usr/src/app
RUN mvn install

CMD ["java", "-classpath", "/usr/src/app/target/blog-builder-1.0-SNAPSHOT.jar:/usr/src/app/target/lib/*", "com.synitex.blogbuilder.BlogApp", "--spring.config.location=classpath:/config/application.properties,file:/config/blog.properties"]