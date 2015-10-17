import IA.Bicing.Estaciones;
import IA.TSP2.ProbTSPGoalTest;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Main {

    private static final int nest = 25;
    private final static int nbic = 1250;
    private final static int nf = 5;
    private static final int dem = Estaciones.EQUILIBRIUM;
    private static final int seed = 1234;

    public static void main(String args[]) {
        double t0 = System.currentTimeMillis();
        BicingState BS = new BicingState(nest, nbic, nf, dem, seed);
        BicingHillClimbingSearch(BS);
        //BicingSimulatedAnnealingSearch(BS);
        System.out.println("\ntime = " + (System.currentTimeMillis()-t0) + " ms");
    }

    private static void BicingHillClimbingSearch(BicingState BS)
    {
        System.out.println("\nBicing HillClimbing  -->");
        try {
            Problem problem = new Problem(BS, new BicingSuccessorFunction(), new ProbTSPGoalTest(), new BicingHeuristicFunction());
            Search search = new HillClimbingSearch();
            System.out.println();
            SearchAgent agent = new SearchAgent(problem, search);

            BicingState finalState;
            if (search.getPathStates().size() > 0)
                finalState = (BicingState) search.getPathStates().get(search.getPathStates().size() - 1);
            else
                finalState = BS;
            System.out.println("FINAL STATE");
            BicingHeuristicFunction HF = new BicingHeuristicFunction();
            double vv = HF.getHeuristicValue(finalState);
            double rc = HF.getRealCost(finalState);
            System.out.println("Heuristic(" + vv + ")");
            System.out.println("RealCost(" + rc + ")");
            System.out.println("Money(" + finalState.getMoney() + " €)");
            System.out.println("Distance(" + finalState.getTotalDistance() + " km)");
            System.out.println(finalState.toString());
            System.out.println("########################################################\n");

            printActions(agent.getActions());
            System.out.println();
            printInstrumentation(agent.getInstrumentation());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void BicingSimulatedAnnealingSearch(BicingState BS) {

        System.out.println("\nBicing SimulatedAnnealing  -->");
        try {
            Problem problem =  new Problem(BS,new BicingSuccessorFunctionSA(), new ProbTSPGoalTest(), new BicingHeuristicFunction());
            Search search =  new SimulatedAnnealingSearch(2000,100,5,0.001);
            SearchAgent agent = new SearchAgent(problem,search);
            printActions(agent.getActions());  //ES RALLA
            System.out.println();
            printInstrumentation(agent.getInstrumentation()); //ES RALLA (?)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }

}
