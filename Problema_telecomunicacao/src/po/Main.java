package po;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
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
    
    public Main() throws FileNotFoundException, IOException{
        try {
            int qtdAntenas = 99;
            int qtdConsumidores = 199;
            double rf[] = new double[qtdAntenas];//raio de cobertura de f
            double xf[] = new double[qtdAntenas];//posição longitudinal de f
            double yf[] = new double[qtdAntenas];//posição latitudinal de f
            double lf[] = new double[qtdAntenas];//limite de capacidade da largura de banda de f
            double cf[] = new double[qtdAntenas];//custo de instalaçao de f
            double dc[] = new double[qtdConsumidores];//demanda por largura de banda de c
            double xc[] = new double[qtdConsumidores];//posição longitudinal de c
            double yc[] = new double[qtdConsumidores];//posição latitudinal de c
            
            //Leitura dos arquivos
            BufferedReader br = new BufferedReader(new FileReader("new_fornecedores3.csv"));
            //BufferedReader br = new BufferedReader(new FileReader("fornecedores3.csv"));
            String linha = br.readLine();
            String s[];
            for (int i = 0; i < qtdAntenas; i++) {
                linha = br.readLine();
                s = linha.split(",");
                rf[i] = Double.parseDouble(s[0]);
                xf[i] = Double.parseDouble(s[1]);
                yf[i] = Double.parseDouble(s[2]);
                lf[i] = Double.parseDouble(s[3]);
                cf[i] = Double.parseDouble(s[4]);
            }
            br.close();
            
            br = new BufferedReader(new FileReader("consumidores3.csv"));
            linha = br.readLine();
            for (int i = 0; i < qtdConsumidores; i++) {
                linha = br.readLine();
                s = linha.split(",");
                dc[i] = Double.parseDouble(s[0]);
                xc[i] = Double.parseDouble(s[1]);
                yc[i] = Double.parseDouble(s[2]);
            }
            br.close();



            //INÍCIO CÓDIGO CPLEX
            IloCplex modelo = new IloCplex();
            
            //variaveis de decisao
            IloIntVar x[] = new IloIntVar[qtdAntenas];
            for (int i = 0; i < qtdAntenas; i++) {
                x[i] = modelo.boolVar();
            }
            
            double distanciaEuclidiana;
            int a[][] = new int[qtdAntenas][qtdConsumidores];
            for (int i = 0; i < qtdAntenas; i++) {
                for (int j = 0; j < qtdConsumidores; j++) {
                    distanciaEuclidiana = Math.sqrt(Math.pow((xc[j] - xf[i]), 2) + (Math.pow((yc[j] - yf[i]), 2)));
                    if (distanciaEuclidiana > rf[i]) {
                        a[i][j] = 0;
                    } else {
                        a[i][j] = 1;
                    }
                }
            }
            
            IloNumVar y[][] = new IloNumVar[qtdAntenas][qtdConsumidores];
            for (int i = 0; i < qtdAntenas; i++) {
                for (int j = 0; j < qtdConsumidores; j++) {
                    y[i][j] = modelo.numVar(0, 999999999);
                }
            }

            //Relacionando y[i][j] com x[i]
            double m = 999999999;
            for (int i = 0; i < qtdAntenas; i++) {
                IloLinearNumExpr dir = modelo.linearNumExpr();
                IloLinearNumExpr esq = modelo.linearNumExpr();
                for (int j = 0; j < qtdConsumidores; j++) {
                    dir.addTerm(1, y[i][j]);
                }
                esq.addTerm(m, x[i]);
                modelo.addGe(esq, dir);
            }
            
            //Função Objetivo
            IloLinearNumExpr fo = modelo.linearNumExpr();
            for (int i = 0; i < qtdAntenas; i++) {
                fo.addTerm(cf[i], x[i]);
            }

            modelo.addMinimize(fo);
            
            //Restrição de capacidade de largura de banda
            for (int i = 0; i < qtdAntenas; i++) {
                IloLinearNumExpr restricaoLarguraBanda = modelo.linearNumExpr();
                for (int j = 0; j < qtdConsumidores; j++) {
                    restricaoLarguraBanda.addTerm(a[i][j], y[i][j]);
                }
                modelo.addLe(restricaoLarguraBanda, lf[i]);
            }
            
            //Restrição de demanda
            for (int j = 0; j < qtdConsumidores; j++) {
                IloLinearNumExpr restricaoDemanda = modelo.linearNumExpr();
                for (int i = 0; i < qtdAntenas; i++) {
                    restricaoDemanda.addTerm(1, y[i][j]);
                }
                modelo.addEq(restricaoDemanda, dc[j]);
            }
            
//            //
//            for (int i = 0; i < qtdAntenas; i++) {
//                IloLinearNumExpr dir = modelo.linearNumExpr();
//                for (int j = 0; j < qtdConsumidores; j++) {
//                    dir.addTerm(a[i][j], y[i][j]);
//                    modelo.addLe(y[i][j], a[i][j]*y[i][j]);
//                }
//            }
            for (int i = 0; i < qtdAntenas; i++) {
                for (int j = 0; j < qtdConsumidores; j++) {
                    IloLinearNumExpr esq = modelo.linearNumExpr();
                    esq.addTerm(1, y[i][j]);
                    modelo.addLe(esq, a[i][j]*m);
                }
            }
            
            modelo.solve();
            
//            for (int i = 0; i < qtdAntenas; i++) {
//                double aux = 0;
//                for (int j = 0; j < qtdConsumidores; j++) {
//                    System.out.print(modelo.getValue(y[i][j]) + " ");
//                    aux += modelo.getValue(y[i][j]);
//                }
//                System.out.print("\tTotal de banda fornecida: " + aux + "\n");
//            }
//            for (int i = 0; i < qtdAntenas; i++) {
//                System.out.println("Antena " + (i + 1) + ": " + modelo.getValue(x[i]));
//            }
            System.out.println("Valor min: " + modelo.getObjValue());
        } catch (IloException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}