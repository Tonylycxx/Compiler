FROM openjdk:11
WORKDIR /app
COPY .MyCompiler/src/* ./
RUN javac main.java