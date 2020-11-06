FROM jdk:11.0.1
WORKDIR /app/
COPY ./* ./
RUN javac OPG.java