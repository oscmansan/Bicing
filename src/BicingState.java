import IA.Bicing.Estaciones;

public class BicingState {

    public static final int SENSE_ESTACIO = -1;
    public static final int ORIGEN  = 0; //Estacio d'origen
    public static final int DESTI1  = 1; //Estacio de desti  (1)
    public static final int NBICIS1 = 2; //Numero de bicis a (1)
    public static final int DESTI2  = 3; //Estacio de desti  (2)
    public static final int NBICIS2 = 4; //Numero de bicis a (2)

    public static Estaciones estaciones;
    public static int nFurgos;
    public int[][] furgos;

    public BicingState(int nest, int nbic, int dem, int seed, int nf) {
        nFurgos = nf;
        estaciones = new Estaciones(nest, nbic, dem, seed);
    }

    public BicingState() {
        furgos = new int[nFurgos][5];
    }

    public final void solucioTrivial() {
        for(int i = 0; i < nFurgos; ++i) {
            furgos[i][ORIGEN] = furgos[i][DESTI1] = i;
            furgos[i][DESTI2] = SENSE_ESTACIO;
            furgos[i][NBICIS1] = furgos[i][NBICIS2] = 0;
        }
    }

    public final void solucioElaborada() {
        solucioTrivial(); //TODO Shhhhhh... tu no has vist res...
    }

    public BicingState copy() {
        BicingState b = new BicingState();
        for (int i = 0; i < nFurgos; ++i) {
            b.furgos[i][0] = furgos[i][0];
            b.furgos[i][1] = furgos[i][1];
            b.furgos[i][2] = furgos[i][2];
            b.furgos[i][3] = furgos[i][3];
            b.furgos[i][4] = furgos[i][4];
        }
        return b;
    }

    //Operadors
    public final void canviarOrigen(int i, int estacio) {
        furgos[i][0] = estacio;
    }

    public final void canviarDesti(int i, int desti, int estacio) {
        furgos[i][desti] = estacio;
    }

    //Getters
    public final int getOrigen(int i) {
        return furgos[i][0];
    }

    public final int getDesti(int i, int desti) {
        return furgos[i][desti];
    }

    public final int getNumBicis(int i, int desti) {
        return furgos[i][desti+1];
    }
}
