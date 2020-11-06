FROM openjdk:11
WORKDIR /app/
COPY ./src/* ./
RUN javac OPG.java