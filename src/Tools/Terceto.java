package Tools;

public class Terceto implements Comparable<Terceto>{

    private Integer numTerceto;
    private String first;
    private String second;
    private String third;

    public Terceto( Integer numTerceto,String first, String second, String third) {
        this.numTerceto = numTerceto;
        this.first = first;
        this.second = second;
        this.third =  third;
    }

    @Override
    public int compareTo(Terceto t) {
        return this.numTerceto - t.getSoloNumTerceto();
        /*if (this.getSoloNumTerceto() < t.getSoloNumTerceto())
            return -1;
        else
            if (this.getSoloNumTerceto() > t.getSoloNumTerceto())
                return 1;
        return 0;*/
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

    public void addFirst(String first) {
        this.first = first;
    }

    public void addSecond(String second) {
        this.second = second;
    }

    public void addThird (String third) {
        this.third = third;
    }

    @Override
    public String toString() {
        return "{" + numTerceto + "}[" + first + "," + second + "," + third + "]";
    }
}
