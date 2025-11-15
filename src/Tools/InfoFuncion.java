package Tools;

import java.util.ArrayList;

public class InfoFuncion extends Info{
    private ArrayList<String> listaTipos;
    private ArrayList<Info> listaParametrosFormales;

    private ArrayList<Terceto> listaExpAritmeticas;

    public InfoFuncion(String nombre, String token,String uso,String ambito,ArrayList<String> listaTipos) {
        super(nombre, token, null, uso, ambito);
        this.listaTipos = listaTipos;
        this.listaParametrosFormales = new ArrayList<>();
        this.listaExpAritmeticas = new ArrayList<>();
    }

    public void setListaPrFormales(ArrayList<Info> listaParametrosFormales) {
        this.listaParametrosFormales = listaParametrosFormales;
    }

    public void setListaExpAritmeticas(ArrayList<Terceto> listaExpAritmeticas) {
        this.listaExpAritmeticas=listaExpAritmeticas;
    }
    @Override
    public String toString() {
        // 1. Llama al toString() de la clase padre (Info)
        //    (Asegúrate de que Info.java también tenga un buen toString())
        String infoBase = super.toString();

        // 2. Prepara un StringBuilder para agregar la nueva información
        StringBuilder sb = new StringBuilder(infoBase);

        // 3. Agrega las listas
        sb.append("\n\t  -> Tipos de Retorno: ");
        sb.append(this.listaTipos != null ? this.listaTipos.toString() : "ninguno");

        sb.append("\n\t  -> Params Formales: ");
        sb.append(this.listaParametrosFormales != null ? this.listaParametrosFormales.toString() : "ninguno");

        sb.append("\n\t  -> Tercetos (Cuerpo): ");
        sb.append(this.listaExpAritmeticas != null ? this.listaExpAritmeticas.toString() : "ninguno");

        // 4. Retorna el String completo
        return sb.toString();
    }

}
