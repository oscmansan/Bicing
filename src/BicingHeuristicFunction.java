import aima.search.framework.HeuristicFunction;

public class BicingHeuristicFunction implements HeuristicFunction {

    @Override
    public double getHeuristicValue(Object o)
    {
        BicingState currentState = (BicingState)o;
        double cost = 0;

        //Calculate the bikes stuff
        //Get the imported bikes to every station
        int[] addedBikes = new int[BicingState.stations.size()];
        int[] takenBikes = new int[BicingState.stations.size()];
        for (int i = 0; i < BicingState.nvans; ++i) {
            if(currentState.getDest(i, BicingState.DEST1)  != BicingState.NO_STATION)
            {
                addedBikes[currentState.getDest(i, BicingState.DEST1)] += currentState.getNumBikes(i, BicingState.DEST1);
            }
            if(currentState.getDest(i, BicingState.DEST2)  != BicingState.NO_STATION)
            {
                addedBikes[currentState.getDest(i, BicingState.DEST2)] += currentState.getNumBikes(i, BicingState.DEST2);
            }

            takenBikes[currentState.getOrig(i)] += currentState.getNumBikes(i, BicingState.DEST1) + currentState.getNumBikes(i, BicingState.DEST2);
        }

        for(int i = 0; i < BicingState.stations.size(); ++i)
        {
            int demandedBikes = currentState.getDemand(i);
            int currentBikes = currentState.getNumBikesNext(i);

            int aportation = Math.abs((currentBikes + (addedBikes[i] - takenBikes[i])) - demandedBikes);
            cost += aportation;
        }
        //


        //Calculate the vans travel stuff
        for (int i = 0; i < BicingState.nvans; ++i) {
            int vansBikes1 = currentState.getNumBikes(i, BicingState.DEST1) +
                             currentState.getNumBikes(i, BicingState.DEST2);

            int dest1 = currentState.getDest(i, BicingState.DEST1), dest2 = currentState.getDest(i, BicingState.DEST2);
            double distOrigDest1  = (double) BicingState.getDistance(currentState.getOrig(i), dest1);
            cost += ((vansBikes1 + 9) / 10) * (distOrigDest1 / 1000.0);

            if(currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                int vansBikes2 = currentState.getNumBikes(i, BicingState.DEST2);
                double distDest1Dest2 = (double) BicingState.getDistance(dest1, dest2);
                cost += ((vansBikes2 + 9) / 10) * (distDest1Dest2 / 1000.0);
            }
        }
        //

        return cost;
    }

}
