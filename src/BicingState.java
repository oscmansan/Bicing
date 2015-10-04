import IA.Bicing.Estaciones;

public class BicingState {

    public static Estaciones estaciones;
    public static int nFurgos;
    public int[][] furgos;
    /*
      [0] Estacio d'origen
      [1] Estacio de desti  (1)
      [2] Numero de bicis a (1)
      [3] Estacio de desti  (2)
      [4] Numero de bicis a (2)

      */
    public BicingState(int nest, int nbic, int dem, int seed, int nf) {
        nFurgos = nf;
        estaciones = new Estaciones(nest, nbic, dem, seed);
    }

    public BicingState() {
        furgos = new int[nFurgos][5];
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
}
