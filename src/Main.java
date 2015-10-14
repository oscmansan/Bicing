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
    private static final int seed = 0;

    public static void main(String args[]) {
        BicingState BS = new BicingState(nest, nbic, nf, dem, seed);
        //BicingHillClimbingSearch(BS);
        BicingSimulatedAnnealingSearch(BS);
    }

    private static void BicingHillClimbingSearch(BicingState BS)
    {
        System.out.println("\nBicing HillClimbing  -->");
        try {
            Problem problem = new Problem(BS, new BicingSuccessorFunction(), new ProbTSPGoalTest(), new BicingHeuristicFunction());
            Search search = new HillClimbingSearch();
            System.out.println();
            SearchAgent agent = new SearchAgent(problem, search);

            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
            /*if (search.getPathStates().size() > 0) {
                BicingHeuristicFunction HF = new BicingHeuristicFunction();
                BicingState initialState = (BicingState)problem.getInitialState();
                System.out.println("*******************************************");
                System.out.println("INITIAL STATE:************************** Coste(" + HF.getHeuristicValue(initialState) + ")");
                System.out.println(initialState.toString());
                System.out.println("*******************************************");
                BicingState finalState = (BicingState)search.getPathStates().get(search.getPathStates().size() - 1);
                System.out.println("FINAL STATE:************************** Coste(" + HF.getHeuristicValue(finalState) + ")");
                System.out.println(finalState.toString());
                System.out.println("*******************************************");
            }*/
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void BicingSimulatedAnnealingSearch(BicingState BS) {

        System.out.println("\nBicing SimulatedAnnealing  -->");
        try {
            Problem problem =  new Problem(BS,new BicingSuccessorFunctionSA(), new ProbTSPGoalTest(), new BicingHeuristicFunction());
            Search search =  new SimulatedAnnealingSearch(2000,100,5,0.001);
            SearchAgent agent = new SearchAgent(problem,search);
            //System.out.println();
            //printActions(agent.getActions());  //ES RALLA
            //printInstrumentation(agent.getInstrumentation()); //ES RALLA (?)
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
