FROM openjdk:14
WORKDIR /app/
COPY ./MyCompiler/* ./
RUN javac ./src/main.java