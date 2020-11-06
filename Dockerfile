FROM jdk:11.0.1
WORKDIR /app/
COPY ./* ./
RUN javac -d ./output ./src/OPG.java