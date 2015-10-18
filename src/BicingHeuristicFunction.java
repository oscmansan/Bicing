import aima.search.framework.HeuristicFunction;

public class BicingHeuristicFunction implements HeuristicFunction {
    @Override
    public double getHeuristicValue(Object o) {
        BicingState currentState = (BicingState) o;
        double cost = 0;

        cost += bikeTransfersCost(currentState);
        cost += robinHoodCost(currentState);
        if(!Main.FREE_TRANSPORT) cost += vansTravelCost(currentState);

        return cost;
    }

    /**
     * Get the cost due to bike transfers
     * @return  the cost due to bike transfers
     */
    public double bikeTransfersCost(BicingState currentState) {
        int[] addedBikes = new int[BicingState.stations.size()];
        int[] takenBikes = new int[BicingState.stations.size()];
        double cost = 0;
        for (int i = 0; i < BicingState.nvans; ++i) {
            addedBikes[currentState.getDest(i, BicingState.DEST1)] += currentState.getNumBikes(i, BicingState.DEST1);
            if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION)
                addedBikes[currentState.getDest(i, BicingState.DEST2)] += currentState.getNumBikes(i, BicingState.DEST2);
            takenBikes[currentState.getOrig(i)] += currentState.getTakenBikes(i);
        }

        for (int i = 0; i < BicingState.stations.size(); ++i) {
            int demandedBikes = currentState.getDemand(i);
            int predictedBikes = currentState.getNumBikesNext(i);

            cost += Math.abs((predictedBikes + (addedBikes[i] - takenBikes[i])) - demandedBikes);
        }
        return cost;
    }

    /**
     * The Robin Hood vans; punish those who 'steal' from 'poorer' stations (with less available bikes) while reward
     * those who 'steal' from 'richer' stations (with more available bikes)
     * @return  the cost after reward vans that take bikes from stations where there are more of them available
     */
    public double robinHoodCost(BicingState currentState) {
        double cost = 0;
        double robinConst = 20.0;
        for (int i = 0; i < BicingState.nvans; ++i)
        {
            int origin = currentState.getOrig(i);

            // 1.0 means more available bikes, 0.0 means less available bikes
            float positionPercent = ((float)BicingState.stationsByAvailableBikesIndices[origin]) / ((float)BicingState.stations.size());
            cost -= (1.0 - positionPercent) * robinConst;
        }

        return cost;
    }

    /**
     * Get the cost due to vans travel
     * @return  the cost due to vans travel
     */
    public double vansTravelCost(BicingState currentState) {
        double cost = 0.0;
        for (int i = 0; i < BicingState.nvans; ++i) {
            int dest1 = currentState.getDest(i, BicingState.DEST1);
            int dest2 = currentState.getDest(i, BicingState.DEST2);

            int bikesUntilDest1 = currentState.getNumBikes(i, BicingState.DEST1) + currentState.getNumBikes(i, BicingState.DEST2);
            double distOrigDest1 = (double) BicingState.getDistance(currentState.getOrig(i), dest1);
            cost += ((bikesUntilDest1 + 9) / 10) * (distOrigDest1 / 1000.0);

            if (dest2 != BicingState.NO_STATION) {
                int bikesUntilDest2 = currentState.getNumBikes(i, BicingState.DEST2);
                double distDest1Dest2 = (double) BicingState.getDistance(dest1, dest2);
                cost += ((bikesUntilDest2 + 9) / 10) * (distDest1Dest2 / 1000.0);
            }
        }
        return cost;
    }

    public double getRealCost(BicingState currentState) {
        return bikeTransfersCost(currentState) + vansTravelCost(currentState);
    }

    public double getHeuristicValueForStation(BicingState currentState, int station) {
        int[] addedBikes = new int[BicingState.stations.size()];
        int[] takenBikes = new int[BicingState.stations.size()];
        for (int i = 0; i < BicingState.nvans; ++i) {
            if (currentState.getDest(i, BicingState.DEST1) != BicingState.NO_STATION) {
                addedBikes[currentState.getDest(i, BicingState.DEST1)] += currentState.getNumBikes(i, BicingState.DEST1);
            }
            if (currentState.getDest(i, BicingState.DEST2) != BicingState.NO_STATION) {
                addedBikes[currentState.getDest(i, BicingState.DEST2)] += currentState.getNumBikes(i, BicingState.DEST2);
            }

            takenBikes[currentState.getOrig(i)] += currentState.getNumBikes(i, BicingState.DEST1) + currentState.getNumBikes(i, BicingState.DEST2);
        }

        int demandedBikes = currentState.getDemand(station);
        int currentBikes = currentState.getNumBikesNext(station);

        return Math.abs((currentBikes + (addedBikes[station] - takenBikes[station])) - demandedBikes);
    }

    public double getHeuristicValueForVanTravel(BicingState currentState, int van) {
        double travelCost = 0.0;

        int vansBikes1 = currentState.getNumBikes(van, BicingState.DEST1) +
                currentState.getNumBikes(van, BicingState.DEST2);

        int dest1 = currentState.getDest(van, BicingState.DEST1), dest2 = currentState.getDest(van, BicingState.DEST2);
        double distOrigDest1 = (double) BicingState.getDistance(currentState.getOrig(van), dest1);
        travelCost += ((vansBikes1 + 9) / 10) * (distOrigDest1 / 1000.0);

        if (currentState.getDest(van, BicingState.DEST2) != BicingState.NO_STATION) {
            int vansBikes2 = currentState.getNumBikes(van, BicingState.DEST2);
            double distDest1Dest2 = (double) BicingState.getDistance(dest1, dest2);
            travelCost += ((vansBikes2 + 9) / 10) * (distDest1Dest2 / 1000.0);
        }

        return travelCost;
    }
}
