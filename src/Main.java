import IA.Bicing.Estaciones;
import IA.TSP2.ProbTSPGoalTest;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.*;

public class Main {
    private static int nest = 25;
    private static int nbic = 1250;
    private static int nf = 20;
    private static int dem = Estaciones.EQUILIBRIUM;
    private static int seed = 1234;

    private static int saIter = 100000;
    private static int saStIter = 1000;
    private static int saK = 100000;
    private static double saLambda = 0.001;


    public static boolean USE_OP_3 = false;
    public static boolean USE_TRIVIAL_INITIAL_SOLUTION = false;
    public static boolean FREE_TRANSPORT = false;
    public static boolean USE_HILL_CLIMBING = true;

    private static int NUM_EXPERIMENTS = 10;

    private static double eurosAverage = 0.0;

    private static String results;

    public static void main(String args[]) {

        int i = 0;
        String arg;
        while (i < args.length) {
            arg = args[i++];
            if (arg.startsWith("-")) {
                switch (arg.substring(1)) {
                    case "nest":
                        if (isNumber(args[i])) nest = Integer.parseInt(args[i++]);
                        else  {
                            System.out.println("\"" + args[i] + "\" is not a number, after \"" + arg + "\"");
                            usage();
                        }
                        break;
                    case "nbic":
                        if (isNumber(args[i])) nbic = Integer.parseInt(args[i++]);
                        else  {
                            System.out.println("\"" + args[i] + "\" is not a number, after \"" + arg + "\"");
                            usage();
                        }
                        break;
                    case "nf":
                        if (isNumber(args[i])) nf = Integer.parseInt(args[i++]);
                        else  {
                            System.out.println("\"" + args[i] + "\" is not a number, after \"" + arg + "\"");
                            usage();
                        }
                        break;
                    case "dem":
                        String a = args[i++];
                        if (a.equals("E")) dem = Estaciones.EQUILIBRIUM;
                        else if (a.equals("R")) dem = Estaciones.RUSH_HOUR;
                        else usage();
                        break;
                    case "seed":
                        if (isNumber(args[i])) seed = Integer.parseInt(args[i++]);
                        else  {
                            System.out.println("\"" + args[i] + "\" is not a number, after \"" + arg + "\"");
                            usage();
                        }
                        break;
                    case "nexp":
                        if (isNumber(args[i])) NUM_EXPERIMENTS = Integer.parseInt(args[i++]);
                        else  {
                            System.out.println("\"" + args[i] + "\" is not a number, after \"" + arg + "\"");
                            usage();
                        }
                        break;
                    case "op3":
                        USE_OP_3 = true;
                        break;
                    case "trivial":
                        USE_TRIVIAL_INITIAL_SOLUTION = true;
                        break;
                    case "gratis":
                        FREE_TRANSPORT = true;
                        break;
                    case "alg":
                        String alg = args[i++];
                        if (alg.equals("hc")) USE_HILL_CLIMBING = true;
                        else if (alg.equals("sa")) {
                            USE_HILL_CLIMBING = false;
                            if (i < args.length && !args[i + 1].startsWith("-") && isNumber(args[i + 1])) {
                                saIter = Integer.parseInt(args[i++]);
                                saStIter = Integer.parseInt(args[i++]);
                                saK = Integer.parseInt(args[i++]);
                                saLambda = Double.parseDouble(args[i++]);
                            }
                        } else usage();
                        break;
                    default:
                        usage(arg);
                        break;
                }
            } else {
                usage(arg);
            }
        }

        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                           Experimento Bicing                            |");
        System.out.println("+-------------------------------------------------------------------------+");

        System.out.println("  Escenario:");
        System.out.println("\tnest: \t\t" + nest);
        System.out.println("\tnbic: \t\t" + nbic);
        System.out.println("\tnf: \t\t" + nf);
        System.out.println("\tdem: \t\t" + (dem == Estaciones.EQUILIBRIUM ? "equilibrium" : "rush hour"));
        System.out.println("\tseed: \t\t" + seed);
        System.out.println();

        System.out.println("  Algoritmo: \t\t\t" + (USE_HILL_CLIMBING ? "Hill Climbing" : "Simulated Annealing"));
        if (!USE_HILL_CLIMBING) {
            System.out.println("\titer:  \t\t" + saIter);
            System.out.println("\tstiter:\t\t" + saStIter);
            System.out.println("\tk:     \t\t" + saK);
            System.out.println("\tlambda:\t\t" + saLambda);
            System.out.println();

        }
        System.out.println("  Usando operador 3: \t\t" + (USE_OP_3 ? "si" : "no"));
        System.out.println("  Transporte gratis: \t\t" + (FREE_TRANSPORT ? "si" : "no"));
        System.out.println("  Solucion trivial: \t\t" + (USE_TRIVIAL_INITIAL_SOLUTION ? "si" : "no"));
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println();
        System.out.println();
        results = "";

        for (i = 0; i < NUM_EXPERIMENTS; ++i)

        {

            BicingState BS = new BicingState(nest, nbic, nf, dem, seed);
            if (USE_HILL_CLIMBING) BicingHillClimbingSearch(BS);
            else BicingSimulatedAnnealingSearch(BS);


        }

        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                               Resultados                                |");
        System.out.println("+------------+-----------------+----------------+-----------------+-------+");
        System.out.println("| heuristico | beneficio (eur) | distancia (km) |   tiempo (ms)   | nodos |");
        System.out.println("+------------+-----------------+----------------+-----------------+-------+");

        System.out.print(results);

        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println();


        System.out.println("Media final: " + (eurosAverage / (double) NUM_EXPERIMENTS) + " eur");
    }

    private static void usage(String arg) {
        System.out.println("No se reconoce el parametro \"" + arg + "\".");
        usage();
    }

    private static void usage() {
        System.out.println(
                "Possibles parametros:\n" +
                "\n" +
                "  \"-nest n\"  -> el problema tiene n estaciones (n = 25 por defecto)\n" +
                "  \"-nbic n\"  -> el problema tiene n bicicletas (n = 1250 por defecto)\n" +
                "  \"-nf n\"    -> el problema tiene n furgonetas (n = 20 por defecto)\n" +
                "  \"-seed n\"  -> se usa n como semilla (n = 1234 por defecto)\n" +
                "\n" +
                "  \"-dem E\"   -> se usa una demanda equilibrada (valor por defecto)\n" +
                "  \"-dem R\"   -> se usa una demanda de hora punta\n" +
                "\n" +
                "  \"-nexp n\"  -> indica el numero de experimentos a realizar (n = 10 por defecto)\n" +
                "  \"-op3\"     -> se usa el operador 3 (el operador 4 se usa por defecto)\n" +
                "  \"-trivial\" -> se usa la solucion inicial trivial (se usa la compleja por defecto)\n" +
                "  \"-gratis\"  -> no se tiene en cuenta el coste del transporte ni en el heuristico ni el las ganancias finales (por defecto se tiene en cuenta)\n" +
                "\n" +
                "  \"-alg hc\"  -> se usa el algoritmo de Hill Climbing (valor por defecto)\n" +
                "  \"-alg sa\"  -> se usa el algoritmo de Simulated Annealing con sus parametros por defecto (iteraciones = 100000, stiter = 1000, k = 100000, lambda = 0.01)\n" +
                "  \"-alg sa iteraciones stiter k lambda\" -> se usa el algoritmo de Simulated Annealing con los parametros que se indican\n" +
                "\n" +
                "  Ejemplos:\n" +
                "\n" +
                "  > java -jar Bicing.jar -nest 27 -nexp 10 -trivial -seed 0 -alg sa 1000 100 5 0.001 -op3\n" +
                "  > java -jar Bicing.jar -alg hc -dem R -nf 40 -nest 45 -nbic 700\n" +
                "  > java -jar Bicing.jar -alg sa");
        System.exit(-1);
    }

    private static void BicingHillClimbingSearch(BicingState BS) {
        try {
            double t0 = System.currentTimeMillis();

            Problem problem = new Problem(BS, new BicingSuccessorFunction(), new ProbTSPGoalTest(), new BicingHeuristicFunction());
            Search search = new HillClimbingSearch();

            SearchAgent agent = new SearchAgent(problem, search);

            BicingState finalState;
            if (search.getPathStates().size() > 0)
                finalState = (BicingState) search.getPathStates().get(search.getPathStates().size() - 1);
            else finalState = BS;

            BicingHeuristicFunction HF = new BicingHeuristicFunction();
            double vv = HF.getHeuristicValue(finalState);

            String leftAlignFormat = "| %10.3f | %15d | %14.1f | %15.1f | %5d | %n";
            results += String.format(leftAlignFormat, vv, finalState.getMoney(), finalState.getTotalDistance(), System.currentTimeMillis() - t0, search.getPathStates().size());

            eurosAverage += (double) finalState.getMoney();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void BicingSimulatedAnnealingSearch(BicingState BS) {

        try {
            double t0 = System.currentTimeMillis();

            Problem problem = new Problem(BS, new BicingSuccessorFunctionSA(), new ProbTSPGoalTest(), new BicingHeuristicFunction());
            Search search = new SimulatedAnnealingSearch(saIter, saStIter, saK, saLambda);
            SearchAgent agent = new SearchAgent(problem, search);

            BicingState finalState;
            if (search.getPathStates().size() > 0)
                finalState = (BicingState) search.getPathStates().get(search.getPathStates().size() - 1);
            else finalState = BS;

            BicingHeuristicFunction HF = new BicingHeuristicFunction();
            double vv = HF.getHeuristicValue(finalState);

            String leftAlignFormat = "| %10.3f | %15d | %14.1f | %15.1f | %5d | %n";
            results += String.format(leftAlignFormat, vv, finalState.getMoney(), finalState.getTotalDistance(), System.currentTimeMillis() - t0, search.getPathStates().size());

            eurosAverage += (double) finalState.getMoney();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNumber(String str) {
        return str.matches("^-?\\d+$");
    }




}
