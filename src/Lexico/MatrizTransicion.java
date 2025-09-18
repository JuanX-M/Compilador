package Lexico;

public class MatrizTransicion {





    public Integer convertir(char c){
        switch (c) {
            case ' ' : return 0;
            case '\t' : return 1;
            case '\n' : return 2;
            case 'a', 'b', 'c', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x',
                 'y', 'z' : return 3;
            case 'A', 'B', 'C', 'E', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
                 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' : return 4;
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' : return 5;
            case 'I' : return 6;
            case 'F' : return 7;
            case '%' : return 8;
            case '"' : return 9;
            case '#' : return 10;
            case '+' : return 11;
            case '-' : return 12;
            case '*' : return 13;
            case '/' : return 14;
            case '=' : return 15;
            case ':' : return 16;
            case '>' : return 17;
            case '<' : return 18;
            case '!' : return 19;
            case '(' : return 20;
            case ')' : return 21;
            case '{' : return 22;
            case '}' : return 23;
            case '_' : return 24;
            case ';' : return 25;
            default : return 26;
        }
    }












}
