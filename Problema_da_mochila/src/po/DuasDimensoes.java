package po;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DuasDimensoes {

    public static void main(String[] args) throws IloException, FileNotFoundException, IOException {
        new DuasDimensoes();
    }

    public DuasDimensoes() throws IloException, FileNotFoundException, IOException {

        try {
            int qtdItens = 100;//qtd itens
            int qtdConteiners = 3;
            double c[] = new double[qtdItens];//custo
            double p[] = new double[qtdItens];//peso
            double v[] = new double[qtdItens];//volume
            double pl = 500;//peso limite para cada contêiner
            double vl = 70;//volume limite para cada contêiner
            int li = 5;//máx 5 unidades de cada item

            //Leitura do arquivo
            BufferedReader br = new BufferedReader(new FileReader("instancia_p1.csv"));
            String linha = br.readLine();
            String s[];
            for (int i = 0; i < qtdItens; i++) {
                linha = br.readLine();
                s = linha.split(",");
                c[i] = Double.parseDouble(s[1]);
                p[i] = Double.parseDouble(s[2]);
                v[i] = Double.parseDouble(s[3]);
                //System.out.println(c[i] + "\t" + p[i] + "\t" + v[i]);
            }

            //INÍCIO CÓDIGO CPLEX
            IloCplex modelo = new IloCplex();

            //variaveis de decisao
            IloIntVar x[][] = new IloIntVar[qtdItens][qtdConteiners];
            IloIntVar y[][] = new IloIntVar[qtdItens][qtdConteiners];
            for (int i = 0; i < qtdItens; i++) {
                for (int j = 0; j < qtdConteiners; j++) {
                    x[i][j] = modelo.intVar(0, li);
                    y[i][j] = modelo.intVar(0, 1);
                }
            }

            //funcao objetivo
            IloLinearNumExpr fo = modelo.linearNumExpr();
            for (int j = 0; j < qtdConteiners; j++) {
                for (int i = 0; i < qtdItens; i++) {
                    fo.addTerm(c[i], x[i][j]);
//                    fo.addTerm((int)y[i][j], x[i][j]);;
                    modelo.add(modelo.ifThen(modelo.ge(x[i][j], 1), modelo.eq(y[i][j], 1)));
                }
            }                        

            modelo.addMaximize(fo);

            //restricoes de peso
            for (int j = 0; j < qtdConteiners; j++) {
                IloLinearNumExpr restricaoPeso = modelo.linearNumExpr();
                for (int i = 0; i < qtdItens; i++) {
                    restricaoPeso.addTerm(p[i], x[i][j]);
                }
                modelo.addLe(restricaoPeso, pl);
            }

            //restricoes de volume
            for (int j = 0; j < qtdConteiners; j++) {
                IloLinearNumExpr restricaoVolume = modelo.linearNumExpr();
                for (int i = 0; i < qtdItens; i++) {
                    restricaoVolume.addTerm(v[i], x[i][j]);
                }
                modelo.addLe(restricaoVolume, vl);
            }

            //restricoes de quantidade maxima de cada item
            for (int i = 0; i < qtdItens; i++) {
                IloLinearNumExpr restricaoQtd = modelo.linearNumExpr();
                for (int j = 0; j < qtdConteiners; j++) {
                    restricaoQtd.addTerm(1, x[i][j]);
                }
                modelo.addLe(restricaoQtd, li);
            }

            //restricoes de "se um item estiver em um contêiner este item não pode estar nos demais contêiners"
            for (int i = 0; i < qtdItens; i++) {
                IloLinearNumExpr restricaoMesmoItemJunto = modelo.linearNumExpr();
                for (int j = 0; j < qtdConteiners; j++) {
                    restricaoMesmoItemJunto.addTerm(1, y[i][j]);
                }
                modelo.addLe(restricaoMesmoItemJunto, 1);
            }
            
            
            //Duas Dimensoes
            //variacao 1
//            IloLinearNumExpr restricao1 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {
//                restricao1.addTerm(1, x[0][j]);
//            }
//            modelo.addLe(1, restricao1);
            
            //variacao 2
//            IloLinearNumExpr restricao2_1 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {
//                restricao2_1.addTerm(1, x[0][j]);
//            }
//            modelo.addLe(1, restricao2_1);
//            
//            IloLinearNumExpr restricao2_2 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {
//                restricao2_2.addTerm(1, x[1][j]);
//            }
//            modelo.addLe(1, restricao2_2);
//            
//            for (int j = 0; j < qtdConteiners; j++) {
//                IloLinearNumExpr restricao2_3 = modelo.linearNumExpr();
//                restricao2_3.addTerm(1, y[0][j]);
//                restricao2_3.addTerm(-1, y[1][j]);
//                modelo.addEq(restricao2_3, 0);
//            }
                       
            //restricao3
//            IloLinearNumExpr restricao3_1 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {
//                restricao3_1.addTerm(1, x[0][j]);
//            }
//            modelo.addLe(1, restricao3_1);
//            
//            IloLinearNumExpr restricao3_2 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {
//                restricao3_2.addTerm(1, x[1][j]);
//            }
//            modelo.addLe(1, restricao3_2);
//            
//            for (int j = 0; j < qtdConteiners; j++) {
//                IloLinearNumExpr restricao3_3 = modelo.linearNumExpr();
//                restricao3_3.addTerm(1, y[0][j]);
//                restricao3_3.addTerm(1, y[1][j]);
//                modelo.addLe(restricao3_3, 1);
//            }
            
            //restricao4
            IloLinearNumExpr restricao4 = modelo.linearNumExpr();
            for (int j = 0; j < qtdConteiners; j++) {
                restricao4.addTerm(1, y[0][j]);
                restricao4.addTerm(1, y[1][j]);
            }
            modelo.addEq(restricao4, 1);
                        

//            modelo.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.03);
            modelo.setParam(IloCplex.Param.TimeLimit, 150);
            modelo.solve();

            //Escrevendo os resultados
            for (int j = 0; j < qtdConteiners; j++) {
                System.out.println("Contêiner " + (j + 1) + ":");
                for (int i = 0; i < qtdItens; i++) {
                    if (modelo.getValue(x[i][j]) != 0) {
                        System.out.println("\tItem: " + (i + 1) + "\tQtd: " + modelo.getValue(x[i][j]));
                    }
                }
                System.out.println("");
            }

            System.out.println("Valor máx (soma de todos os contêiners): " + modelo.getObjValue());

        } catch (IloException ex) {
            Logger.getLogger(DuasDimensoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
