import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;

public class BicingState {

    public static final int MAX_BIKES_PER_VAN = 30;

    public static final String ORIG_SWAP = "Origin swap";
    public static final String DEST_CHANGE = "Destination change";
    public static final String NBIKES_CHANGE = "Num bikes change";

    public static final int NO_STATION = -1;
    public static final int ORIG = 0; // Estacio d'origen
    public static final int DEST1 = 1; // Estacio de desti  (1)
    public static final int NBIKES1 = 2; // Numero de bicis a (1)
    public static final int DEST2 = 3; // Estacio de desti  (2)
    public static final int NBIKES2 = 4; // Numero de bicis a (2)

    public static Estaciones stations;
    public static int nvans;

    public int[][] vans;

    /**
     * Constructora per defecte
     */
    public BicingState() {
        vans = new int[nvans][5];
    }

    /**
     * Constructora amb parametres i una solucio inicial
     *
     * @param nest numero d'estacions
     * @param nbic numero de bicis
     * @param nv   numero de furgonetes
     * @param dem  tipus d'escenari (equilibrat/hora punta)
     * @param seed llavor pel generador de numeros aleatoris
     */
    public BicingState(int nest, int nbic, int nv, int dem, int seed) {
        nvans = nv;
        stations = new Estaciones(nest, nbic, dem, seed);
        vans = new int[nvans][5];
        trivialSolution();
    }

    private final void trivialSolution() {
        for (int i = 0; i < nvans; ++i)
        {
            vans[i][ORIG] = i;
            vans[i][DEST1] = i;
            vans[i][DEST2] = NO_STATION;
            vans[i][NBIKES1] = vans[i][NBIKES2] = 0;
        }
    }

    private final void complexSolution() {
        trivialSolution(); //TODO Shhhhhh... tu no has vist res...
    }

    /**
     * Retorna una copia de l'estat
     *
     * @return una copia de l'estat sobre el qual es crida
     */
    public BicingState copy() {
        BicingState bs = new BicingState();
        for (int i = 0; i < nvans; ++i) {
            bs.vans[i][ORIG] = vans[i][ORIG];
            bs.vans[i][DEST1] = vans[i][DEST1];
            bs.vans[i][NBIKES1] = vans[i][NBIKES1];
            bs.vans[i][DEST2] = vans[i][DEST2];
            bs.vans[i][NBIKES2] = vans[i][NBIKES2];
        }
        return bs;
    }


    // Getters

    /**
     * Retorna l'estacio d'origen de la furgoneta i
     *
     * @param i l'index de la furgoneta
     * @return l'estacio d'origen de la furgoneta
     */
    public final int getOrig(int i) {
        return vans[i][ORIG];
    }

    /**
     * Retorna l'estacio de desti (primer o segon) de la furgoneta i
     *
     * @param i    l'index de la furgoneta
     * @param dest indica primer o segon desti
     * @return l'estacio de desti (primer o segon) de la furgoneta
     */
    public final int getDest(int i, int dest) {
        return vans[i][dest];
    }

    /**
     * Retorna el numero de bicis que la furgoneta i deixa al desti
     *
     * @param i    l'index de la furgoneta
     * @param dest indica primer o segon desti
     * @return el numero de bicis que la furgoneta deixa al desti (primer o segon)
     */
    public final int getNumBikes(int i, int dest) {
        return getDest(i, dest) == NO_STATION ? 0 : vans[i][dest + 1];
    }

    public final int getNumBikesOnStation(int station) {
        return stations.get(station).getNumBicicletasNext();
    }

    public final int getStationBikesDemand(int station) {
        return stations.get(station).getDemanda();
    }

    public final int getStationUselessBikes(int station) {
        return stations.get(station).getNumBicicletasNoUsadas();
    }


    // Operadors

    /**
     * Canvia l'estacio d'origen de la furgoneta i
     *
     * @param station1 l'index de la furgoneta
     * @param station2 la nova estacio d'origen
     */
    public final void swapOrig(int station1, int station2) {
        int vanOnStation1, vanOnStation2, orig1, orig2;
        vanOnStation1 = vanOnStation2 = -1;
        for (int i = 0; i < vans.length; ++i) {
            if (getOrig(i) == station1) vanOnStation1 = i;
            else if (getOrig(i) == station2) vanOnStation2 = i;
        }

        if (vanOnStation1 != -1) vans[vanOnStation1][ORIG] = station2;
        if (vanOnStation2 != -1) vans[vanOnStation2][ORIG] = station1;
    }

    /**
     * Canvia l'estacio de desti de la furgoneta i
     *
     * @param i       l'index de la furgoneta
     * @param dest    indica primer o segon desti
     * @param station la nova estacio de desti
     */
    public final void changeDest(int i, int dest, int station) {
        vans[i][dest] = station;
    }

    /**
     * Canvia el numero de bicis que la furgoneta i deixa al desti
     *
     * @param i    l'index de la furgoneta
     * @param dest indica primer o segon desti
     * @param n    el numero de bicis que la furgoneta deixa al desti
     */
    public final void changeNumBikes(int i, int dest, int n) {
        vans[i][dest + 1] = n;
    }

    public static int getDistance(int station1, int station2)
    {
        int x1 = BicingState.stations.get(station1).getCoordX();
        int y1 = BicingState.stations.get(station1).getCoordY();
        int x2 = BicingState.stations.get(station2).getCoordX();
        int y2 = BicingState.stations.get(station2).getCoordY();

        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }

    public final String toString()
    {
        String msg = "\n";
        int[] addedBikes = new int[BicingState.stations.size()];
        int[] takenBikes = new int[BicingState.stations.size()];
        for (int i = 0; i < BicingState.nvans; ++i) {
            if(getDest(i, BicingState.DEST1)  != BicingState.NO_STATION)
            {
                addedBikes[getDest(i, BicingState.DEST1)] += getNumBikes(i, BicingState.DEST1);
            }
            if(getDest(i, BicingState.DEST2)  != BicingState.NO_STATION)
            {
                addedBikes[getDest(i, BicingState.DEST2)] += getNumBikes(i, BicingState.DEST2);
            }

            takenBikes[getOrig(i)] += getNumBikes(i, BicingState.DEST1) + getNumBikes(i, BicingState.DEST2);
        }

        msg += "Stations: \t\t\t Vans: \n";
        for(int i = 0; i < stations.size(); ++i)
        {
            msg += "Station " + i + ":\t";
            msg += "  originalBikes=" + getNumBikesOnStation(i);
            msg += ",\tfinalBikes=" + (getNumBikesOnStation(i) + addedBikes[i] - takenBikes[i]);
            msg += ",\taddedBikes=" + addedBikes[i];
            msg += ",\ttakenBikes=" + takenBikes[i];
            //msg += "\n";

            if (i < BicingState.nvans) {
                msg += "\t\t\t";
                int vansBikes1 = getNumBikes(i, BicingState.DEST1) +
                        getNumBikes(i, BicingState.DEST2);

                int dest1 = getDest(i, BicingState.DEST1), dest2 = getDest(i, BicingState.DEST2);
                double distOrigDest1 = (double) BicingState.getDistance(getOrig(i), dest1);

                if (getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                    int vansBikes2 = getNumBikes(i, BicingState.DEST2);
                    double distDest1Dest2 = (double) BicingState.getDistance(dest1, dest2);
                }

                msg += "Van " + i + ":\t ";
                msg += "  orig=" + getOrig(i) + "(" + getNumBikesOnStation(getOrig(i)) + ")";
                msg += ",\tdest1=" + getDest(i, DEST1) + "(" + getNumBikes(i, DEST1) + ")";
                msg += ",\tdest2=" + getDest(i, DEST2) + "(" + getNumBikes(i, DEST2) + ")";
                msg += "\n";
            } else {
                msg += "\n";
            }
        }

        msg += ":::::::::::::::::::::::::::::\n\n";

        return msg;
    }


}
