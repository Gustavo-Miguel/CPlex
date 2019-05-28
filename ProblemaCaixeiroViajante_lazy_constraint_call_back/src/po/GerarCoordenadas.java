package po;

import java.io.FileWriter;
import java.io.IOException;

public class GerarCoordenadas {

    private static void gerarCsv(String nomeArquivo) throws IOException {
        try {
            FileWriter writer = new FileWriter(nomeArquivo);

            writer.append("x,y\n");
            for (int i = 0; i < 5; i++) {
                writer.append(String.valueOf(Math.random()*100) + "," + String.valueOf(Math.random()*100) + "\n");
            }
            
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        GerarCoordenadas g = new GerarCoordenadas();
        g.gerarCsv("coordenadas.csv");
    }
}
