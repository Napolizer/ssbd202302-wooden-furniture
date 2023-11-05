# building stage
FROM node:latest as builder

WORKDIR /app

COPY src/main/ssbd202302-frontend .

RUN npm i && npm run build:docker

# nginx stage
FROM nginx:alpine as  runtime

WORKDIR /usr/share/nginx/html

RUN rm -rf ./*

COPY --from=builder /app/dist/ssbd202302-frontend .

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

