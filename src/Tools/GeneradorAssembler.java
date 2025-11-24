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
        String tipo; // Nuevo atributo

        // Constructor actualizado
        public Terceto(int id, String operador, String op1, String op2, String tipo) {
            this.id = id;
            this.operador = (operador != null) ? operador.trim() : "";
            this.op1 = (op1 == null || op1.trim().equals("null")) ? null : op1.trim();
            this.op2 = (op2 == null || op2.trim().equals("null")) ? null : op2.trim();
            this.tipo = (tipo != null) ? tipo.trim() : null; // Guardamos el tipo
        }
    }

    private List<Terceto> listaTercetos = new ArrayList<>();
    private Set<String> variablesDeclaradas = new HashSet<>();
    private Map<Integer, String> mapaComparaciones = new HashMap<>();

    // NUEVO: Mapa para guardar las constantes de string (ej: "Hola" -> str_const_1)
    private Map<String, String> constantesString = new HashMap<>();
    private int contadorStrings = 0;

    private Map<String, String> constantesFloat = new HashMap<>();
    private int contadorFloats = 0;
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
        // Regex modificada: busca lo usual, y opcionalmente un espacio y palabra al final (Grupo 5)
        Pattern pattern = Pattern.compile("\\{(\\d+)}\\[(.*?),(.*?),(.*?)\\](?:\\s+(\\w+))?");

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
                    String tipo = matcher.group(5); // Aquí capturamos "INT", "FLOAT" o null

                    // Creamos el terceto con el tipo detectado
                    Terceto t = new Terceto(id, op, arg1, arg2, tipo);
                    listaTercetos.add(t);

                    // Recolectar variables para la sección .DATA
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
        if (token.startsWith("ETIQUETA")) return;

        // Strings
        if (token.startsWith("\"")) {
            if (!constantesString.containsKey(token)) {
                constantesString.put(token, "str_const_" + (contadorStrings++));
            }
            return;
        }

        // NUEVO: Lógica para Floats
        if (esNumero(token)) {
            if (token.contains(".")) { // Si tiene punto, es float
                if (!constantesFloat.containsKey(token)) {
                    constantesFloat.put(token, "const_float_" + (contadorFloats++));
                }
            }
            return; // Si es entero o float ya procesado, volvemos
        }

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

        // --- VARIABLES PARA CHEQUEOS DE ERROR (Plan 2011 - Grupo 1) ---
        // a: División por cero
        w.println("    msg_err_div_zero  DB \"Error Runtime: Division por cero\", 0");
        // e: Overflow en producto flotante
        w.println("    msg_err_overflow  DB \"Error Runtime: Overflow en producto flotante\", 0");
        // g: Pérdida de información en conversión Float->Int
        w.println("    msg_err_conv_loss DB \"Error Runtime: Perdida de informacion en conversion Float->Int\", 0");
        w.println("    ; --------------------------------------------------------");

        // Variables temporales aritméticas y de conversión
        for (Terceto t : listaTercetos) {
            // Incluimos 'toi' porque genera una variable temporal para el chequeo 'g'
            if (esOperacionAritmetica(t.operador) || t.operador.equals("toi")) {
                w.println("    @temp_terceto_" + t.id + " DD 0");
            }
        }

        w.println("    ; Constantes de texto");
        for (Map.Entry<String, String> entry : constantesString.entrySet()) {
            w.println("    " + entry.getValue() + " DB " + entry.getKey() + ", 0");
        }

        w.println("    ; Constantes Float");
        for (Map.Entry<String, String> entry : constantesFloat.entrySet()) {
            w.println("    " + entry.getValue() + " DD " + entry.getKey());
        }
        w.println();
    }

    private void escribirSeccionCode(PrintWriter w) {
        w.println(".CODE");
        w.println("start:");

        // --- INICIO DEL BUCLE DE TERCETOS ---
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
                case "toi":
                    String origenFloat = resolverOperando(t.op1);
                    String destinoInt = "@temp_terceto_" + t.id;

                    w.println("    ; Conversion TOI con chequeo de perdida de datos");
                    w.println("    FLD " + origenFloat);
                    w.println("    FISTP " + destinoInt);

                    // Chequeo g)
                    w.println("    FILD " + destinoInt);
                    w.println("    FLD " + origenFloat);
                    w.println("    FCOMP");
                    w.println("    FSTSW AX");
                    w.println("    SAHF");
                    w.println("    JNE Label_Error_Conv_Loss");

                    w.println("    FSTP ST(0)");
                    break;
            }
        }
        // --- FIN DEL BUCLE DE TERCETOS (IMPORTANTE: La llave cierra aquí) ---


        // --- ZONA DE SALIDA Y ERRORES (FUERA DEL FOR) ---

        // 1. Salto para evitar ejecutar los errores si el programa termina bien
        w.println("    JMP Label_Fin_Programa");

        // 2. Rutinas de Error
        w.println("Label_Error_DivZero:");
        w.println("    invoke StdOut, addr msg_err_div_zero");
        w.println("    invoke StdOut, addr newline");
        w.println("    JMP Label_Fin_Programa");

        w.println("Label_Error_Overflow:");
        w.println("    invoke StdOut, addr msg_err_overflow");
        w.println("    invoke StdOut, addr newline");
        w.println("    JMP Label_Fin_Programa");

        w.println("Label_Error_Conv_Loss:");
        w.println("    invoke StdOut, addr msg_err_conv_loss");
        w.println("    invoke StdOut, addr newline");
        w.println("    JMP Label_Fin_Programa");

        // 3. Etiqueta final
        w.println("Label_Fin_Programa:");
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
        boolean esFloat = t.tipo != null && t.tipo.equalsIgnoreCase("FLOAT");

        if (esFloat) {
            // --- FLOATS (Coprocesador 8087) ---

            // Chequeo a) División por Cero (Float)
            if (t.operador.equals("/")) {
                w.println("    ; Chequeo Div Cero (Float)");
                w.println("    FLD " + val2);    // Cargar divisor
                w.println("    FTST");           // Comparar con 0.0
                w.println("    FSTSW AX");       // Guardar estado en AX
                w.println("    SAHF");           // Pasar a flags CPU
                w.println("    JE Label_Error_DivZero");
                w.println("    FSTP ST(0)");     // Limpiar divisor de la pila
            }

            w.println("    FLD " + val1); // Cargar op1

            switch (t.operador) {
                case "+": w.println("    FADD " + val2); break;
                case "-": w.println("    FSUB " + val2); break;
                case "/": w.println("    FDIV " + val2); break;
                case "*":
                    w.println("    FMUL " + val2);

                    // Chequeo e) Overflow en Producto Flotante
                    // Verificamos el bit de Overflow (OE) en la palabra de estado del 8087
                    w.println("    ; Chequeo Overflow (Float Product)");
                    w.println("    FSTSW AX");      // Estado a AX
                    w.println("    TEST AL, 8");    // Bit 3 (0x08) es Overflow Flag (OE)
                    w.println("    JNZ Label_Error_Overflow");
                    break;
            }
            w.println("    FSTP @temp_terceto_" + t.id);

        } else {
            // --- ENTEROS (CPU) ---
            w.println("    MOV EAX, " + val1);

            switch (t.operador) {
                case "+": w.println("    ADD EAX, " + val2); break;
                case "-": w.println("    SUB EAX, " + val2); break; // Sin chequeo de resta negativa
                case "*": w.println("    IMUL EAX, " + val2); break;
                case "/":
                    // Chequeo a) División por Cero (Enteros)
                    w.println("    ; Chequeo Div Cero (Int)");
                    w.println("    MOV ECX, " + val2);
                    w.println("    CMP ECX, 0");
                    w.println("    JE Label_Error_DivZero");

                    w.println("    CDQ");
                    w.println("    IDIV ECX");
                    break;
            }
            w.println("    MOV @temp_terceto_" + t.id + ", EAX");
        }
    }

    private void generarComparacion(PrintWriter w, Terceto t) {
        String val1 = resolverOperando(t.op1);
        String val2 = resolverOperando(t.op2);

        if (t.tipo != null && t.tipo.equalsIgnoreCase("FLOAT")) {
            w.println("    FLD " + val1);        // Carga val1
            w.println("    FCOM " + val2);       // Compara con val2
            w.println("    FSTSW AX");           // Mueve flags del coprocesador a AX
            w.println("    SAHF");               // Mueve AH a los flags del CPU
            // Ahora los saltos normales (JE, JB, JA) funcionarán en generarSaltoBF
        } else {
            w.println("    MOV EAX, " + val1);
            w.println("    CMP EAX, " + val2);
        }
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
        if (constantesFloat.containsKey(raw)) {
            return constantesFloat.get(raw);
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
        if (s == null) return "";
        String resultado = s.replace(".", "_");
        return resultado.replaceAll("[^a-zA-Z0-9_]", "_");
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