package lab2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class Lab2 {
	private static int N;
	private static final int K = 1000;
	private static final int Repeat = 100;

	private static int[][] D;

	private static final String INPUT_eil51 = "resources/input/eil51.txt";
	private static final String INPUT_eil76 = "resources/input/eil76.txt";
	private static final String INPUT_eil101 = "resources/input/eil101.txt";
	private static final String INPUT_croD100 = "resources/input/kroD100.txt";
	private static final String INPUT_croE100 = "resources/input/kroE100.txt";
	private static final String SOL_GREEDY_eil51 = "resources/greedy/eil51.txt";
	private static final String SOL_GREEDY_eil76 = "resources/greedy/eil76.txt";
	private static final String SOL_GREEDY_eil101 = "resources/greedy/eil101.txt";
	private static final String SOL_GREEDY_kroD100 = "resources/greedy/kroD100.txt";
	private static final String SOL_GREEDY_kroE100 = "resources/greedy/kroE100.txt";
	private static final String SOL_LOCAL_eil51 = "resources/local/eil51 K=" + K + ".txt";
	private static final String SOL_LOCAL_eil76 = "resources/local/eil76 K=" + K + ".txt";
	private static final String SOL_LOCAL_eil101 = "resources/local/eil101 K=" + K + ".txt";
	private static final String SOL_LOCAL_kroD100 = "resources/local/kroD100 K=" + K + ".txt";
	private static final String SOL_LOCAL_kroE100 = "resources/local/kroE100 K=" + K + ".txt";
	private static final String SOL_HIBRID_eil51 = "resources/hibrid/eil51 K=" + K + ".txt";
	private static final String SOL_HIBRID_eil76 = "resources/hibrid/eil76 K=" + K + ".txt";
	private static final String SOL_HIBRID_eil101 = "resources/hibrid/eil101 K=" + K + ".txt";
	private static final String SOL_HIBRID_kroD100 = "resources/hibrid/kroD100 K=" + K + ".txt";
	private static final String SOL_HIBRID_kroE100 = "resources/hibrid/kroE100 K=" + K + ".txt";
	private static final String SOL_SA_kroE100 = "resources/simulatedAnnealing/eil51 K=" + K + ".txt";

	public static void main(final String[] args) throws IOException {

		readInput(INPUT_eil51);
		simulatedAnnealingSearch(SOL_SA_kroE100);
		//hibridSearch(SOL_HIBRID_kroE100);
		//greedy(SOL_GREEDY_kroE100);
		//localSearch(SOL_LOCAL_kroE100);
		//exhaustiveSimulation();

		System.out.println("end");
	}

	private static void readInput(final String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		Map<Integer, int[]> points = new HashMap<Integer, int[]>();
		String line;
		String[] values;
		int i, x, y, j, dx, dy, d;
		int[] coord1 = new int[2], coord2 = new int[2];

		br.readLine(); //read name
		N = Integer.parseInt(br.readLine());
		D = new int[N + 2][N + 2];

		for (j = 0; j < N; j++) {
			line = br.readLine();
			values = StringUtils.split(line);
			i = Integer.parseInt(values[0]);
			x = Integer.parseInt(values[1]);
			y = Integer.parseInt(values[2]);
			coord1 = new int[2];
			coord1[0] = x;
			coord1[1] = y;
			points.put(i, coord1);
		}

		for (i = 1; i <= N; i++) {
			for (j = i; j <= N; j++) {
				if (i == j) {
					D[i][j] = 0;
				}
				else {
					coord1 = points.get(i);
					coord2 = points.get(j);
					dx = coord1[0] - coord2[0];
					dy = coord1[1] - coord2[1];
					double test = Math.sqrt(dx * dx + dy * dy);
					d = (int) Math.round(test);
					D[i][j] = D[j][i] = d;
				}
			}
		}

//		for(i=1;i<=N;i++) {
//			System.out.println();
//			for(j=1;j<=N;j++){
//				System.out.print(D[i][j] + " ");
//			}
//		}

		br.close();
	}

	public static void exhaustiveSimulation() {
		int[] array = new int[N];
		for (int i = 0; i < N; i++) {
			array[i] = i + 1;
		}
		permute(array, 0);

	}

	public static void greedy(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		Integer cost;

		writer.println("TSP: Solutie GREEDY");
		Integer[] solution = pseudo_greedy();
		cost = computeCost(solution);

		printSolution(cost, solution, writer);
		printExecutionTime(startTime, writer);
	}

	public static void localSearch(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		Random rn = new Random();
		Integer bestCost = 0, tempCost, bestCostFromK = 9999999, noNewBest;
		Integer[] solution = new Integer[N + 3], tempSolution, bestSolutionFromK = null;

		writer.println("TSP: Solutie Cautare Locala");

		for (int k = 0; k < Repeat; k++) {
			solution = generateRandomPermutation();
			noNewBest = 0;
			bestCost = 9999999;
			while (noNewBest < K) {
				bestCost = computeCost(solution);
				//generate temporary solution
				tempSolution = twoOptim(solution.clone(), rn);
				tempCost = computeCost(tempSolution);
				//select best solution
				if (tempCost < bestCost) {
					bestCost = tempCost;
					solution = tempSolution;
					noNewBest = 0;
				}
				else {
					noNewBest++;
				}
			}
			//update bestFromK solution
			if (k == 0 || bestCost < bestCostFromK) {
				bestCostFromK = bestCost;
				bestSolutionFromK = solution.clone();
			}

		}

		printSolution(bestCostFromK, bestSolutionFromK, writer);
		printExecutionTime(startTime, writer);

	}

	public static void hibridSearch(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		Random rn = new Random();
		Integer cost = 0, tempCost, noNewBest = 0;
		Integer[] solution, tempSolution;
		writer.println("TSP: Solutie Cautare Hibrid");

		solution = pseudo_greedy();

		while (noNewBest < K) {
			cost = computeCost(solution);
			//generate temporary solution
			tempSolution = twoOptim(solution.clone(), rn);
			tempCost = computeCost(tempSolution);
			//update with new, better solution
			if (tempCost < cost) {
				cost = tempCost;
				solution = tempSolution;
				noNewBest = 0;
			}
			else {
				noNewBest++;
			}
		}

		printSolution(cost, solution, writer);
		printExecutionTime(startTime, writer);
	}

	public static void simulatedAnnealingSearch(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		Random rn = new Random();

		Integer cost = 0, neighborCost, bestCostFromK = 9999999;
		Integer[] solution = new Integer[N + 3], neighborSol, bestSolutionFromK = null;
		writer.println("TSP: Solutie Cautare Locala");

		double T = 10000;
		int t = 0, k = 0;

		solution = generateRandomPermutation();
		cost = computeCost(solution);

		while (T > 0.1) {
			k = 0;
			while (k < K) {
				neighborSol = twoOptim(solution.clone(), rn);
				neighborCost = computeCost(neighborSol);
				//System.out.println("diff:" + (cost - neighborCost) + " val:" + Math.exp((neighborCost - cost) / T) );
				//System.out.println(Math.exp((neighborCost - cost) / T));

				if (neighborCost < cost || rn.nextDouble() < Math.exp((cost - neighborCost) / T)) {
					solution = neighborSol;
					cost = neighborCost;
				}
				k++;
			}
			//System.out.println(T);
			//halt-condition
			T = haltFunction(T, t);
			t = t + 1;
		}

		printSolution(cost, solution, writer);
		printExecutionTime(startTime, writer);

	}

	private static double haltFunction(final double T, final int t) {
		return T * 0.99;
	}

	private static Integer[] twoOptim(final Integer[] solution, final Random rn) {
		int i, j, aux;
		Integer[] midPart, firstPart, lastPart, result;
		//generate random i, j
		i = rn.nextInt(N + 1);
		j = rn.nextInt(N + 1);
		while (Math.abs(i - j) < 2) { //there must be at least 2 cities between i and j
			j = rn.nextInt(N + 1);
		}
		if (j < i) {
			aux = i;
			i = j;
			j = aux;
		}

		firstPart = ArrayUtils.subarray(solution, 0, i);
		midPart = ArrayUtils.subarray(solution, i, j);
		lastPart = ArrayUtils.subarray(solution, j, N);
		ArrayUtils.reverse(midPart);

		result = ArrayUtils.addAll(firstPart, midPart);
		result = ArrayUtils.addAll(result, lastPart);
		return result;

	}

	private static void permute(final int[] a, final int k) {
		if (k == a.length) {
			for (int i = 0; i < a.length; i++) {
				System.out.print(a[i] + " ");
			}
			System.out.println();
		}
		else {
			for (int i = k; i < a.length; i++) {
				int temp = a[k];
				a[k] = a[i];
				a[i] = temp;

				permute(a, k + 1);

				temp = a[k];
				a[k] = a[i];
				a[i] = temp;
			}
		}
	}

	private static void printSolution(final Integer cost, final Integer[] solution, final PrintWriter writer) {
		String strSol = StringUtils.join(solution, " ");
		writer.println("Min Cost: " + cost);
		writer.print("Solution:" + strSol);
	}

	private static void printExecutionTime(final long startTime, final PrintWriter writer) {
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		writer.println("\nExecution time " + duration + "ms!");
		writer.close();
	}

	private static Integer[] generateRandomPermutation() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			list.add(i + 1);
		}
		Collections.shuffle(list);
		return list.toArray(new Integer[list.size()]);
	}

	private static Object test() {
		String str = "40 42 19 41 13 25 14 24 6 51 46 12 47 18 4 17 37 44 15 45 33 39 10 49 5 38 11 32 1 27 48 23 43 7 26 8 31 28 22 3 36 35 20 29 21 34 30 9 50 16 2";
		String[] tokens = str.split(" ");
		int cost = 0, j, k, z, d;
		for (int i = 0; i < N - 1; i++) {
			j = Integer.parseInt(tokens[i]);
			z = Integer.parseInt(tokens[i + 1]);
			d = D[j][z];
			cost += d;
		}
		return null;
	}

	private static Integer computeCost(final Integer[] sol) {
		Integer cost = 0;
		for (int i = 0; i < N - 1; i++) {
			cost += D[sol[i]][sol[i + 1]];
		}
		cost += D[sol[0]][sol[N - 1]];
		return cost;
	}

	private static Integer[] pseudo_greedy() {
		Random rn = new Random();
		Set<Integer> visited = new HashSet<Integer>();
		Integer minDis, next = null, i, initial, first;
		Integer[] solution = new Integer[N + 2];

		first = initial = rn.nextInt(N + 1);
		visited.add(first);
		solution[0] = first;

		for (int k = 1; k < N; k++) {
			//select the closest city
			minDis = 99999999;
			for (i = 1; i <= N; i++) {
				if (!visited.contains(i) && D[first][i] < minDis) {
					minDis = D[first][i];
					next = i;
				}
			}
			//add next city to go
			visited.add(next);
			first = next;
			solution[k] = first;
		}
		solution[N] = initial;
		return solution;
	}

}
