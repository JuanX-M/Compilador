package Tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public final class Logger {
    private static final String OUTPUT = "/output/";
    private static final String LOG_FILE = "log.txt";
    private static final ArrayList<String> warnings = new ArrayList<>();
    private static final ArrayList<String> errors = new ArrayList<>();
    private static final ArrayList<String> tokens = new ArrayList<>();
    private static final ArrayList<String> rules = new ArrayList<>();
    private static ArrayList<String> tercetos = new ArrayList<>();

    private Logger(){};

    private enum LogType {
        ERROR,
        WARNING,
        TOKEN,
        RULE,
        TERCETO;

    }

    public static void logError(int line, Object message) {
        errors.add("Se encontro un " + LogType.ERROR + " en la linea [" + line + "] : " + message + "\n");
    }

    public static void logWarning(int line, Object message) {
        warnings.add("Se encontro un " + LogType.WARNING + " en la linea [" + line + "] : " + message + "\n");
    }

    public static void logToken(int line, Object message) {
        tokens.add("Se encontro un " + LogType.TOKEN + " en la linea [" + line + "] : " + message + "\n");
    }

    public static void logRule(int line, Object message) {
        rules.add("Se encontro un " + LogType.RULE + " en la linea [" + line + "] : " + message + "\n");
    }

    public static void logTerceto(int line, Object message) {
        tercetos.add("Se encontro un " + LogType.TERCETO + " en la linea [" + line + "] : " + message + "\n");
    }

    public static int extraerNumTercetos(String linea){
        int inicio = linea.indexOf('{');
        int fin = linea.indexOf('}');
        String numeroComoString = linea.substring(inicio + 1, fin);
        return Integer.parseInt(numeroComoString);
    }
    public static void exportarTercetos(String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            for (String terceto : tercetos) {
                String contenido = extraerContenidoTerceto(terceto);
                writer.write(contenido + "\n");
            }
            System.out.println("Archivo de tercetos generado exitosamente en: " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al generar el archivo de tercetos: " + e.getMessage());
        }
    }

    public static String extraerContenidoTerceto(String linea) {
        int inicio = linea.indexOf('{');
        return linea.substring(inicio);
    }
    public static String generateLog() {
        String out = null;
        out = "\n>>>    LOG \n\n";
        for(String s: tercetos) {
            out += s;
        }
        //for (String s : rules) {
          //  out += s; TODO: Sacar esto
        //}
        for (String s : warnings) {
            out += s;
        }
        for (String s : errors) {
            out += s;
        }
        for (String s : tokens) {
            out += s;
        }
        out += "\nCantidad de errores totales: " + errors.size();
        out += "\nCantidad de warnings totales: " + warnings.size();
        return out;
    }
}
