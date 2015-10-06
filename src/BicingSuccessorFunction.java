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
                newState.swapOrig(i,j);

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
                    newState.changeDest(i,BicingState.DEST1,j);

                    double v = HF.getHeuristicValue(newState);
                    String S = BicingState.DEST_CHANGE + " van: " + i + " dest1: " + j + " Coste(" + v + ") ---> " + newState.toString();

                    retVal.add(new Successor(S, newState));
                }
                if (currentState.getDest(i, BicingState.DEST2) != j) {
                    BicingState newState = currentState.copy();
                    newState.changeDest(i,BicingState.DEST2,j);

                    double v = HF.getHeuristicValue(newState);
                    String S = BicingState.DEST_CHANGE + " van: " + i + " dest2: " + j + " Coste(" + v + ") ---> " + newState.toString();

                    retVal.add(new Successor(S, newState));
                }
            }
        }

        return retVal;
    }

}
