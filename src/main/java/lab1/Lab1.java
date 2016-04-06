package lab1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

public class Lab1 {

	private static final int N = 200;
	private static final int K = 10;

	private static final int valoareMax = 50;
	private static final int greutateMax = 10;
	private static int capacitateRucsac = 100;

	private static final int readMode = 0;
	private static Integer[] G, V;

	private static final String INPUT = "resources/N-" + N + "/Rucsac n-" + N + ".txt";
	private static final String INPUT_TEACHER = "resources/FromTeacher/rucsac-" + N + ".txt";
	private static final String SOLUTION_RANDOM = "resources/N-" + N + "/Random S. n-" + N + " k-" + K + ".txt";
	private static final String SOLUTION_GREEDY = "resources/N-" + N + "/Greedy S. n-" + N + ".txt";
	private static final String SOLUTION_EXHAUSTIVE = "resources/N-" + N + "/Exhaustive S. n-" + N + ".txt";
	private static final String SOLUTION_STEEPEST_ASCENT = "resources/N-" + N + "/Hill Climbing S. n-" + N + " k-" + K + ".txt";
	private static final String SOLUTION_STEEPEST_ASCENT_TEACHER = "resources/FromTeacher/N-" + N +"/Hill Climbing S. n-" + N + " k-" + K + ".txt";
	private static final String SOLUTION_EXHAUSTIVE_TEACHER = "resources/FromTeacher/N-" + N +"/Exhaustive S. n-" + N + ".txt";
	private static final String SOLUTION_RANDOM_TEACHER = "resources/FromTeacher/N-" + N +"/Random S. n-" + N + " k-" + K + ".txt";
	private static final String SOLUTION_GREEDY_TEACHER = "resources/FromTeacher/N-" + N +"/Greedy S. n-" + N + ".txt";

	public static void main(final String[] args) throws IOException {

		readFile(INPUT_TEACHER, 1); //0 --> for local, 1 --> for teacher
//		randomSearch(SOLUTION_RANDOM_TEACHER);
		randomSearchOneRun(SOLUTION_RANDOM_TEACHER);
//		exhaustiveSearch();
//		greedySearch();
//		steepestAscentSearch();
		System.out.println("end");
	}

	public static void generateNumbers() throws FileNotFoundException, UnsupportedEncodingException {
		Random rn = new Random();
		PrintWriter writer = new PrintWriter(INPUT, "UTF-8");

		Integer i, nr;
		StringBuilder strB;
		String sep = "\t";

		writer.println(N);

		for (i = 0; i < N; i++) {
			strB = new StringBuilder(sep);
			strB.append(i);

			// valoare 0-vMax
			strB.append(sep);
			nr = rn.nextInt(valoareMax + 1);
			strB.append(nr);

			// greutate 0-gMax
			strB.append(sep);
			nr = rn.nextInt(greutateMax + 1);
			strB.append(nr);

			writer.println(strB.toString());
		}

		writer.close();
	}

	public static void readFile(final String input, final int mode) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(input));

		G = new Integer[N + 1];
		V = new Integer[N + 1];
		int i, v, g, ln = 0;
		String separator;

		if (mode == 0) {
			separator = "\t";
		}
		else {
			separator = " ";
		}

		String[] values;
		String line = br.readLine();
		line = br.readLine();

		while (line != null) {
			ln++;
			if (ln <= N) {
				values = StringUtils.split(line, separator);
				i = Integer.parseInt(values[0]) - mode;
				v = Integer.parseInt(values[1]);
				g = Integer.parseInt(values[2]);
				V[i] = v;
				G[i] = g;
			}
			else if (mode == 1) {
				capacitateRucsac = Integer.parseInt(line);
			}
			line = br.readLine();
		}

		br.close();
	}

	public static void randomSearchOneRun(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		Random rn = new Random();
		Boolean[] S = new Boolean[N + 1];

		writer.println("Rucsac: Solutie Random n-" + N + " k-" + K);
		writer.println("V\tG\tSolutie");

		int bestG = 0, bestV = 0, tGreutate = 0, tValoare = 0, avgG = 0, avgV = 0;
		int result[];
		Boolean[] bestS = new Boolean[N + 1];

		for (int j = 0; j < K; j++) {

			// generate solution
			for (int i = 0; i < N; i++) {
				S[i] = rn.nextBoolean();
			}

			// checkValid
			result = computeAndCheckValid(S);

			if(result[0] > 0 && result[0] > bestV) {
				bestV = result[0];
				bestG = result[1];
				bestS = S.clone();
			}
		}

		if (bestV > 0) {
			writer.println(printSolution(bestV, bestG, bestS));
		}
		else {
			writer.println("\nNu exista solutie valida ");
		}

		printExecutionTime(startTime, writer);
	}

	public static void randomSearch(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		Random rn = new Random();
		Boolean[] S = new Boolean[N + 1];

		writer.println("Rucsac: Solutie Random n-" + N + " k-" + K);
		writer.println("V\tG\tSolutie");

		int bestG = 0, bestV = 0, tGreutate = 0, tValoare = 0, avgG = 0, avgV = 0;
		int result[];
		Boolean[] bestS = new Boolean[N + 1];
		ObiectRucsac[] objRulari = new ObiectRucsac[10];

		for (int z = 0; z < 10; z++) {
			bestG = 0;
			bestV = 0;

			for (int j = 0; j < K; j++) {
				// generate solution
				for (int i = 0; i < N; i++) {
					S[i] = rn.nextBoolean();
				}
				// checkValid
				result = computeAndCheckValid(S);

				// print valid and best solution, update bestSolution
				if (result[1] > 0 && result[0] > bestV) {
					bestV = tValoare;
					bestG = tGreutate;
					bestS = S.clone();
				}
			}

			if (bestV > 0) {
				// Salveaza bestSolution pentru Rularea z
				objRulari[z] = new ObiectRucsac();
				objRulari[z].setValoare(bestV);
				objRulari[z].setGreutate(bestG);
				objRulari[z].setBooleanSol(bestS.clone());

				// print Best Solution rularea z
				writer.println("\nSolutie rularea " + (z + 1));
				writer.println(printSolution(bestV, bestG, bestS));
			}
			else {
				writer.println("\nNu exista solutie valida " + (z + 1));
			}
		}

		printSolutionForAllExecutions(objRulari, 10, writer);
		printExecutionTime(startTime, writer);
	}

	public static void exhaustiveSearch(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.println("Rucsac: Solutie Exhaustiva n-" + N + "\n");
		writer.println("V\tG\tSolutie");

		String S, bestS = "";
		int bestG = 0, bestV = 0;
		int tGreutate, tValoare;
		Long maxValue = (long) Math.floor(Math.pow(2, N)) - 1;

		// generate all solutions N must be < 60
		for (Long j = maxValue; j >= 0; j--) {
			// compute Solution
			S = StringUtils.leftPad(Long.toBinaryString(j), N, '0');
			tGreutate = 0;
			tValoare = 0;
			for (int i = 0; i < S.length(); i++) {
				if (S.charAt(i) == '1') {
					tGreutate += G[i];
					tValoare += V[i];
				}
			}

			if (j % 10000000 == 0) {
				System.out.println(j);
			}

			// update best solution &&
			if (tValoare > bestV && tGreutate < capacitateRucsac) {
				bestV = tValoare;
				bestG = tGreutate;
				bestS = S;
			}
		}
		
		// print execution time and Best Solution
		writer.print("\nBest Solution: ");
		writer.println(printStringSolution(bestV, bestG, bestS));
		printExecutionTime(startTime, writer);
	}

	public static void greedySearch(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		List<ObiectRucsac> objectsList = new ArrayList<ObiectRucsac>();
		writer.println("Rucsac: Solutie Exhaustive n-" + N);
		writer.println("Sortat Dupa Calitate Calitate");

		// create list of objects
		for (int i = 0; i < N; i++) {
			ObiectRucsac obj = new ObiectRucsac();
			float calitate = (float) G[i] / V[i];

			obj.setIndex(i);
			obj.setGreutate(G[i]);
			obj.setValoare(V[i]);
			obj.setCalitate(calitate);

			objectsList.add(obj);
		}

		//sort dupa calitate
		Collections.sort(objectsList, new Comparator<ObiectRucsac>() {
			public int compare(final ObiectRucsac obj1, final ObiectRucsac obj2) {
				return obj1.getCalitate().compareTo(obj2.getCalitate());
			}
		});

		int bestG = 0, bestV = 0, k = 0;
		Boolean[] bestS = new Boolean[N + 1];
		for (int z = 0; z < N + 1; z++) {
			bestS[z] = false;
		}

		while (k < N) {
			if ((bestG + objectsList.get(k).getGreutate()) < capacitateRucsac) {
				bestG += objectsList.get(k).getGreutate();
				bestV += objectsList.get(k).getValoare();
				bestS[objectsList.get(k).getIndex()] = true;
				k++;
			}
			else {
				break;
			}
		}

		// print Solution
		writer.println("\nSolution:");
		writer.println(printSolution(bestV, bestG, bestS));

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;

		writer.println("\nExecution time " + duration + "ms!");
		writer.close();
	}

	private static String printSolution(final int v, final int g, final Boolean[] s) {
		StringBuilder str;
		str = new StringBuilder("");
		str.append(v);
		str.append("\t");
		str.append(g);
		str.append("\t");
		for (int i = 0; i < N; i++) {
			if (s[i]) {
				str.append('1');
			}
			else {
				str.append('0');
			}
		}

		return str.toString();
	}

	private static String printStringSolution(final int v, final int g, final String s) {
		StringBuilder str;
		str = new StringBuilder("");
		str.append(v);
		str.append("\t");
		str.append(g);
		str.append("\t");
		str.append(s);
		return str.toString();
	}

	private static void steepestAscentSearch(final String file) throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.println("Rucsac: Solutie Steepest Ascent Hill Climbing n-" + N + " k-" + K);

		int bestV, bestG;
//		ObiectRucsac[] objRulari = new ObiectRucsac[10];
		ObiectRucsac localObj;
		ObiectRucsac bestSolFromK = null;

//		//Simuleaza rulari
//		for (int z = 0; z < 10; z++) {
		bestG = 0;
		bestV = 0;

		//find best solution in K tries
		for (int j = 0; j < K; j++) {
			localObj = hillClimbing();
			if (bestV <= 0 && localObj != null) {
				bestSolFromK = localObj;
				bestV = bestSolFromK.getValoare();
				bestG = bestSolFromK.getGreutate();
			}
			else if (localObj != null && bestV < localObj.getValoare()) {
				bestSolFromK = localObj;
				bestV = bestSolFromK.getValoare();
				bestG = bestSolFromK.getGreutate();
			}
		}

//			//save Solution
//			objRulari[z] = bestSolFromK;

		// print solution or message
//			writer.println("\nSolutie rularea " + (z + 1));
		if (bestSolFromK != null && bestSolFromK.getValoare() > 0) {
			writer.println(printSolution(bestV, bestG, bestSolFromK.getBooleanSol()));
		}
		else {
			writer.println("Nici o solutie valida");
		}
//		}

//		printSolutionForAllExecutions(objRulari, 10, writer);
		printExecutionTime(startTime, writer);

	}

	private static int[] computeAndCheckValid(final Boolean[] S) {
		int result[] = new int[2];
		int v = 0, g = 0;

		for (int i = 0; i < N; i++) {
			if (S[i]) {
				g += G[i];
				v += V[i];
			}
		}

		if (g > capacitateRucsac) {
			result[0] = -1;
			result[1] = -1;
		}
		else {
			result[0] = v;
			result[1] = g;
		}

		return result;
	}

	public static void printExecutionTime(final long startTime, final PrintWriter writer) {
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		writer.println("\nExecution time " + duration + "ms!");
		writer.close();
	}

	private static void printSolutionForAllExecutions(final ObiectRucsac[] objRulari, int z, final PrintWriter writer) {
		//compute final best Solution
		int bestV = -1, bestG = -1, avgG = 0, avgV = 0;
		Boolean[] bestS = new Boolean[N + 1];
		int k = 0;

		for (z = 0; z < 10; z++) {
			if (objRulari[z] != null) {
				int v = objRulari[z].getValoare();
				int g = objRulari[z].getGreutate();

				if (v > bestV) {
					bestV = v;
					bestG = g;
					bestS = objRulari[z].getBooleanSol().clone();
				}

				avgV += v;
				avgG += g;
				k++;
			}
		}

		if (k != 0) {
			avgV = avgV / k;
			avgG = avgG / k;
			// print Best Solution
			writer.println("\n!!Best Solution:");
			writer.println(printSolution(bestV, bestG, bestS));
			writer.println("\n!!Average Solution:");
			writer.println(avgV + " " + avgG);
		}
		else {
			writer.println("No valid solution was found!");
		}

	}

	private static ObiectRucsac hillClimbing() {
		Random rn = new Random();
		Boolean[] S = new Boolean[N + 1];
		Boolean[] bestS = new Boolean[N + 1];
		int bestV = -1, previousV = -1, bestG = -1;
		int[] result;
		boolean maximFound = false;

		// generate solution
		for (int i = 0; i < N; i++) {
			S[i] = rn.nextBoolean();
		}

		result = computeAndCheckValid(S);
		previousV = result[0];
		bestV = result[0];
		bestG = result[1];

		while (!maximFound) {
			// genereaza vecini si verifica daca sunt mai buni ca solutia
			for (int i = 0; i < N; i++) {
				S[i] = !S[i];

				result = computeAndCheckValid(S);
				int vecinValue = result[0];
				if (vecinValue > bestV) {
					bestV = vecinValue;
					bestS = S.clone();
					bestG = result[1];
				}

				S[i] = !S[i];
			}

			if (bestV > previousV) {
				previousV = bestV;
				S = bestS.clone();
			}
			else {
				maximFound = true;
			}
		}

		if (bestV <= 0) {
			return null;
		}

		ObiectRucsac obj = new ObiectRucsac();
		obj.setBooleanSol(bestS.clone());
		obj.setValoare(bestV);
		obj.setGreutate(bestG);
		return obj;
	}

}
