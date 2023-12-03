# mysql.Dockerfile for backend app
FROM maven:3.9.5-eclipse-temurin-17

WORKDIR /app

COPY . /app

RUN chmod -R 7777 /app

RUN chown -R ${UID:=1000750000} /app

EXPOSE 9080

RUN chmod -R 7777 /root

RUN chown -R ${UID:=1000750000} /root

RUN mkdir /.m2

RUN chmod -R 7777 /.m2

RUN chown -R ${UID:=1000750000} /.m2

RUN mkdir /.m2/repository

RUN chmod -R 7777 /.m2/repository

RUN chown -R ${UID:=1000750000} /.m2/repository

CMD ["mvn", "clean", "liberty:run"]