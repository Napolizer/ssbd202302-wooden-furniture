# Dockerfile for backend app
FROM maven:3.9.5-amazoncorretto-17

WORKDIR /app

COPY . /app

EXPOSE 9080

CMD ["mvn", "clean", "liberty:run"]