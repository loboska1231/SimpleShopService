FROM openjdk:17-alpine

MAINTAINER borys

RUN apk add bash

RUN mkdir /app

WORKDIR /app