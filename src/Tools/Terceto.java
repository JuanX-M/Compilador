package Tools;

public class Terceto {

    private Integer numTerceto;
    private String first;
    private String second;
    private String third;
    private Integer linea;

    public Terceto( Integer numTerceto,String first, String second, String third) {
        this.numTerceto = numTerceto;
        this.first = first;
        this.second = second;
        this.third =  third;
        this.linea = -1;
    }

    public int getSoloNumTerceto() {
        return numTerceto;
    }

    public String getNumTerceto() {
        return "(" + numTerceto.toString() + ")";
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public String getThird() {
        return third;
    }

    public Integer getLinea() {
        return linea;
    }

    public void addFirst(String first) {
        this.first = first;
    }

    public void addSecond(String second) {
        this.second = second;
    }

    public void addThird (String third) {
        this.third = third;
    }

    public void addLine (Integer linea) {
        this.linea = linea;
    }

    @Override
    public String toString() {
        return "{" + numTerceto + "}[" + first + "," + second + "," + third + "]";
    }
}
