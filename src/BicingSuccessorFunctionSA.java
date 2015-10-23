import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BicingSuccessorFunctionSA implements SuccessorFunction {

    @Override
    public List getSuccessors(Object aState)
    {
        boolean debugOp = false;
        BicingState currentState = (BicingState) aState;

        int E = BicingState.stations.size();
        int F = BicingState.nvans;
        int probOp1 = E * (E - 1);
        int probOp2 = F * E * 2;
        int probOp3 = F * 4;
        int probOp4 = 0;
        for(int f = 0; f < BicingState.nvans; ++f)
        {
            int nBikes = Math.min(BicingState.MAX_BIKES_PER_VAN, currentState.getAvailableBikes(currentState.getOrig(f)));
            if(currentState.getDest(f, BicingState.DEST2) != BicingState.NO_STATION) probOp4 += nBikes;
            probOp4 += 1;
        }
        int probOp3Or4 = Main.USE_OP_3 ? probOp3 : probOp4;
        int totalProbs = probOp1 + probOp2 + probOp3Or4;
        int p = new Random().nextInt(totalProbs);

        ArrayList<Successor> retVal = new ArrayList<>();
        BicingHeuristicFunction HF = new BicingHeuristicFunction();

        boolean found = false;
        for (int i = 0; i < BicingState.nvans; ++i) {
            if (currentState.getAvailableBikes(i) != 0) {
                found = true;
                break;
            }
        }
        if (!found) p = new Random().nextInt(totalProbs);

        double vv = HF.getHeuristicValue(currentState);
        //System.out.println("Cost(" + vv + ") --->");
        //System.out.println(currentState.toString());

        if(debugOp) System.out.println("Prob op1: " + ((float)probOp1)/totalProbs);
        if(debugOp) System.out.println("Prob op2: " + ((float)probOp2)/totalProbs);
        if(debugOp) if(Main.USE_OP_3) System.out.println("Prob op3: "  + ((float)probOp3)/totalProbs);
        else System.out.println("Prob op4: "  + ((float)probOp4)/totalProbs);
        if(debugOp) System.out.println("p: " + ((float)p)/totalProbs);

        if (p < probOp1)
        {
            if(debugOp) System.out.println("Using op1");

            int station1 = currentState.getOrig(new Random().nextInt(F));
            int station2 = station1;
            while (station1 == station2) station2 = new Random().nextInt(E);

            // Operador 1

            BicingState newState = currentState.copy();
            newState.swapOrig(station1, station2);

            double v = HF.getHeuristicValue(newState);
            String S = BicingState.ORIG_SWAP + " (" + station1 + "," + station2 + ") Cost(" + v + ")";
            //System.out.println(S);
            retVal.add(new Successor(S, newState));
        } else if (p < probOp1 + probOp2) {
            if(debugOp) System.out.println("Using op2");
            int van = new Random().nextInt(F);
            int station = new Random().nextInt(E);
            int dest = new Random().nextBoolean() ? BicingState.DEST1 : BicingState.DEST2;

            // Operador 2
            while (currentState.getDest(van, dest) == station) station = new Random().nextInt(E);

            BicingState newState = currentState.copy();
            newState.changeDest(van, dest, station);

            double v = HF.getHeuristicValue(newState);
            String S = BicingState.DEST_CHANGE + " van(" + van + ") dest1(" + station + ") Cost(" + v + ")";
            //System.out.println(S);
            retVal.add(new Successor(S, newState));
        } else
        {
            if(Main.USE_OP_3)
            {
                if(debugOp) System.out.println("Using op3");
                // Operador 3
                // Aqui canviem les bicis que portem als destins.
                int dest = new Random().nextBoolean() ? BicingState.DEST1 : BicingState.DEST2;
                int otherDest = (dest == BicingState.DEST1 ? BicingState.DEST2 : BicingState.DEST1);
                Boolean op = new Random().nextBoolean();


                int van, vanOrig, maxBikes;
                do {
                    van = new Random().nextInt(F);
                    vanOrig = currentState.getOrig(van);
                    maxBikes = currentState.getAvailableBikes(vanOrig);
                } while (maxBikes == 0);

                int bikesToDest = currentState.getNumBikes(van, dest);
                int bikesToOtherDest = currentState.getNumBikes(van, otherDest);


                if (op) {
                    if (bikesToDest + bikesToOtherDest < maxBikes) {
                        // Portem una bici mes al desti dest
                        if (currentState.getDest(van, dest) != BicingState.NO_STATION) {
                            BicingState newState = currentState.copy();
                            newState.addBike(van, dest);

                            double v = HF.getHeuristicValue(newState);
                            String S = "Added 1 bike to DEST1 of van " + van + "(" + (bikesToDest + 1) + ")" + " Cost(" + v + ")";
                            //System.out.println(S);
                            retVal.add(new Successor(S, newState));
                        }
                    }
                } else {
                    if (bikesToDest > 0) {
                        // Portem una bici menys al desti dest
                        if (currentState.getDest(van, dest) != BicingState.NO_STATION) {
                            BicingState newState = currentState.copy();
                            newState.substractBike(van, dest);

                            double v = HF.getHeuristicValue(newState);
                            String S = "Substracted 1 bike to DEST1 of van " + van + "(" + (bikesToDest - 1) + ")" + " Cost(" + v + ")";
                            //System.out.println(S);
                            retVal.add(new Successor(S, newState));
                        }
                    }
                }
            }
            else
            {
                if(debugOp) System.out.println("Using op4");
                //Operador 4
                int van, nBikes;
                do {
                    van = new Random().nextInt(F);
                    nBikes = Math.min(BicingState.MAX_BIKES_PER_VAN, currentState.getAvailableBikes(currentState.getOrig(van)));
                }
                while ((currentState.getDest(van, BicingState.DEST2) == BicingState.NO_STATION
                        && currentState.getNumBikes(van, BicingState.DEST1) == nBikes)
                        || nBikes <= 0);

                if (currentState.getDest(van, BicingState.DEST2) != BicingState.NO_STATION) {
                    int bikesToDest1 = new Random().nextInt(nBikes + 1);
                    int bikesToDest2 = nBikes - bikesToDest1;

                    BicingState newState = currentState.copy();
                    newState.changeNumBikes(van, BicingState.DEST1, bikesToDest1);
                    newState.changeNumBikes(van, BicingState.DEST2, bikesToDest2);

                    double v = HF.getHeuristicValue(newState);
                    String S = "van " + van + " bikes to DEST1 (" + bikesToDest1 + ") bikes to DEST2 (" + bikesToDest2 + ")" + " Cost(" + v + ")";
                    retVal.add(new Successor(S, newState));
                } else if (currentState.getNumBikes(van, BicingState.DEST1) != nBikes) {
                    BicingState newState = currentState.copy();
                    newState.changeNumBikes(van, BicingState.DEST1, nBikes);

                    int bikesToDest1 = newState.getNumBikes(van, BicingState.DEST1);
                    int bikesToDest2 = newState.getNumBikes(van, BicingState.DEST2);
                    double v = HF.getHeuristicValue(newState);
                    String S = "van " + van + " bikes to DEST1 (" + bikesToDest1 + ") bikes to DEST2 (" + bikesToDest2 + ")" + " Cost(" + v + ")";
                    retVal.add(new Successor(S, newState));
                }
            }
            //System.out.println("########################################################\n");
        }
        return retVal;
    }

}
