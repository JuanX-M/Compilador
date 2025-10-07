package Tools;

/**
 * Representa una entrada en la Tabla de Símbolos.
 * Almacena los atributos asociados de un lexema, como su tipo de token y nroLinea.
 */
public class Info {

    //TODO: Verificar atributos privados de Info
    private String lexema; // ej: "ID", "CTE_INT"
    private Integer token;

    public Info(String lexema, Integer token) {
        this.lexema = lexema;
        this.token = token; // creo que se calcula por lexico, cambiar esto
    }

    @Override
    public String toString() {
        return "Info{ " +
                "Lexema : " + lexema + " " +
                ", Token : " + token +
                "}";
    }

    public String getLexema() {
        return lexema;
    }
    public Integer getToken() {
        return token;
    }
}