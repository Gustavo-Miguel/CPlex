//relatorio semana que vem (no latex de preferencia) (desenhar formula no sistema do flavio)
//x passa a ser int e nao num
//varios containers
//se um item está num container ele não pode estar nos outros


//Implementação com n contêiners, variáveis de decisão inteiras, se um item estiver em um contêiner este item não pode estar nos demais contêineres.

//Implementação remove restrição possivelmete redundante

//Implementação substitui condições lógicas if-them por operações matemáticas.

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

public class TresDimensoes {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        new TresDimensoes();
    }
    
    public TresDimensoes() throws FileNotFoundException, IOException{
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
            IloIntVar x[][][] = new IloIntVar[qtdItens][qtdConteiners][li + 1];
            for (int i = 0; i < qtdItens; i++) {
                for (int j = 0; j < qtdConteiners; j++) {
                    for (int k = 0; k < (li + 1); k++) {
                        x[i][j][k] = modelo.boolVar();
                    }
                }
            }
            
            //funcao objetivo
            //IloLinearNumExpr fo = modelo.linearNumExpr();
            IloLinearNumExpr fo = modelo.linearNumExpr();
            for (int j = 0; j < qtdConteiners; j++) {
                for (int i = 0; i < qtdItens; i++) {
                    for (int k = 0; k < (li + 1); k++) {
                        fo.addTerm(c[i] * k, x[i][j][k]);
                    }
                }
            }

            modelo.addMaximize(fo);
                        
            //restricoes de peso
            for (int j = 0; j < qtdConteiners; j++) {
                IloLinearNumExpr restricaoPeso = modelo.linearNumExpr();
                for (int i = 0; i < qtdItens; i++) {
                    for (int k = 0; k < (li + 1); k++) {
                        restricaoPeso.addTerm(p[i] * k, x[i][j][k]);
                    }
                }
                modelo.addLe(restricaoPeso, pl);
            }
            
            //restricoes de volume
            for (int j = 0; j < qtdConteiners; j++) {
                IloLinearNumExpr restricaoVolume = modelo.linearNumExpr();
                for (int i = 0; i < qtdItens; i++) {
                    for (int k = 0; k < (li + 1); k++) {
                        restricaoVolume.addTerm(v[i] * k, x[i][j][k]);
                    }
                }
                modelo.addLe(restricaoVolume, vl);
            }
            
            //restricoes de quantidade maxima de cada item
            for (int i = 0; i < qtdItens; i++) {
                IloLinearNumExpr restricaoQtd = modelo.linearNumExpr();
                for (int j = 0; j < qtdConteiners; j++) {
                    for (int k = 0; k < (li + 1); k++) {
                        restricaoQtd.addTerm(k, x[i][j][k]);
                    }
                }
                modelo.addLe(restricaoQtd, li);
            }
            for (int i = 0; i < qtdItens; i++) {
                for (int j = 0; j < qtdConteiners; j++) {
                    IloLinearNumExpr restricao2Qtd = modelo.linearNumExpr();
                    for (int k = 0; k < (li + 1); k++) {
                        restricao2Qtd.addTerm(1, x[i][j][k]);
                    }
                    modelo.addLe(restricao2Qtd, 1);
                }
            }
            
            //restricoes de "se um item estiver em um contêiner este item não pode estar nos demais contêiners"
            for (int i = 0; i < qtdItens; i++) {
                IloLinearNumExpr restricaoMesmoItemJunto = modelo.linearNumExpr();
                for (int j = 0; j < qtdConteiners; j++) {
                    for (int k = 0; k < (li + 1); k++) {
                        restricaoMesmoItemJunto.addTerm(1, x[i][j][k]);
                    }
                }
                modelo.addLe(restricaoMesmoItemJunto, 1);
            }
            
            //Tres Dimensoes
            //restricao1
//            IloLinearNumExpr restricao1 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {                   
//                for (int k = 0; k < (li + 1); k++) {
//                    restricao1.addTerm(1, x[0][j][k]);
//                }
//                modelo.addEq(restricao1, 1);
//            }
            
            //restricao2
//            IloLinearNumExpr restricao2_1 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {                   
//                for (int k = 1; k < (li + 1); k++) {
//                    restricao2_1.addTerm(1, x[0][j][k]);
//                }
//                modelo.addEq(restricao2_1, 1);
//            } 
//            IloLinearNumExpr restricao2_2 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {                   
//                for (int k = 1; k < (li + 1); k++) {
//                    restricao2_2.addTerm(1, x[1][j][k]);
//                }
//                modelo.addEq(restricao2_2, 1);
//            }
//            
//            for (int j = 0; j < qtdConteiners; j++) {
//            IloLinearNumExpr restricao2_3 = modelo.linearNumExpr();
//                for (int k = 0; k < (li + 1); k++) {
//                    restricao2_3.addTerm(1, x[0][j][k]);
//                    restricao2_3.addTerm(-1, x[1][j][k]);
//                }
//                modelo.addEq(restricao2_3, 0);
//            }
            
            //restricao3
            IloLinearNumExpr restricao3_1 = modelo.linearNumExpr();
            for (int j = 0; j < qtdConteiners; j++) {                   
                for (int k = 1; k < (li + 1); k++) {
                    restricao3_1.addTerm(1, x[0][j][k]);
                }
                modelo.addEq(restricao3_1, 1);
            } 
            IloLinearNumExpr restricao3_2 = modelo.linearNumExpr();
            for (int j = 0; j < qtdConteiners; j++) {                   
                for (int k = 1; k < (li + 1); k++) {
                    restricao3_2.addTerm(1, x[1][j][k]);
                }
                modelo.addEq(restricao3_2, 1);
            }
            
            for (int j = 0; j < qtdConteiners; j++) {
            IloLinearNumExpr restricao3_3 = modelo.linearNumExpr();
                for (int k = 0; k < (li + 1); k++) {
                    restricao3_3.addTerm(1, x[0][j][k]);
                    restricao3_3.addTerm(1, x[1][j][k]);
                }
                modelo.addLe(restricao3_3, 1);
            }
            
            //restricao4
//            IloLinearNumExpr restricao4 = modelo.linearNumExpr();
//            for (int j = 0; j < qtdConteiners; j++) {           
//                for (int k = 0; k < (li + 1); k++) {
//                    restricao4.addTerm(1, x[0][j][k]);
//                    restricao4.addTerm(1, x[1][j][k]);
//                }
//                modelo.addEq(restricao4, 1);
//            }
            
            //Tolerância de GAP
//            modelo.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.03);
            modelo.setParam(IloCplex.Param.TimeLimit, 20);

            //modelo.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.0022);
            
            modelo.solve();
            
            //Escrevendo os resultados
            for (int j = 0; j < qtdConteiners; j++) {
                System.out.println("Contêiner " + (j + 1) + ":");
                for (int i = 0; i < qtdItens; i++) {
                    for (int k = 0; k < (li + 1); k++) {
                        if (modelo.getValue(x[i][j][k]) != 0) {
                            System.out.println("\tItem: " + (i + 1) + "\tQtd: " + modelo.getValue(x[i][j][k])*k);
                        }
                    }
                }
                System.out.println("");
            }
            
            System.out.println("Valor máx (soma de todos os contêiners): " + modelo.getObjValue());
            
        } catch (IloException ex) {
            Logger.getLogger(TresDimensoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}