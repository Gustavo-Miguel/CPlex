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
            int qtdLinhasArquivo = 25174;
            int qtdCriancas = 1000;
            int qtdPresentes = 100;
            double weight[][] = new double[qtdCriancas][qtdPresentes];

            //Leitura dos arquivos
            BufferedReader br = new BufferedReader(new FileReader("instanciaCorrigida.csv"));
            String linha = br.readLine();
            String s[];
            for (int i = 2; i <= qtdLinhasArquivo; i++) {
                linha = br.readLine();
                s = linha.split(",");
                weight[Integer.parseInt(s[0])][Integer.parseInt(s[1])] = Double.parseDouble(s[2]);
            }

            //Print weight[][]
//            for (int i = 0; i < qtdCriancas; i++) {
//                for (int j = 0; j < qtdPresentes; j++) {
//                    System.out.print(weight[i][j] + " ");
//                }
//                System.out.println("");
//            }            

            //INÍCIO CÓDIGO CPLEX
            IloCplex modelo = new IloCplex();

            //variaveis de decisao
            IloIntVar x[][] = new IloIntVar[qtdCriancas][qtdPresentes];
            for (int i = 0; i < qtdCriancas; i++) {
                for (int j = 0; j < qtdPresentes; j++) {
                    x[i][j] = modelo.boolVar();
                }
            }

            //Função Objetivo
            IloLinearNumExpr fo = modelo.linearNumExpr();
            for (int i = 0; i < qtdCriancas; i++) {
                for (int j = 0; j < qtdPresentes; j++) {
                    fo.addTerm(weight[i][j], x[i][j]);
                }
            }

            modelo.addMaximize(fo);

            //Restrição "cada criança deve receber apenas 1 presente"
            for (int i = 0; i < qtdCriancas; i++) {
                IloLinearNumExpr expr = modelo.linearNumExpr();
                for (int j = 0; j < qtdPresentes; j++) {
                    expr.addTerm(1, x[i][j]);
                }
                modelo.addEq(expr, 1);
            }
            
            //Restrição "cada presente deve ser distribuido 10 vezes"
            for (int j = 0; j < qtdPresentes; j++) {
                IloLinearNumExpr expr = modelo.linearNumExpr();
                for (int i = 0; i < qtdCriancas; i++) {
                    expr.addTerm(1, x[i][j]);
                }
                modelo.addEq(expr, 10);
            }
            
            //Restrição "as 10 primeiras criancas são gẽmeas (de 2 em 2, um par de gêmeos); Gêmeos devem receber o mesmo presente"
            for (int i = 0; i < 9; i += 2) {
                for (int j = 0; j < qtdPresentes; j++) {
                    IloLinearNumExpr irmao1 = modelo.linearNumExpr();
                    irmao1.addTerm(1, x[i][j]);
                    IloLinearNumExpr irmao2 = modelo.linearNumExpr();
                    irmao2.addTerm(1, x[i+1][j]);
                    modelo.addEq(irmao1, irmao2);
                }
            }
            
            //Restrição "da 11a até a 22a são trigêmeas (de 3 em 3, um trio de trigêmeos); trigêmeos devem receber o mesmo presente"
            for (int i = 10; i < 20; i += 3) {
                for (int j = 0; j < qtdPresentes; j++) {
                    IloLinearNumExpr irmao1 = modelo.linearNumExpr();
                    irmao1.addTerm(1, x[i][j]);
                    IloLinearNumExpr irmao2 = modelo.linearNumExpr();
                    irmao2.addTerm(1, x[i+1][j]);
                    IloLinearNumExpr irmao3 = modelo.linearNumExpr();
                    irmao3.addTerm(1, x[i+2][j]);
                    modelo.addEq(irmao1, irmao2);
                    modelo.addEq(irmao2, irmao3);
                }
            }

            modelo.solve();

            //Print x[][]
//            System.out.println("");
//            for (int i = 0; i < qtdCriancas; i++) {
//                for (int j = 0; j < qtdPresentes; j++) {
//                    System.out.print(modelo.getValue(x[i][j]) + " ");
//                }
//                System.out.println("");
//            }
            
            //Print criança - presente
            for (int i = 0; i < qtdCriancas; i++) {
                for (int j = 0; j < qtdPresentes; j++) {
                    if (modelo.getValue(x[i][j]) == 1) {
                        System.out.println("Criança " + i + " - Presente " + j);
                    }
                }
            }
            
            //Print qtd distribuida de cada presente
//            for (int j = 0; j < qtdPresentes; j++) {
//                int total = 0;
//                for (int i = 0; i < qtdCriancas; i++) {
//                    if (modelo.getValue(x[i][j]) == 1) {
//                        total++;
//                    }
//                }
//                System.out.println("Presente " + j + ": " + total + " un");
//            }

            //Print resultado final
            System.out.println("\nValor máx: " + modelo.getObjValue());
        } catch (IloException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
