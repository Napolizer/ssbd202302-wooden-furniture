FROM mysql:8.0

WORKDIR /docker-entrypoint-initdb.d

COPY ./src/main/resources/init-users-mysql.sql /docker-entrypoint-initdb.d