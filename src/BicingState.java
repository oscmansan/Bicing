import IA.Bicing.Estaciones;
import aima.search.framework.HeuristicFunction;
import aima.search.framework.Successor;
import javafx.util.Pair;

import java.util.*;

public class BicingState {

    public static final int MAX_BIKES_PER_VAN = 30;

    public static final String ORIG_SWAP = "swap origin";
    public static final String DEST_CHANGE = "change destination";
    public static final String NBIKES_CHANGE = "change num of bikes";

    // Stations sorted by number of available bikes
    public static ArrayList<Pair<Integer, Integer>> stationsByAvailableBikes;

    // Stations sorted by number of needed bikes
    public static ArrayList<Pair<Integer, Integer>> stationsByNeededBikes = new ArrayList<>();

    // Position i contains the index of station i in stationsByAvailableBikes
    public static int[] stationsByAvailableBikesIndices;

    // Position i contains the index of station i in stationsByNeededBikes
    public static int[] stationsByNeededBikesIndices;

    public static final int NO_STATION = -1;
    public static final int ORIG = 0;       // Origin station
    public static final int DEST1 = 1;      // Destination station (1)
    public static final int NBIKES1 = 2;    // Num of bikes in (1)
    public static final int DEST2 = 3;      // Destination station (2)
    public static final int NBIKES2 = 4;    // Num of bikes in (2)

    public static Estaciones stations;
    public static int nvans;

    public int[][] vans;

    /**
     * Default constructor
     */
    public BicingState() {
        vans = new int[nvans][5];
    }

    /**
     * Constructor with arguments and a starter solution
     *
     * @param nst  num of stations
     * @param nbik num of bikes
     * @param nv   num of vans
     * @param dem  setting type (equilibrium/rush hour)
     * @param seed seed for the random generator
     */
    public BicingState(int nst, int nbik, int nv, int dem, int seed)
    {
        nvans = nv;
        stations = new Estaciones(nst, nbik, dem, seed);
        vans = new int[nvans][5];

        stationsByAvailableBikes = new ArrayList<>();
        stationsByNeededBikes = new ArrayList<>();

        stationsByAvailableBikesIndices = new int[stations.size()];
        stationsByNeededBikesIndices = new int[stations.size()];
        fillStationArrays();

        if(Main.USE_TRIVIAL_INITIAL_SOLUTION) trivialSolution();
        else complexSolution();
    }

    private final void fillStationArrays() {
        for (int i = 0; i < stations.size(); ++i) {
            stationsByAvailableBikes.add(new Pair<>(i, getAvailableBikes(i)));
        }
        Collections.sort(stationsByAvailableBikes, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (int i = 0; i < stations.size(); ++i) {
            stationsByNeededBikes.add(new Pair<>(i, Math.max(0, getDemand(i) - getNumBikesNext(i))));
        }
        Collections.sort(stationsByNeededBikes, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (int j = 0; j < stationsByAvailableBikes.size(); ++j) {
            stationsByAvailableBikesIndices[stationsByAvailableBikes.get(j).getKey()] = j;
        }

        for (int j = 0; j < stationsByNeededBikes.size(); ++j) {
            stationsByNeededBikesIndices[stationsByNeededBikes.get(j).getKey()] = j;
        }

    }

    private final void trivialSolution() {
        for (int i = 0; i < nvans; ++i) {
            vans[i][ORIG] = i;
            vans[i][DEST1] = new Random().nextInt(stations.size());
            do {
                vans[i][DEST2] = new Random().nextInt(stations.size());
            } while (vans[i][DEST2] == vans[i][DEST1]);

            int n = Math.min(MAX_BIKES_PER_VAN, getAvailableBikes(vans[i][ORIG]));
            vans[i][NBIKES1] = (n == 0) ? 0 : new Random().nextInt(n);
            vans[i][NBIKES2] = n - vans[i][NBIKES1];
        }
    }

    private final void complexSolution() {
        for (int i = 0; i < nvans; ++i) {
            vans[i][ORIG] = stationsByAvailableBikes.get(i).getKey();
            vans[i][DEST1] = i != vans[i][ORIG] ? i : ((i + 1) % stations.size());
            vans[i][DEST2] = ((i + 1) != vans[i][ORIG] && (i + 1) != vans[i][DEST1]) ? ((i + 1) % stations.size()) : ((i + 2) % stations.size());
            vans[i][NBIKES1] = Math.min(MAX_BIKES_PER_VAN, Math.min(stationsByAvailableBikes.get(i).getValue(), stationsByNeededBikes.get(i).getValue()));
            vans[i][NBIKES2] = stationsByAvailableBikes.get(i).getValue() - vans[i][NBIKES1];
        }
    }

    /**
     * Returns a copy of the state
     *
     * @return a copy of the calling state
     */
    public BicingState copy() {
        BicingState bs = new BicingState();
        for (int i = 0; i < nvans; ++i) {
            bs.vans[i][ORIG] = vans[i][ORIG];
            bs.vans[i][DEST1] = vans[i][DEST1];
            bs.vans[i][NBIKES1] = vans[i][NBIKES1];
            bs.vans[i][DEST2] = vans[i][DEST2];
            bs.vans[i][NBIKES2] = vans[i][NBIKES2];
        }
        return bs;
    }


    // Getters

    /**
     * Returns the origin of the van
     *
     * @param i the index of the van
     * @return the origin station
     */
    public final int getOrig(int i) {
        return vans[i][ORIG];
    }

    /**
     * Returns the destination (first or second) of the van
     *
     * @param i    the index of the van
     * @param dest indicates first or second destination
     * @return the destination station
     */
    public final int getDest(int i, int dest) {
        return vans[i][dest];
    }

    /**
     * Returns the number of bikes the van leaves at the destination
     *
     * @param i    the index of the van
     * @param dest indicates first or second destination
     * @return the num of bikes the van leaves at the destination
     */
    public final int getNumBikes(int i, int dest) {
        return getDest(i, dest) == NO_STATION ? 0 : vans[i][dest + 1];
    }

    /**
     * Returns the number of bikes the van takes from the origin
     *
     * @param i the index of the van
     * @return the numb of bikes the van takes from the origin
     */
    public final int getTakenBikes(int i) {
        return getNumBikes(i, DEST1) + getNumBikes(i, DEST2);
    }

    /**
     * Returns the number of bikes there will be in the station in the next hour
     *
     * @param station the index of the considered station
     * @return the number of bikes there will be in the next hour, excluding transfers
     */
    public final int getNumBikesNext(int station) {
        return stations.get(station).getNumBicicletasNext();
    }

    /**
     * Returns the number of bikes that will be needed in the station in the next hour
     *
     * @param station the index of the considered station
     * @return the number of bikes that will be needed in the next hour
     */
    public final int getDemand(int station) {
        return stations.get(station).getDemanda();
    }

    /**
     * Returns the number of bikes that won't be moved from the station in the current hour
     *
     * @param station the index of the considered station
     * @return the number of bikes that won't be moved in the current hour
     */
    public final int getUselessBikes(int station) {
        return stations.get(station).getNumBicicletasNoUsadas();
    }


    // Operators

    /**
     * Swaps the origin of the van whose current origin is station1 with the van whose current origin is station2;
     * if there is only one such van, changes its origin;
     * if there are not such vans, it does nothing
     *
     * @param station1 the index of the first station
     * @param station2 the index of the second station
     * @return true if the swap was possible and thus has been made;
     * false otherwise
     */
    public final boolean swapOrig(int station1, int station2) {
        int vanOnStation1, vanOnStation2;
        vanOnStation1 = vanOnStation2 = -1;
        for (int i = 0; i < nvans; ++i) {
            if (getOrig(i) == station1) vanOnStation1 = i;
            else if (getOrig(i) == station2) vanOnStation2 = i;
        }

        if (vanOnStation1 == -1 && vanOnStation2 == -1) return false;
        else if (vanOnStation1 != -1 && vanOnStation2 == -1 && getTakenBikes(vanOnStation1) <= getAvailableBikes(station2)) {
            vans[vanOnStation1][ORIG] = station2;
        }
        else if (vanOnStation2 != -1 && vanOnStation1 == -1 && getTakenBikes(vanOnStation2) <= getAvailableBikes(station1)) {
            vans[vanOnStation2][ORIG] = station1;
        }
        else if (vanOnStation1 != -1 && vanOnStation2 != -1 && getTakenBikes(vanOnStation1) <= getAvailableBikes(station2) &&
                getTakenBikes(vanOnStation2) <= getAvailableBikes(station1)) {
            vans[vanOnStation1][ORIG] = station2;
            vans[vanOnStation2][ORIG] = station1;
        }
        return true;
    }

    /**
     * Changes the destination of the van i
     *
     * @param i       the index of the van
     * @param dest    indicates first or second destination
     * @param station the new destination station
     */
    public final void changeDest(int i, int dest, int station) {
        vans[i][dest] = station;
    }

    /**
     * Adds 1 bike to the destination dest of the van i
     *
     * @param i    the index of the van
     * @param dest indicates first or second destination
     */
    public final void addBike(int i, int dest) {
        changeNumBikes(i, dest, getNumBikes(i, dest) + 1);
    }

    /**
     * Substracts 1 bike to the destination dest of the van i
     *
     * @param i    the index of the van
     * @param dest indicates first or second destination
     */
    public final void substractBike(int i, int dest) {
        changeNumBikes(i, dest, getNumBikes(i, dest) - 1);
    }


    // Auxiliary functions

    /**
     * Returns the Manhattan distance between stations
     *
     * @param station1 the index of the first station
     * @param station2 the index of the second station
     * @return the Manhattan distance between stations
     */
    public static int getDistance(int station1, int station2) {
        int x1 = BicingState.stations.get(station1).getCoordX();
        int y1 = BicingState.stations.get(station1).getCoordY();
        int x2 = BicingState.stations.get(station2).getCoordX();
        int y2 = BicingState.stations.get(station2).getCoordY();

        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Changes the num of bikes the van leaves at the destination
     *
     * @param i    the index of the van
     * @param dest indicates first or second destination
     * @param n    the num of bikes the van leaves at the destination
     */
    public final void changeNumBikes(int i, int dest, int n) {
        vans[i][dest + 1] = n;
    }

    /**
     * Returns the maximum number of bikes the van can take from the station
     *
     * @param station the index of the station
     * @return the maximum number of bikes the van can take from the station
     */
    public final int getAvailableBikes(int station) {
        return Math.min(MAX_BIKES_PER_VAN, Math.min( Math.max(0, getNumBikesNext(station) - getDemand(station)), getUselessBikes(station)));
    }

    /**
     * Returns the total amount of money earned from bike transfers
     * @return  the total amount of money earned from bike transfers
     */
    public final int getMoney() {
        int money = 0;

        int[] addedBikes = new int[BicingState.stations.size()];
        int[] takenBikes = new int[BicingState.stations.size()];

        for (int i = 0; i < BicingState.nvans; ++i) {
            addedBikes[getDest(i, BicingState.DEST1)] += getNumBikes(i, BicingState.DEST1);
            if (getDest(i, BicingState.DEST2) != BicingState.NO_STATION)
                addedBikes[getDest(i, BicingState.DEST2)] += getNumBikes(i, BicingState.DEST2);
            takenBikes[getOrig(i)] += getTakenBikes(i);
        }

        for (int i = 0; i < stations.size(); ++i)
        {
            int demand = getDemand(i);
            int next = getNumBikesNext(i);
            int result = next + (addedBikes[i] - takenBikes[i]);
            int realAdded = Math.max(0, addedBikes[i] - takenBikes[i]);
            int realTaken = Math.max(0, takenBikes[i] - addedBikes[i]);

            if(demand > next) money += Math.min( demand - next, realAdded );
            if(result < demand) money -= Math.min( demand - result, realTaken );
        }

        if(!Main.FREE_TRANSPORT)
        {
            for (int i = 0; i < BicingState.nvans; ++i) {
                int dest1 = getDest(i, BicingState.DEST1);
                int dest2 = getDest(i, BicingState.DEST2);

                int bikesUntilDest1 = getNumBikes(i, BicingState.DEST1) + getNumBikes(i, BicingState.DEST2);
                double distOrigDest1 = (double) BicingState.getDistance(getOrig(i), dest1);
                money -= ((bikesUntilDest1 + 9) / 10) * (distOrigDest1 / 1000.0);

                if (dest2 != BicingState.NO_STATION) {
                    int bikesUntilDest2 = getNumBikes(i, BicingState.DEST2);
                    double distDest1Dest2 = (double) BicingState.getDistance(dest1, dest2);
                    money -= ((bikesUntilDest2 + 9) / 10) * (distDest1Dest2 / 1000.0);
                }
            }
        }

        return money;
    }

    /**
     * Returns the total distance traveled by the vans
     * @return  the total distance traveled by the vans in km
     */
    public final double getTotalDistance() {
        int dist = 0;

        for (int i = 0; i < nvans; ++i) {
            dist += getDistance(getOrig(i), getDest(i, DEST1));
            if (getDest(i, DEST2) != NO_STATION)
                dist += getDistance(getDest(i, DEST1), getDest(i, DEST2));
        }

        return (double) dist / 1000.0;
    }

    public final String toString() {
        BicingHeuristicFunction HF = new BicingHeuristicFunction();

        String str = "\n";
        // Stores the number of bikes added to each station
        int[] addedBikes = new int[BicingState.stations.size()];
        // Stores the number of bikes taken from each station
        int[] takenBikes = new int[BicingState.stations.size()];

        for (int i = 0; i < BicingState.nvans; ++i) {
            addedBikes[getDest(i, BicingState.DEST1)] += getNumBikes(i, BicingState.DEST1);
            if (getDest(i, BicingState.DEST2) != BicingState.NO_STATION)
                addedBikes[getDest(i, BicingState.DEST2)] += getNumBikes(i, BicingState.DEST2);

            takenBikes[getOrig(i)] += getNumBikes(i, BicingState.DEST1) + getNumBikes(i, BicingState.DEST2);
        }

        str += String.format("+-----------------------------------------------------+%n");
        str += String.format("| %21s %-29s | %n", " ", "STATIONS");

        str += String.format("+---------+----------+---------+--------+-------+-------+-------+ %n");
        str += String.format("| station |   next   | useless | demand | final | added | taken | %n");
        str += String.format("+---------+----------+---------+--------+-------+-------+-------+ %n");

        String leftAlignFormat = "| %7d | %8d | %6d | %5d | %5d | %5d | %5d |  Cost(%f)%s %n";
        for (int i = 0; i < stations.size(); ++i) {
            str += String.format(
                    leftAlignFormat,
                    i,
                    getNumBikesNext(i),
                    getUselessBikes(i),
                    getDemand(i),
                    (getNumBikesNext(i) + addedBikes[i] - takenBikes[i]),
                    addedBikes[i],
                    takenBikes[i],
                    HF.getHeuristicValueForStation(this, i),
                    (addedBikes[i] == 0 && takenBikes[i] == 0 ? "  \t**UNTOUCHED STATION" : "")
            );
        }
        str += String.format("+---------+----------+--------+-------+-------+-------+%n");

        str += String.format("+--------------------------------+ %n");
        str += String.format("| %12s %-17s | %n", " ", "VANS");

        str += String.format("+-----+------+---------+---------+ %n");
        str += String.format("| van | orig |  dest1  |  dest2  | %n");
        str += String.format("+-----+------+---------+---------+ %n");

        leftAlignFormat = "| %3d | %4d | %7s | %7s |  Cost(%f) %n";
        for (int i = 0; i < nvans; ++i) {
            str += String.format(
                    leftAlignFormat,
                    i,
                    getOrig(i),
                    getDest(i, DEST1) + "(" + getNumBikes(i, DEST1) + "b)",
                    getDest(i, DEST2) + "(" + getNumBikes(i, DEST2) + "b)",
                    HF.getHeuristicValueForVanTravel(this, i)
            );
        }
        str += String.format("+-----+------+---------+---------+ %n");

        return str;
    }

}
