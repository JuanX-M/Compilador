package Tools;

/**
 * Representa una entrada en la Tabla de Símbolos.
 * Almacena los atributos asociados de un lexema, como su tipo de token y nroLinea.
 */
class Info {

    //TODO: Verificar atributos privados de Info
    private String tokenType; // ej: "ID", "CTE_INT"
    private int nroLinea;

    public Info(String tokenType, int nroLinea) {
        this.tokenType = tokenType;
        this.nroLinea=nroLinea; // creo que se calcula por lexico, cambiar esto
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getNroLinea() {
        return nroLinea;
    }
}