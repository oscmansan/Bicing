import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class BicingSuccessorFunction implements SuccessorFunction {

    @Override
    public List getSuccessors(Object aState) {
        BicingState currentState = (BicingState) aState;
        ArrayList retVal = new ArrayList();
        BicingHeuristicFunction HF = new BicingHeuristicFunction();

        return retVal;
    }

}
