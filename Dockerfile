FROM gcc:10
WORKDIR /app/
COPY ./* ./
RUN gcc Lexer-1.c -o Lexer-1
RUN chmod +x Lexer-1