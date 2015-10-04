import IA.Bicing.Estaciones;

public class BicingState {

    public static final int SENSE_ESTACIO = -1;
    public static final int ORIGEN  = 0; // Estacio d'origen
    public static final int DESTI1  = 1; // Estacio de desti  (1)
    public static final int NBICIS1 = 2; // Numero de bicis a (1)
    public static final int DESTI2  = 3; // Estacio de desti  (2)
    public static final int NBICIS2 = 4; // Numero de bicis a (2)

    public static Estaciones estacions;
    public static int nfurgos;

    public int[][] furgos;

    /**
     * Constructora per defecte
     */
    public BicingState() {
        furgos = new int[nfurgos][5];
    }

    /**
     * Constructora amb parametres i una solucio inicial
     * @param nest  numero d'estacions
     * @param nbic  numero de bicis
     * @param nf    numero de furgonetes
     * @param dem   tipus d'escenari (equilibrat/hora punta)
     * @param seed  llavor pel generador de numeros aleatoris
     */
    public BicingState(int nest, int nbic, int nf, int dem, int seed) {
        nfurgos = nf;
        estacions = new Estaciones(nest, nbic, dem, seed);
        furgos = new int[nfurgos][5];
        solucioTrivial();
    }

    private final void solucioTrivial() {
        for(int i = 0; i < nfurgos; ++i) {
            furgos[i][ORIGEN] = furgos[i][DESTI1] = i;
            furgos[i][DESTI2] = SENSE_ESTACIO;
            furgos[i][NBICIS1] = furgos[i][NBICIS2] = 0;
        }
    }

    private final void solucioElaborada() {
        solucioTrivial(); //TODO Shhhhhh... tu no has vist res...
    }

    /**
     * Retorna una copia de l'estat
     * @return  una copia de l'estat sobre el qual es crida
     */
    public BicingState copy() {
        BicingState bs = new BicingState();
        for (int i = 0; i < nfurgos; ++i) {
            bs.furgos[i][ORIGEN] = furgos[i][ORIGEN];
            bs.furgos[i][DESTI1] = furgos[i][DESTI1];
            bs.furgos[i][NBICIS1] = furgos[i][NBICIS1];
            bs.furgos[i][DESTI2] = furgos[i][DESTI2];
            bs.furgos[i][NBICIS2] = furgos[i][NBICIS2];
        }
        return bs;
    }


    // Getters

    /**
     * Retorna l'estacio d'origen de la furgoneta i
     * @param i     l'index de la furgoneta
     * @return      l'estacio d'origen de la furgoneta
     */
    public final int getOrigen(int i) {
        return furgos[i][ORIGEN];
    }

    /**
     * Retorna l'estacio de desti (primer o segon) de la furgoneta i
     * @param i     l'index de la furgoneta
     * @param desti indica primer o segon desti
     * @return      l'estacio de desti (primer o segon) de la furgoneta
     */
    public final int getDesti(int i, int desti) {
        return furgos[i][desti];
    }

    /**
     * Retorna el numero de bicis que la furgoneta i deixa al desti
     * @param i     l'index de la furgoneta
     * @param desti indica primer o segon desti
     * @return      el numero de bicis que la furgoneta deixa al desti (primer o segon)
     */
    public final int getNumBicis(int i, int desti) {
        return furgos[i][desti+1];
    }


    // Operadors

    /**
     * Canvia l'estacio d'origen de la furgoneta i
     * @param i         l'index de la furgoneta
     * @param estacio   la nova estacio d'origen
     */
    public final void canviarOrigen(int i, int estacio) {
        furgos[i][0] = estacio;
    }

    /**
     * Canvia l'estacio de desti de la furgoneta i
     * @param i         l'index de la furgoneta
     * @param desti     indica primer o segon desti
     * @param estacio   la nova estacio de desti
     */
    public final void canviarDesti(int i, int desti, int estacio) {
        furgos[i][desti] = estacio;
    }

    /**
     * Canvia el numero de bicis que la furgoneta i deixa al desti
     * @param i     l'index de la furgoneta
     * @param desti indica primer o segon desti
     * @param n     el numero de bicis que la furgoneta deixa al desti
     */
    public final void canviarNumBicis(int i, int desti, int n) {
        furgos[i][desti+1] = n;
    }

}
