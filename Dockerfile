FROM openjdk:14
WORKDIR /app/
COPY folder ./MyCompiler/src ./
RUN javac .main.java