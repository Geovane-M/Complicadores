#include <stdio.h>
#include <string>

int main (){
    int i;
    float mediaMiniTotal = 35, nota1, nota2;
    for(i = 0; i <= 10; i = i + 1){
            nota1 = 2 * i;
            nota2 = (mediaMiniTotal - nota1) / 3;
            printf("_________________\n");
            printf("1º GQ  |   2º GQ ");
            printf("\n%i%s|   %.2f", i, i > 9? "     " : "      ", nota2);
            printf(i < 3? " REPROVADO!\n": "\n");
    }
    printf("_________________");
    return 0;
}

