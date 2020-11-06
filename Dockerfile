FROM jdk:11.0.1
WORKDIR /app/
COPY ./src/* ./
RUN javac OPG.java