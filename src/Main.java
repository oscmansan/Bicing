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

public class Main
{
    private static final int nest = 25*7;
    private final static int nbic = nest * 50;
    private final static int nf = nest / 5;
    private static final int dem = Estaciones.EQUILIBRIUM;
    private static final int seed = 1234;

    public static boolean USE_OP_3                     = false;
    public static boolean USE_TRIVIAL_INITIAL_SOLUTION = false;
    public static boolean FREE_TRANSPORT               = true;
    public static boolean USE_HILL_CLIMBING            = true;
    private static final int NUM_EXPERIMENTS = 20;

    private static double eurosAverage = 0.0;

    public static void main(String args[])
    {
        for(int i = 0; i < NUM_EXPERIMENTS; ++i)
        {
            double t0 = System.currentTimeMillis();
            BicingState BS = new BicingState(nest, nbic, nf, dem, seed);
            if(USE_HILL_CLIMBING) BicingHillClimbingSearch(BS);
            else BicingSimulatedAnnealingSearch(BS);
            //System.out.println("\ntime = " + (System.currentTimeMillis()-t0) + " ms");
            System.out.println((System.currentTimeMillis()-t0));
        }
        System.out.println("Euros average: " + eurosAverage);
    }

    private static void BicingHillClimbingSearch(BicingState BS)
    {
        //System.out.println("\nBicing HillClimbing  -->");
        try {
            Problem problem = new Problem(BS, new BicingSuccessorFunction(), new ProbTSPGoalTest(), new BicingHeuristicFunction());
            Search search = new HillClimbingSearch();
            //System.out.println();
            SearchAgent agent = new SearchAgent(problem, search);

            BicingState finalState;
            if (search.getPathStates().size() > 0) finalState = (BicingState) search.getPathStates().get(search.getPathStates().size() - 1);
            else finalState = BS;

            //System.out.println("FINAL STATE");
            BicingHeuristicFunction HF = new BicingHeuristicFunction();
            double vv = HF.getHeuristicValue(finalState);
            double rc = HF.getRealCost(finalState);

            //System.out.println("Heuristic(" + vv + ")");
            //System.out.println("RealCost(" + rc + ")");
            //System.out.println("Money(" + finalState.getMoney() + " EURUS)");
            //System.out.println("Distance(" + finalState.getTotalDistance() + " km)");
            //System.out.println(finalState.toString());
            //System.out.println("########################################################\n");

            //System.out.print(vv + "\t");
           // System.out.print(rc + "\t");
            //System.out.print(finalState.getMoney() + "\t");
            //System.out.print(finalState.getTotalDistance() + "\t");
            //System.out.print("Expanded nodes(" + search.getPathStates().size() + ")\t");

            eurosAverage += ((float)finalState.getMoney()) / NUM_EXPERIMENTS;

            //printActions(agent.getActions());
            //System.out.println();
            //printInstrumentation(agent.getInstrumentation());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void BicingSimulatedAnnealingSearch(BicingState BS) {

        try {
            Problem problem =  new Problem(BS,new BicingSuccessorFunctionSA(), new ProbTSPGoalTest(), new BicingHeuristicFunction());
            Search search =  new SimulatedAnnealingSearch(1500, 100, 5, 0.0001);
            SearchAgent agent = new SearchAgent(problem,search);
            //printActions(agent.getActions());
            //System.out.println();
            //printInstrumentation(agent.getInstrumentation());

            BicingState finalState;
            if (search.getPathStates().size() > 0) finalState = (BicingState) search.getPathStates().get(search.getPathStates().size() - 1);
            else finalState = BS;

            BicingHeuristicFunction HF = new BicingHeuristicFunction();
            double vv = HF.getHeuristicValue(finalState);
            double rc = HF.getRealCost(finalState);

            System.out.print(vv + "\t");
            System.out.print(rc + "\t");
            System.out.print(finalState.getMoney() + "\t");
            System.out.print(finalState.getTotalDistance() + "\t");
            //System.out.println(finalState.toString());
            //System.out.print("Expanded nodes(" + search.getPathStates().size() + ")\t");

            eurosAverage += ((float)finalState.getMoney()) / NUM_EXPERIMENTS;

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
