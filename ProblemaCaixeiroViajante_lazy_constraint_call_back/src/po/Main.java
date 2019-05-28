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
    //public int qtdVertices = 10;
    public int qtdVertices = 25;
    //int qtdVertices = 50;
    double coordenadaX[] = new double[qtdVertices];
    double coordenadaY[] = new double[qtdVertices];
    double distancia[][] = new double[qtdVertices][qtdVertices];
    public IloCplex modelo;
    public IloIntVar x[][];
    
    public class MyCallback extends IloCplex.LazyConstraintCallback {
        
        @Override
        protected void main() throws IloException {
            int qtdArestas = 0;
            int origem = 0;
            boolean origemEncontrada = false;
            boolean stop = false;
            int caminho[] = new int[qtdVertices];
            int indice = 0;
            
            for (int i = 0; (i < qtdVertices) && !stop; i++) {
                for (int j = 0; (j < qtdVertices) && !stop; j++) {
                    //if (modelo.getValue(x[i][j]) == 1) {
                    if (Math.round(getValue(x[i][j])) == 1) {
                        qtdArestas++;
                        caminho[indice] = i;
                        indice++;
                        if (!origemEncontrada) {
                            origem = i;
                            origemEncontrada = true;
                        } else if (j == origem) {
                            stop = true;
                        }
                        i = j - 1;
                        break;
                    }
                }
            }

            //Criando restrição dinamicamente
            if (qtdArestas != qtdVertices) {
                IloLinearNumExpr expr = modelo.linearNumExpr();
                for (int i = 0; i < qtdArestas - 1; i++) {
                    expr.addTerm(1, x[caminho[i]][caminho[i + 1]]);
                }
                expr.addTerm(1, x[caminho[qtdArestas - 1]][caminho[0]]);
                this.add(modelo.le(expr, qtdArestas - 1));
            }
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        new Main();
    }

    public Main() throws FileNotFoundException, IOException {
        try {
            //Leitura dos arquivos
            //BufferedReader br = new BufferedReader(new FileReader("10.csv"));
            BufferedReader br = new BufferedReader(new FileReader(qtdVertices + ".csv"));
            //BufferedReader br = new BufferedReader(new FileReader("50.csv"));
            String linha = br.readLine();
            String s[];
            for (int i = 0; i < qtdVertices; i++) {
                linha = br.readLine();
                s = linha.split(",");
                coordenadaX[i] = Double.parseDouble(s[0]);
                coordenadaY[i] = Double.parseDouble(s[1]);
            }

            //Calculando distâncias
            for (int i = 0; i < qtdVertices; i++) {
                distancia[i][i] = 0;
                for (int j = i + 1; j < qtdVertices; j++) {
                    distancia[i][j] = distancia[j][i] = Math.sqrt(Math.pow((coordenadaX[i] - coordenadaX[j]), 2) + (Math.pow((coordenadaY[i] - coordenadaY[j]), 2)));
                }
            }

            //INÍCIO CÓDIGO CPLEX
            modelo = new IloCplex();

            //variaveis de decisao
            x = new IloIntVar[qtdVertices][qtdVertices];//Matriz de Adjacência
            for (int i = 0; i < qtdVertices; i++) {
                for (int j = 0; j < qtdVertices; j++) {
                    x[i][j] = modelo.boolVar();
                }
            }
            for (int i = 0; i < qtdVertices; i++) {
                modelo.addEq(x[i][i], 0);//Para evitar laços
            }

            //Função Objetivo
            IloLinearNumExpr fo = modelo.linearNumExpr();
            for (int i = 0; i < qtdVertices; i++) {
                for (int j = 0; j < qtdVertices; j++) {
                    fo.addTerm(distancia[i][j], x[i][j]);
                }
            }

            modelo.addMinimize(fo);

            //Restrição "para cada vértice há apenas uma aresta que sai e apenas uma que chega"
            for (int i = 0; i < qtdVertices; i++) {
                IloLinearNumExpr expr = modelo.linearNumExpr();
                for (int j = 0; j < qtdVertices; j++) {
                    expr.addTerm(1, x[i][j]);
                }
                modelo.addEq(expr, 1);
            }
            for (int j = 0; j < qtdVertices; j++) {
                IloLinearNumExpr expr = modelo.linearNumExpr();
                for (int i = 0; i < qtdVertices; i++) {
                    expr.addTerm(1, x[i][j]);
                }
                modelo.addEq(expr, 1);
            }

            
            
            modelo.use(new MyCallback());
            
            
            
            modelo.solve();

            
            
            
            
            
            

            //Print x[][]
            System.out.println("");
            for (int i = 0; i < qtdVertices; i++) {
                for (int j = 0; j < qtdVertices; j++) {
                    System.out.print(modelo.getValue(x[i][j]) + " ");
                }
                System.out.println("");
            }

            //Print caminho
            System.out.println("");
            int qtdArestas = 0;
            int origem = 0;
            boolean origemEncontrada = false;
            boolean stop = false;
            int caminho[] = new int[qtdVertices];
            int indice = 0;
            for (int i = 0; (i < qtdVertices) && !stop; i++) {
                for (int j = 0; (j < qtdVertices) && !stop; j++) {
                    if (modelo.getValue(x[i][j]) == 1) {
                        //System.out.println(i + " --> " + j);
                        qtdArestas++;
                        caminho[indice] = i;
                        indice++;
                        if (!origemEncontrada) {
                            origem = i;
                            origemEncontrada = true;
                        } else if (j == origem) {
                            stop = true;
                        }
                        i = j - 1;
                        break;
                    }
                }
            }
            for (int i = 0; i < qtdArestas; i++) {
                System.out.print(caminho[i] + " --> ");
            }
            System.out.println(caminho[0]);
            System.out.println("");

            //Print resultado final
            System.out.println("Valor min: " + modelo.getObjValue());
        } catch (IloException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
