package Tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LectorArchivo {
    private final String FILE_NAME;
    private final String PATH_FILE;

    public LectorArchivo(String FILE_NAME, String PATH_FILE){
        this.FILE_NAME = FILE_NAME;
        this.PATH_FILE = PATH_FILE;
    }

    public ArrayList<ArrayList<Character>> read() {
        if (FILE_NAME.length() <= 1) {
            System.out.println("No ingreso el nombre del archivo correctamente.");
            return null;
        }
        String path = System.getProperty("user.dir");
        try {
            File file = new File(path + File.separator + PATH_FILE + File.separator + FILE_NAME);
            if (!file.exists()) {
                System.out.println("El archivo no existe.");
                return null;
            }
            FileReader fileReader = new FileReader(file);
            int character;
            int line = 1;
            ArrayList<ArrayList<Character>> out = new ArrayList<>();
            // Añade la primera línea
            out.add(new ArrayList<>());

            while ((character = fileReader.read()) != -1) {
                //    AÑADE EL CARÁCTER (CUALQUIERA QUE SEA)
                //    Si es una 'H', añade 'H'. Si es un '\n', añade '\n'.
                out.get(line - 1).add((char) character);
                //    SI (Y SÓLO SI) FUE UN '\n', PREPARA LA SIGUIENTE LÍNEA
                if ((char) character == '\n') {
                    out.add(new ArrayList<>());
                    line++;
                }
            }
            //  REVISIÓN FINAL (para archivos que no terminan en '\n')
            ArrayList<Character> ultimaLinea = out.get(line - 1);

            if (ultimaLinea.isEmpty()) {
                // Si la última línea quedó vacía (archivo terminaba en '\n')
                // y no es la única línea (archivo no estaba vacío), la borramos.
                if (out.size() > 1) {
                    out.remove(line - 1);
                }
            } else {
                // Si la última línea NO está vacía (le falta el '\n'), se lo añadimos.
                ultimaLinea.add('\n');
            }

            fileReader.close();
            return out;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}