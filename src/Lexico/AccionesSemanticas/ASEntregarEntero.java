package Lexico.AccionesSemanticas;

public class ASEntregarEntero extends AccionSemantica {
    public ASEntregarEntero() {
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Tools.Cursor cursor) {
        String lexema = "";
        int valor = 0;
        if (!buffer.isEmpty()) {
            lexema = "";
            for (int i = 0; i < buffer.getSize(); i++) {
                lexema += buffer.getCharacter(i);
            }
            try {
                valor = Integer.parseInt(lexema);
            } catch (NumberFormatException e) {
                //Logger.log("Error: El número es demasiado grande para un entero.");
                valor = Integer.MAX_VALUE; // O cualquier otro valor que indique un error
            }
        }
        buffer.clear();
        return new Pair<>(lexema, valor);
    }
}
