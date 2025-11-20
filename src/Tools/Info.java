package Tools;

import java.util.ArrayList;

/**
 * Representa una entrada en la Tabla de Símbolos.
 * Almacena los atributos asociados de un lexema, como su tipo de token y nroLinea.
 */
public class Info {

    //TODO: Verificar atributos privados de Info
    private final String nombre; // ej: "ID", "CTE_INT"
    private final String token;
    private final String tipo;
    private final String uso;
    private final String ambito;
    private String varAux=null;

    private String nroTercetoEtiqueta;
    private ArrayList<String> listaVariablesRetorno=null;

    private ArrayList<String> listaParametrosFormales=null;

    public Info(String token, String tipo) {
        this.nombre = null;
        this.token = token;
        this.tipo = tipo;
        this.uso = null;
        this.ambito = null;
        this.nroTercetoEtiqueta=null;

    }

    public Info(String nombre, String token, String tipo, String ambito) {
        this.nombre = nombre;
        this.token = token; // creo que se calcula por lexico, cambiar esto
        this.tipo = tipo;
        this.uso = null;
        this.ambito = ambito;
        this.nroTercetoEtiqueta=null;

    }

    public Info(String nombre, String token, String tipo, String uso, String ambito) {
        this.nombre = nombre;
        this.token = token;
        this.tipo = tipo;
        this.uso = uso;
        this.ambito = ambito;
        this.nroTercetoEtiqueta=null;

    }

    public Info(String nombre, String token, String tipo, String uso, String ambito,String nroTercetoEtiqueta) {
        this.nombre = nombre;
        this.token = token;
        this.tipo = tipo;
        this.uso = uso;
        this.ambito = ambito;
        this.nroTercetoEtiqueta=nroTercetoEtiqueta;

    }

    @Override
    public String toString() {
        return " Info {" +
                "Nombre: " + nombre +
                ", Token: " + token +
                ", Tipo: " + tipo +
                ", Uso: " + uso +
                ", Ambito: " + ambito +
                ", listaVariablesRetorno: " + listaVariablesRetorno +
                ", listaParametrosFormales: " + listaParametrosFormales +
                "}";
    }

    public String getTipo() {
        return tipo;
    }

    public void setNroTercetoEtiqueta(String nroTercetoEtiqueta) {
        this.nroTercetoEtiqueta = nroTercetoEtiqueta;
    }

    public String getUso() {
        return uso;
    }

    public String getAmbito() {
        return ambito;
    }

    public String getToken() {
        return token;
    }

    public String getVarAux() {
        return varAux;
    }

    public void setVarAux(String varAux){
        this.varAux=varAux;
    }
    public String getNroTercetoEtiqueta() {
        return nroTercetoEtiqueta;
    }

    public void setListaVariablesRetorno(ArrayList<String> lista) {
        this.listaVariablesRetorno = lista;
    }

    public ArrayList<String> getListaVariablesRetorno() {
        return this.listaVariablesRetorno;
    }

    public ArrayList<String> getListaParametrosFormales() {
        return listaParametrosFormales;
    }

    public String getNombre() {
        return nombre;
    }

    public void setListaParametrosFormales(ArrayList<String> listaParametrosFormales) {
        this.listaParametrosFormales = listaParametrosFormales;
    }
}