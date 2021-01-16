FROM openjdk:14
WORKDIR /app/
COPY ./MyCompiler/src/* ./
RUN javac main.java