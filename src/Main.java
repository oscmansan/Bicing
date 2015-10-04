import IA.Bicing.Estaciones;

public class Main {

    private static final int nest = 30;
    private final static int nbic = 100;
    private final static int nf = 5;
    private static final int dem = Estaciones.EQUILIBRIUM;
    private static final int seed = 0;

    public static void main(String args[]) {
        BicingState BS = new BicingState(nest, nbic, nf, dem, seed);
        BicingHillClimbingSearch(BS);
        BicingSimulatedAnnealingSearch(BS);
    }

    private static void BicingHillClimbingSearch(BicingState BS) {

    }

    private static void BicingSimulatedAnnealingSearch(BicingState BS) {

    }

}
