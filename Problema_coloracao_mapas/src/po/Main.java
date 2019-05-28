package po;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        new Main();
    }

    public Main() throws FileNotFoundException, IOException {
        try {
            //int qtdLinhasArquivo = 4938;//instancia completa
            //int qtdPaises = 999;//(1,...,999)//instancia completa / teste
            //int qtdCores = 999;//instancia completa / teste
            //int qtdLinhasArquivo = 77;//instancia teste
            //int qtdLinhasArquivo = 17;//instancia 10
            //int qtdPaises = 9;//instancia 10
            //int qtdCores = 9;//instancia 10
            //int qtdLinhasArquivo = 282;//instancia 50
            //int qtdPaises = 49;//instancia 50
            //int qtdCores = 49;//instancia 50
            int qtdLinhasArquivo = 494;//instancia 100
            int qtdPaises = 99;//instancia 100
            int qtdCores = 99;//instancia 100

            //INÍCIO CÓDIGO CPLEX
            IloCplex modelo = new IloCplex();

            //variaveis de decisao
            IloIntVar x[][] = new IloIntVar[qtdPaises][qtdCores];
            for (int i = 0; i < qtdPaises; i++) {
                for (int j = 0; j < qtdCores; j++) {
                    x[i][j] = modelo.boolVar();
                }
            }
            
            //Função Objetivo
            IloLinearNumExpr fo = modelo.linearNumExpr();
            for (int i = 0; i < qtdPaises; i++) {
                for (int j = 0; j < qtdCores; j++) {
                    fo.addTerm(j+1, x[i][j]);
                }
            }

            modelo.addMinimize(fo);
            
            //Restrição "cada país deve ser colorido com apenas uma cor"
            for (int i = 0; i < qtdPaises; i++) {
                IloLinearNumExpr expr = modelo.linearNumExpr();
                for (int j = 0; j < qtdCores; j++) {
                    expr.addTerm(1, x[i][j]);
                }
                modelo.addEq(expr, 1);
            }
            
            //Restrição "países vizinhos devem ter cores diferentes"
            //Leitura dos arquivos
            //BufferedReader br = new BufferedReader(new FileReader("instancia.csv"));
            //BufferedReader br = new BufferedReader(new FileReader("instancia_teste.csv"));
            //BufferedReader br = new BufferedReader(new FileReader("instancia10.csv"));
            //BufferedReader br = new BufferedReader(new FileReader("instancia50.csv"));
            BufferedReader br = new BufferedReader(new FileReader("instancia100.csv"));
            String linha = br.readLine();
            String s[];
            int pais1;
            int pais2;
            for (int linhaArq = 2; linhaArq <= qtdLinhasArquivo; linhaArq++) {
                linha = br.readLine();
                s = linha.split(",");
                pais1 = Integer.parseInt(s[0]) - 1;
                pais2 = Integer.parseInt(s[1]) - 1;
                for (int j = 0; j < qtdCores; j++) {
                    IloLinearNumExpr expr = modelo.linearNumExpr();
                    expr.addTerm(1, x[pais1][j]);
                    expr.addTerm(1, x[pais2][j]);
                    modelo.addLe(expr, 1);
                }
            }

            modelo.solve();

            //Print x[][]
//            System.out.println("");
//            for (int i = 0; i < qtdPaises; i++) {
//                for (int j = 0; j < qtdCores; j++) {
//                    System.out.print(modelo.getValue(x[i][j]) + " ");
//                }
//                System.out.println("");
//            }
            
            //Print resultado final
            //System.out.println("\nValor min: " + modelo.getObjValue());
            int y[] = new int[qtdCores];
            for (int i = 0; i < qtdPaises; i++) {
                for (int j = 0; j < qtdCores; j++) {
                    if (modelo.getValue(x[i][j]) == 1) {
                        y[j] = 1;
                    }
                }
            }
            int totalCores = 0;
            for (int j = 0; j < qtdCores; j++) {
                if (y[j] == 1) {
                    totalCores++;
                }
            }
            System.out.println("\nValor min: " + totalCores);
        } catch (IloException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
