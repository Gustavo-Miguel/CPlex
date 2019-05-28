
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author a14013
 */
public class Main {

    public static void main(String[] args) {
        Random rnd = new Random();
        double melhor_soja = 0;
        double melhor_milho = 0;
        double melhor_sol = 0;
        double qtd_soja = 0;
        double qtd_milho = 0;

        for (int i = 0; i < 100000; i++) {
            double rs_milho = 50;
            double rs_soja = 70;

            double capacidade = 400;

            double peso_milho = 80;
            double peso_soja = 50;

            double dinheiro = 350;

            double valor_rnd;

            double soja;
            double milho;

            valor_rnd = Math.random() + rnd.nextInt(4);
            soja = valor_rnd;
            capacidade = capacidade - valor_rnd * peso_soja;
            dinheiro = dinheiro - valor_rnd * rs_soja;

            valor_rnd = Math.random() + rnd.nextInt(6);
            milho = valor_rnd;
            capacidade = capacidade - valor_rnd * peso_milho;
            dinheiro = dinheiro - valor_rnd * peso_milho;

            if (capacidade > 0 && dinheiro > 0) {
//                System.out.println("Capacidade: " + capacidade);
//                System.out.println("Dinheiro: " + dinheiro);
//

                if (melhor_sol < (milho * 280) + (soja * 300)) {
                    melhor_milho = milho * 280;
                    melhor_soja = soja * 300;
                    qtd_milho = milho;
                    qtd_soja = soja;

                }

            }
            System.out.println("");
        }
        System.out.println("Rt Milho: " + melhor_milho);
        System.out.println("Rt Soja: " + melhor_soja);

        System.out.println("Milho: " + qtd_milho);
        System.out.println("Soja: " + qtd_soja);
    }
}
