#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define Reserved_num 20
#define MAX_LENGTH 200

union identifier {
    char *id_val;
    long long int_val;
};

char *token;
long long num;
char *symbol;
char curChar;
FILE *fp;
const char *Reserved_words[Reserved_num] = {"BEGIN", "END",  "For",
                                            "IF",    "THEN", "ELSE"};
const char *Reserved_words_print[Reserved_num] = {"Begin", "End",  "For",
                                                  "If",    "Then", "Else"};

// const char Reserved_sops[Reserved_num] = {'+', '*'};
// const char *Reserved_sops_print[Reserved_num] = {"Plus", "Star"};

// const char Reserved_end[Reserved_num] = {':', ',', '(', ')'};
// const char *Reserved_end_print[Reserved_num] = {"Colon", "Comma",
//                                                 "LParenthesis",
//                                                 "RParenthesis"};

// const char *Reserved_dops[Reserved_num] = {":="};
// const char *Reserved_dops_print[Reserved_num] = {"Assign"};

int isSpace(char curChar) { return curChar == ' ' ? 1 : 0; }

int isNewLine(char curChar) {
    if (curChar == '\n' || curChar == '\r')
        return 1;
    return 0;
}

int isTab(char curChar) { return curChar == '\t' ? 1 : 0; }

int isLetter(char curChar) {
    if (('A' <= curChar && 'Z' >= curChar) ||
        ('a' <= curChar && 'z' >= curChar))
        return 1;
    return 0;
}

int isDigit(char curChar) {
    if ('0' <= curChar && '9' >= curChar)
        return 1;
    return 0;
}

void catToken() {
    char *newToken = &curChar;
    if (!token) {
        token = (char *)malloc(sizeof(char) * strlen(newToken));
        memset(token, 0, sizeof(char) * strlen(token));
        strcat(token, newToken);
        return;
    }
    char *temp = token;
    token = (char *)malloc(sizeof(char) * (strlen(token) + strlen(newToken)));
    memset(token, 0, sizeof(char) * strlen(token));
    strcat(token, temp);
    strcat(token, newToken);
    free(temp);
    temp = NULL;
    return;
}

int reservedOrNot() {
    int i;
    for (i = 0; Reserved_words[i]; i++) {
        if (!strcmp(Reserved_words[i], token)) {
            printf("%s\n", Reserved_words_print[i]);
            return 1;
        }
    }
    return 0;
}

void getChar() {
    curChar = fgetc(fp);
    return;
}

void retChar() {
    fseek(fp, -1, SEEK_CUR);
    return;
}

int isColon(char curChar) { return curChar == ':' ? 1 : 0; }
int isEqu(char curChar) { return curChar == '=' ? 1 : 0; }
int isPlus(char curChar) { return curChar == '+' ? 1 : 0; }
int isStar(char curChar) { return curChar == '*' ? 1 : 0; }
int isComma(char curChar) { return curChar == ',' ? 1 : 0; }
int isLP(char curChar) { return curChar == '(' ? 1 : 0; }
int isRP(char curChar) { return curChar == ')' ? 1 : 0; }

int main(int argc, char *argv[]) {
    if ((fp = fopen(argv[1], "r")) == NULL)
        printf("Counld not open this file! error!\n");
    while (!feof(fp)) {
        curChar = fgetc(fp);
        if (isSpace(curChar) || isNewLine(curChar) || isTab(curChar))
            continue;
        if (isLetter(curChar)) {
            while (isLetter(curChar) || isDigit(curChar)) {
                catToken();
                getChar();
            }
            retChar();
            if (!reservedOrNot())
                printf("Ident(%s)\n", token);
            memset(token, 0, sizeof(char) * strlen(token));
            continue;
        }
        if (isDigit(curChar)) {
            while (isDigit(curChar)) {
                catToken();
                getChar();
            }
            retChar();
            num = atoi(token);
            printf("Int(%lld)\n", num);
            num = 0;
            memset(token, 0, sizeof(char) * strlen(token));
            continue;
        } else if (isColon(curChar)) {
            getChar();
            if (isEqu(curChar))
                printf("Assign\n");
            else {
                retChar();
                printf("Colon\n");
            }
        } else if (isPlus(curChar))
            printf("Plus\n");
        else if (isStar(curChar))
            printf("Star\n");
        else if (isComma(curChar))
            printf("Comma\n");
        else if (isLP(curChar))
            printf("LParenthesis\n");
        else if (isRP(curChar))
            printf("RParenthesis\n");
        // else
        // {
        //     printf("Unknown\n");
        //     break;
        // }
    }
    fclose(fp);
    return 0;
}
