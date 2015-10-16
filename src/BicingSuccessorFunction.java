import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class BicingSuccessorFunction implements SuccessorFunction {

    @Override
    public List getSuccessors(Object aState) {

        ArrayList<Successor> retVal = new ArrayList<>();
        BicingHeuristicFunction HF = new BicingHeuristicFunction();

        BicingState currentState = (BicingState) aState;

        double vv = HF.getHeuristicValue(currentState);
        double rc = HF.getRealCost(currentState);
        System.out.println("Heuristic(" + vv + ")");
        System.out.println("RealCost(" + rc + ")");
        System.out.println("Money(" + currentState.getMoney() + " €)");
        System.out.println("Distance(" + currentState.getTotalDistance() + " km)");
        System.out.println(currentState.toString());
        System.out.println("########################################################\n");

        // Operator 1 (swap origins)
        for (int i = 0; i < BicingState.stations.size(); ++i) {
            for (int j = 0; j < BicingState.stations.size(); ++j) {
                if (i == j) continue;
                BicingState newState = currentState.copy();
                if (newState.swapOrig(i, j)) {    // Could not change the state
                    double v = HF.getHeuristicValue(newState);
                    String S = BicingState.ORIG_SWAP + " (" + i + "," + j + ") Cost(" + v + ")";

                    retVal.add(new Successor(S, newState));
                }
            }
        }

        // Operator 2 (change destinations)
        for (int i = 0; i < BicingState.nvans; ++i) {
            for (int j = 0; j < BicingState.stations.size(); ++j) {
                if (currentState.getDest(i, BicingState.DEST1) != j) {
                    BicingState newState = currentState.copy();
                    newState.changeDest(i, BicingState.DEST1, j);

                    double v = HF.getHeuristicValue(newState);
                    String S = BicingState.DEST_CHANGE + " van(" + i + ") dest1(" + j + ") Cost(" + v + ")";

                    retVal.add(new Successor(S, newState));
                }
            }

            // Starts from -1, taking into account that DEST2 can be empty (NO_STATION)
            for (int j = -1; j < BicingState.stations.size(); ++j) {
                if (currentState.getDest(i, BicingState.DEST2) != j) {
                    BicingState newState = currentState.copy();
                    newState.changeDest(i, BicingState.DEST2, (j == -1 ? BicingState.NO_STATION : j));

                    double v = HF.getHeuristicValue(newState);
                    String S = BicingState.DEST_CHANGE + " van(" + i + ") dest2(" + j + ") Cost(" + v + ")";

                    retVal.add(new Successor(S, newState));
                }
            }
        }

        /*// Operator 3
        // Here we change the num of bikes the vans leave at destination
        for (int i = 0; i < BicingState.nvans; ++i) {
            int bikesToDest1 = currentState.getNumBikes(i, BicingState.DEST1);
            int bikesToDest2 = currentState.getNumBikes(i, BicingState.DEST2);

            int vanOrig = currentState.getOrig(i);
            int maxBikes = currentState.getAvailableBikes(vanOrig);

            if (bikesToDest1 + bikesToDest2 < maxBikes) {
                // We leave one more bike at DEST1
                if (currentState.getDest(i, BicingState.DEST1) != BicingState.NO_STATION) {
                    BicingState newState = currentState.copy();
                    newState.addBike(i, BicingState.DEST1);

                    double v = HF.getHeuristicValue(newState);
                    String S = "Added 1 bike to DEST1 of van " + i + "(" + (bikesToDest1 + 1) + ")" + " Cost(" + v + ")";
                    retVal.add(new Successor(S, newState));
                }

                // We leave one more bike at DEST2
                if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                    BicingState newState = currentState.copy();
                    newState.addBike(i, BicingState.DEST2);

                    double v = HF.getHeuristicValue(newState);
                    String S = "Added 1 bike to DEST2 of van " + i + "(" + (bikesToDest2 + 1) + ")" + " Cost(" + v + ")";
                    retVal.add(new Successor(S, newState));
                }
            }

            if (bikesToDest1 > 0) {
                // We leave one bike less at DEST1
                if (currentState.getDest(i, BicingState.DEST1) != BicingState.NO_STATION) {
                    BicingState newState = currentState.copy();
                    newState.substractBike(i, BicingState.DEST1);

                    double v = HF.getHeuristicValue(newState);
                    String S = "Substracted 1 bike to DEST1 of van " + i + "(" + (bikesToDest1 - 1) + ")" + " Cost(" + v + ")";
                    retVal.add(new Successor(S, newState));
                }
            }

            if (bikesToDest2 > 0) {
                // We leave one bike less at DEST2
                if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                    BicingState newState = currentState.copy();
                    newState.substractBike(i, BicingState.DEST2);

                    double v = HF.getHeuristicValue(newState);
                    String S = "Substracted 1 bike to DEST2 of van " + i + "(" + (bikesToDest2 - 1) + ")" + " Cost(" + v + ")";
                    retVal.add(new Successor(S, newState));
                }
            }
        }*/

        // Operator 4 (change the num of bikes)
        for (int i = 0; i < BicingState.nvans; ++i) {
            int n = Math.min(BicingState.MAX_BIKES_PER_VAN, currentState.getAvailableBikes(currentState.getOrig(i)));
            if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                for (int j = 0; j <= n; ++j) {
                    if (currentState.getNumBikes(i, BicingState.DEST1) != j) {
                        BicingState newState = currentState.copy();
                        newState.changeNumBikes(i, BicingState.DEST1, j);
                        newState.changeNumBikes(i, BicingState.DEST2, n - j);

                        int bikesToDest1 = newState.getNumBikes(i, BicingState.DEST1);
                        int bikesToDest2 = newState.getNumBikes(i, BicingState.DEST2);
                        double v = HF.getHeuristicValue(newState);
                        String S = "van " + i + " bikes to DEST1 (" + bikesToDest1 + ") bikes to DEST2 (" + bikesToDest2 + ")" + " Cost(" + v + ")";

                        retVal.add(new Successor(S, newState));
                    }
                }
            } else if (currentState.getNumBikes(i, BicingState.DEST1) != n) {
                BicingState newState = currentState.copy();
                newState.changeNumBikes(i, BicingState.DEST1, n);

                int bikesToDest1 = newState.getNumBikes(i, BicingState.DEST1);
                int bikesToDest2 = newState.getNumBikes(i, BicingState.DEST2);
                double v = HF.getHeuristicValue(newState);
                String S = "van " + i + " bikes to DEST1 (" + bikesToDest1 + ") bikes to DEST2 (" + bikesToDest2 + ")" + " Cost(" + v + ")";

                retVal.add(new Successor(S, newState));
            }
        }

        return retVal;
    }

}
