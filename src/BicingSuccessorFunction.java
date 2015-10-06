import IA.Bicing.Estacion;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class BicingSuccessorFunction implements SuccessorFunction {

    @Override
    public List getSuccessors(Object aState) {
        BicingState currentState = (BicingState) aState;
        ArrayList<Successor> retVal = new ArrayList<>();
        BicingHeuristicFunction HF = new BicingHeuristicFunction();

        // Operador 1
        for (int i = 0; i < BicingState.stations.size(); ++i) {
            for (int j = 0; j < BicingState.stations.size(); ++j) {
                if (i == j) continue;
                BicingState newState = currentState.copy();
                newState.swapOrig(i, j);

                double v = HF.getHeuristicValue(newState);
                String S = BicingState.ORIG_SWAP + " " + i + " " + j + " Coste(" + v + ") ---> " + newState.toString();

                retVal.add(new Successor(S, newState));
            }
        }

        // Operador 2
        for (int i = 0; i < BicingState.nvans; ++i) {
            for (int j = 0; j < BicingState.stations.size(); ++j) {
                if (currentState.getDest(i, BicingState.DEST1) != j) {
                    BicingState newState = currentState.copy();
                    newState.changeDest(i, BicingState.DEST1, j);

                    double v = HF.getHeuristicValue(newState);
                    String S = BicingState.DEST_CHANGE + " van: " + i + " dest1: " + j + " Coste(" + v + ") ---> " + newState.toString();

                    retVal.add(new Successor(S, newState));
                }
                if (currentState.getDest(i, BicingState.DEST2) != j) {
                    BicingState newState = currentState.copy();
                    newState.changeDest(i, BicingState.DEST2, j);

                    double v = HF.getHeuristicValue(newState);
                    String S = BicingState.DEST_CHANGE + " van: " + i + " dest2: " + j + " Coste(" + v + ") ---> " + newState.toString();

                    retVal.add(new Successor(S, newState));
                }
            }
        }

        // Operador 3
        // Aqui canviem les bicis que portem als destins.
        for (int i = 0; i < BicingState.nvans; ++i) {
            int bikesToDest1 = currentState.getNumBikes(i, BicingState.DEST1);
            int bikesToDest2 = currentState.getNumBikes(i, BicingState.DEST2);

            int vanOrig = currentState.getOrig(i);
            int maxBikes = Math.min(BicingState.MAX_BIKES_PER_VAN, currentState.getNumBikesOnStation(vanOrig));

            if (bikesToDest1 + bikesToDest2 < maxBikes) {
                // Portem una bici mes al desti1 (DEST1)
                if (currentState.getDest(i, BicingState.DEST1) != BicingState.NO_STATION) {
                    BicingState newState = currentState.copy();
                    newState.changeNumBikes(i, BicingState.DEST1, bikesToDest1 + 1);
                    String S = "Added 1 bike to DEST1 of van " + i + "(" + (bikesToDest1 + 1) + ")";
                    retVal.add(new Successor(S, newState));
                }

                // Portem una bici mes al desti2 (DEST2)
                if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                    BicingState newState = currentState.copy();
                    newState.changeNumBikes(i, BicingState.DEST2, bikesToDest2 + 1);
                    String S2 = "Added 1 bike to DEST2 of van " + i + "(" + (bikesToDest2 + 1) + ")";
                    retVal.add(new Successor(S2, newState));
                }
            }


            if (bikesToDest1 > 0) {
                // Portem una bici menys al desti1 (DEST1)
                if (currentState.getDest(i, BicingState.DEST1) != BicingState.NO_STATION) {
                    BicingState newState = currentState.copy();
                    newState.changeNumBikes(i, BicingState.DEST1, bikesToDest1 - 1);
                    String S = "Substracted 1 bike to DEST1 of van " + i + "(" + (bikesToDest1 - 1) + ")";
                    retVal.add(new Successor(S, newState));
                }
            }

            if (bikesToDest2 > 0) {
                // Portem una bici menys al desti2 (DEST2)
                if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                    BicingState newState = currentState.copy();
                    newState.changeNumBikes(i, BicingState.DEST2, bikesToDest2 - 1);
                    String S = "Substracted 1 bike to DEST2 of van " + i + "(" + (bikesToDest2 - 1) + ")";
                    retVal.add(new Successor(S, newState));
                }
            }
        }


        return retVal;
    }

}
