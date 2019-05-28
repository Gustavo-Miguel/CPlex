package po;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author a14013
 */
public class sudoku {

    public static void main(String[] args) throws IOException, FileNotFoundException, IloException {
        new sudoku();
    
    }

    public sudoku() throws FileNotFoundException, IOException, IloException {
        //INÍCIO CÓDIGO CPLEX
        IloCplex modelo = new IloCplex();
        
        //variaveis de decisão
        IloIntVar x[][][] = new IloIntVar[9][9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                   x[i][j][k] = modelo.boolVar();
                }
            }
        }

        //Leitura do arquivo
        BufferedReader br = new BufferedReader(new FileReader("instancia_s1.csv"));
        //String numero = br.readLine();
        String numero;
        String s[];
        for (int i = 0; i < 9; i++) {
            numero = br.readLine();
            for (int j = 0; j < 9; j++) {                
                s = numero.split(" ");
                if(Integer.valueOf(s[j]) != 0){
                    for (int k = 9; k < 9; k++) {
                        x[i][j][k] = modelo.boolVar(s[j]);
                    }                    
                }
            }
        }        
        
        //funcao objetivo
        //IloLinearNumExpr fo = modelo.linearNumExpr();
        IloLinearIntExpr fo = modelo.linearIntExpr();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    fo.addTerm(1, x[i][j][k]);
                }
            }
        }
        modelo.addEq(fo, 81);
                
        //restricao linhas
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                IloLinearNumExpr restricaoLinha = modelo.linearNumExpr();
                for (int k = 0; k < 9; k++) {
                    restricaoLinha.addTerm(1, x[i][j][k]);
                }
                modelo.eq(restricaoLinha, 1);
            }        
        }
        
        //restricao colunas
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                IloLinearNumExpr restricaoColuna = modelo.linearNumExpr();
                for (int k = 0; k < 9; k++) {
                    restricaoColuna.addTerm(1, x[i][j][k]);
                }
                modelo.eq(restricaoColuna, 1);
            }        
        }
        
        //restricao quadrantes        
        for (int lin = 0; lin < 3; lin++) {
            for (int col = 0; col < 3; col++) {
                for (int v = 0; v < 9; v++) {
                    IloLinearNumExpr restricaoQuad = modelo.linearNumExpr();

                    for (int i = 0; i < 3; i++) {                                       
                        for (int j = 0; j < 3; j++) {
                            restricaoQuad.addTerm(1, x[lin+i][col+j][v]);
                        }
                    }
                    modelo.eq(restricaoQuad, 1);
                }
            }
        }
                
        modelo.solve();
        
        //Escrevendo os resultados
        for (int i = 0; i < 9; i++) {            
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    System.out.println(modelo.getValue(x[i][j][k])+" ");                
                }
            }
            System.out.println("\n");
        }
    }
}
