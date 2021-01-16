FROM openjdk:10
WORKDIR /app/
COPY ./MyCompiler/src/* ./
RUN javac main.java