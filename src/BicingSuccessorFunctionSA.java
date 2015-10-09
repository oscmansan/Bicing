import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BicingSuccessorFunctionSA implements SuccessorFunction {

    @Override
    public List getSuccessors(Object aState)
    {
        int E = BicingState.stations.size();
        int F = BicingState.nvans;
        int probOp1 = E * (E-1);
        int probOp2 = F * E * 2;
        int probOp3 = 4 * F;
        int totalProbs = probOp1 + probOp2 + probOp3;
        int p = new Random().nextInt(totalProbs);

        ArrayList<Successor> retVal = new ArrayList<>();
        BicingHeuristicFunction HF = new BicingHeuristicFunction();

        BicingState currentState = (BicingState) aState;

        double vv = HF.getHeuristicValue(currentState);
        System.out.println("Cost(" + vv + ") --->");
        System.out.println(currentState.toString());
        System.out.println("########################################################\n");

        if(p < probOp1) {
            int station1 = new Random().nextInt(E);
            int station2 = station1;
            while(station1 == station2) station2 = new Random().nextInt(E);

            // Operador 1

            BicingState newState = currentState.copy();
            newState.swapOrig(station1, station2);

            double v = HF.getHeuristicValue(newState);
            String S = BicingState.ORIG_SWAP + " (" + station1 + "," + station2 + ") Cost(" + v + ")";

            retVal.add(new Successor(S, newState));
        }
        else if(p < probOp2) {
            int van = new Random().nextInt(F);
            int station = new Random().nextInt(E);
            int dest = new Random().nextBoolean() ? BicingState.DEST1 : BicingState.DEST2;

            // Operador 2
            while (currentState.getDest(van, dest) == station) station = new Random().nextInt(E);

            BicingState newState = currentState.copy();
            newState.changeDest(van, dest, station);

            double v = HF.getHeuristicValue(newState);
            String S = BicingState.DEST_CHANGE + " van(" + van + ") dest1(" + station + ") Cost(" + v + ")";

            retVal.add(new Successor(S, newState));
        }
        else {/*
            // Operador 3
            // Aqui canviem les bicis que portem als destins.
            int van = new Random().nextInt(F);
            int dest = new Random().nextBoolean() ? BicingState.DEST1 : BicingState.DEST2;
            int otherDest = (dest == BicingState.DEST1 ? BicingState.DEST2 : BicingState.DEST1);

            int bikesToDest = currentState.getNumBikes(van, dest);
            int bikesToOtherDest = currentState.getNumBikes(van, otherDest);

            int vanOrig = currentState.getOrig(van);
            int maxBikes = Math.min(BicingState.MAX_BIKES_PER_VAN, currentState.getNumBikesNext(vanOrig));

                if (bikesToDest + bikesToOtherDest < maxBikes) {
                    // Portem una bici mes al desti1 (DEST1)
                    if (currentState.getDest(van, BicingState.DEST1) != BicingState.NO_STATION) {
                        BicingState newState = currentState.copy();
                        newState.addBike(van, BicingState.DEST1);

                        double v = HF.getHeuristicValue(newState);
                        String S = "Added 1 bike to DEST1 of van " + i + "(" + (bikesToDest1 + 1) + ")" + " Cost(" + v + ")";
                        retVal.add(new Successor(S, newState));
                    }

                    // Portem una bici mes al desti2 (DEST2)
                    if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                        BicingState newState = currentState.copy();
                        newState.addBike(i, BicingState.DEST2);

                        double v = HF.getHeuristicValue(newState);
                        String S = "Added 1 bike to DEST2 of van " + i + "(" + (bikesToDest2 + 1) + ")" + " Cost(" + v + ")";
                        retVal.add(new Successor(S, newState));
                    }
                }

                if (bikesToDest1 > 0) {
                    // Portem una bici menys al desti1 (DEST1)
                    if (currentState.getDest(i, BicingState.DEST1) != BicingState.NO_STATION) {
                        BicingState newState = currentState.copy();
                        newState.substractBike(i, BicingState.DEST1);

                        double v = HF.getHeuristicValue(newState);
                        String S = "Substracted 1 bike to DEST1 of van " + i + "(" + (bikesToDest1 - 1) + ")" + " Cost(" + v + ")";
                        retVal.add(new Successor(S, newState));
                    }
                }

                if (bikesToDest2 > 0) {
                    // Portem una bici menys al desti2 (DEST2)
                    if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                        BicingState newState = currentState.copy();
                        newState.substractBike(i, BicingState.DEST2);

                        double v = HF.getHeuristicValue(newState);
                        String S = "Substracted 1 bike to DEST2 of van " + i + "(" + (bikesToDest2 - 1) + ")" + " Cost(" + v + ")";
                        retVal.add(new Successor(S, newState));
                    }
                }
            }
*/
        }

        return retVal;
    }

}
