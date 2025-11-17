package Tools;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class GeneradorAssembler {

    // Estructura interna para manejar los datos del terceto
    private static class Terceto {
        int id;
        String operador;
        String op1;
        String op2;

        public Terceto(int id, String operador, String op1, String op2) {
            this.id = id;
            this.operador = operador != null ? operador.trim() : "";
            this.op1 = (op1 == null || op1.trim().equals("null")) ? null : op1.trim();
            this.op2 = (op2 == null || op2.trim().equals("null")) ? null : op2.trim();
        }
    }

    private List<Terceto> listaTercetos = new ArrayList<>();
    private Set<String> variablesDeclaradas = new HashSet<>();
    private Map<Integer, String> mapaComparaciones = new HashMap<>();

    // NUEVO: Mapa para guardar las constantes de string (ej: "Hola" -> str_const_1)
    private Map<String, String> constantesString = new HashMap<>();
    private int contadorStrings = 0;

    public void generarArchivoASM(String rutaEntrada, String rutaSalida) {
        leerTercetos(rutaEntrada);

        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaSalida))) {
            escribirEncabezado(writer);
            escribirSeccionData(writer);
            escribirSeccionCode(writer);
            escribirFin(writer);

            System.out.println("¡Éxito! Archivo generado en: " + rutaSalida);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // 1. LECTURA Y PARSEO
    // ---------------------------------------------------------
    private void leerTercetos(String ruta) {
        Pattern pattern = Pattern.compile("\\{(\\d+)}\\[(.*?),(.*?),(.*?)\\]");

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                Matcher matcher = pattern.matcher(linea);
                if (matcher.find()) {
                    int id = Integer.parseInt(matcher.group(1));
                    String op = matcher.group(2);
                    String arg1 = matcher.group(3);
                    String arg2 = matcher.group(4);

                    Terceto t = new Terceto(id, op, arg1, arg2);
                    listaTercetos.add(t);

                    recolectarVariable(t.op1);
                    recolectarVariable(t.op2);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo de tercetos: " + e.getMessage());
        }
    }

    private void recolectarVariable(String token) {
        if (token == null) return;
        if (esReferencia(token)) return;
        if (esNumero(token)) return;
        if (token.startsWith("ETIQUETA")) return;

        // NUEVO: Si empieza con comillas, es un string. Lo guardamos en el mapa.
        if (token.startsWith("\"")) {
            if (!constantesString.containsKey(token)) {
                constantesString.put(token, "str_const_" + (contadorStrings++));
            }
            return; // No lo agregamos a variablesDeclaradas (DD)
        }

        // Si no es nada de lo anterior, es una variable numérica
        variablesDeclaradas.add(limpiarNombre(token));
    }

    // ---------------------------------------------------------
    // 2. ESCRITURA DEL ASSEMBLER
    // ---------------------------------------------------------
    private void escribirEncabezado(PrintWriter w) {
        w.println(".386");
        w.println(".model flat, stdcall");
        w.println("option casemap :none");
        w.println("include C:\\masm32\\include\\windows.inc");
        w.println("include C:\\masm32\\include\\kernel32.inc");
        w.println("include C:\\masm32\\include\\user32.inc");
        w.println("include C:\\masm32\\include\\masm32.inc");
        w.println("includelib C:\\masm32\\lib\\kernel32.lib");
        w.println("includelib C:\\masm32\\lib\\user32.lib");
        w.println("includelib C:\\masm32\\lib\\masm32.lib");
        w.println();
    }

    private void escribirSeccionData(PrintWriter w) {
        w.println(".DATA");
        w.println("    ; Variables de usuario");
        for (String var : variablesDeclaradas) {
            w.println("    " + var + " DD 0");
        }

        w.println("    ; Variables auxiliares del sistema");
        w.println("    buffer_print BYTE 128 dup(0)");
        w.println("    newline DB 13, 10, 0");
        w.println("    pause_msg DB 13, 10, \"Presione Enter para salir...\", 0");
        w.println("    input_char DB ?");

        // Variables temporales aritméticas
        for (Terceto t : listaTercetos) {
            if (esOperacionAritmetica(t.operador)) {
                w.println("    @temp_terceto_" + t.id + " DD 0");
            }
        }

        // Constantes de String (Tu corrección)
        w.println("    ; Constantes de texto");
        for (Map.Entry<String, String> entry : constantesString.entrySet()) {
            // entry.getValue() es la etiqueta (str_const_0)
            // entry.getKey() es el valor ("Hola")
            w.println("    " + entry.getValue() + " DB " + entry.getKey() + ", 0");
        }
        w.println();
    }

    private void escribirSeccionCode(PrintWriter w) {
        w.println(".CODE");
        w.println("start:");

        for (Terceto t : listaTercetos) {
            w.println("; Terceto " + t.id + " -> " + t.operador);
            w.println("Label_" + t.id + ":");

            if (t.operador.startsWith("ETIQUETA")) {
                w.println(t.operador + ":");
                continue;
            }

            switch (t.operador) {
                case ":=":
                    String destino = limpiarNombre(t.op1);
                    String origen = resolverOperando(t.op2);
                    w.println("    MOV EAX, " + origen);
                    w.println("    MOV " + destino + ", EAX");
                    break;

                case "+":
                case "-":
                case "*":
                case "/":
                    generarAritmetica(w, t);
                    break;

                case "<":
                case ">":
                case "<=":
                case ">=":
                case "==":
                case "!=":
                case "<>":
                    generarComparacion(w, t);
                    break;

                case "BI":
                    w.println("    JMP " + resolverLabel(t.op1));
                    break;

                case "BF":
                    generarSaltoBF(w, t);
                    break;

                case "CALL":
                    w.println("    CALL " + resolverLabel(t.op1));
                    break;

                case "RET":
                    w.println("    RET");
                    break;

                case "print":
                    generarPrint(w, t);
                    break;
            }
            w.println();
        }
    }

    private void escribirFin(PrintWriter w) {
        w.println();
        w.println("    ; --- PAUSA FINAL ---");
        w.println("    invoke StdOut, addr pause_msg");
        w.println("    invoke StdIn, addr input_char, 1");
        w.println("    invoke ExitProcess, 0");
        w.println("end start");
    }

    // ---------------------------------------------------------
    // 3. MÉTODOS AUXILIARES DE GENERACIÓN
    // ---------------------------------------------------------

    private void generarPrint(PrintWriter w, Terceto t) {
        String token = t.op1;

        // NUEVO: Lógica inteligente para saber si imprimir Texto o Número
        if (token.startsWith("\"")) {
            // Es un string constante (ej: "Nicolas Ortiz")
            String nombreEtiqueta = constantesString.get(token);
            w.println("    invoke StdOut, addr " + nombreEtiqueta);
        } else {
            // Es una variable numérica o constante numérica
            String valorPrint = resolverOperando(token);
            w.println("    invoke dwtoa, " + valorPrint + ", addr buffer_print");
            w.println("    invoke StdOut, addr buffer_print");
        }

        w.println("    invoke StdOut, addr newline");
    }

    private void generarAritmetica(PrintWriter w, Terceto t) {
        String val1 = resolverOperando(t.op1);
        String val2 = resolverOperando(t.op2);

        w.println("    MOV EAX, " + val1);

        if (t.operador.equals("+")) {
            w.println("    ADD EAX, " + val2);
        } else if (t.operador.equals("-")) {
            w.println("    SUB EAX, " + val2);
        } else if (t.operador.equals("*")) {
            w.println("    IMUL EAX, " + val2);
        } else if (t.operador.equals("/")) {
            w.println("    MOV ECX, " + val2);
            w.println("    CDQ");
            w.println("    IDIV ECX");
        }
        w.println("    MOV @temp_terceto_" + t.id + ", EAX");
    }

    private void generarComparacion(PrintWriter w, Terceto t) {
        String val1 = resolverOperando(t.op1);
        String val2 = resolverOperando(t.op2);
        w.println("    MOV EAX, " + val1);
        w.println("    CMP EAX, " + val2);
        mapaComparaciones.put(t.id, t.operador);
    }

    private void generarSaltoBF(PrintWriter w, Terceto t) {
        String refCondicion = t.op1;
        String destino = resolverLabel(t.op2);
        String operadorPrevio = "";

        if (esReferencia(refCondicion)) {
            int idCond = obtenerIdDesdeReferencia(refCondicion);
            operadorPrevio = mapaComparaciones.getOrDefault(idCond, "");
        }

        String instruccionSalto = "JMP";
        switch (operadorPrevio) {
            case "<":  instruccionSalto = "JGE"; break;
            case ">":  instruccionSalto = "JLE"; break;
            case "<=": instruccionSalto = "JG";  break;
            case ">=": instruccionSalto = "JL";  break;
            case "==": instruccionSalto = "JNE"; break;
            case "!=":
            case "<>": instruccionSalto = "JE";  break;
        }
        w.println("    " + instruccionSalto + " " + destino);
    }

    // ---------------------------------------------------------
    // 4. UTILIDADES
    // ---------------------------------------------------------

    private String resolverLabel(String raw) {
        if (raw == null) return "Label_Error";
        if (esReferencia(raw)) {
            return "Label_" + obtenerIdDesdeReferencia(raw);
        }
        return raw;
    }

    private String resolverOperando(String raw) {
        if (raw == null) return "0";
        if (esReferencia(raw)) {
            return "@temp_terceto_" + obtenerIdDesdeReferencia(raw);
        }
        if (esNumero(raw)) {
            if (raw.contains(".")) return raw.substring(0, raw.indexOf("."));
            return raw;
        }
        return limpiarNombre(raw);
    }

    private int obtenerIdDesdeReferencia(String ref) {
        return Integer.parseInt(ref.substring(1, ref.length() - 1));
    }

    private String limpiarNombre(String s) {
        return s.replace(".", "_");
    }

    private boolean esReferencia(String s) {
        return s != null && s.startsWith("(") && s.endsWith(")");
    }

    private boolean esNumero(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean esOperacionAritmetica(String op) {
        return "+".equals(op) || "-".equals(op) || "*".equals(op) || "/".equals(op);
    }
}