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

	private static final int N = 20;
	private static final int K = 20;

	private static final int valoareMax = 50;
	private static final int greutateMax = 10;
	private static final int capacitateRucsac = 100;

	private static Integer[] G, V;

	private static final String INPUT = "resources/N-" + N + "/Rucsac n-" + N + ".txt";
	private static final String SOLUTION_RANDOM = "resources/N-" + N + "/Random S. n-" + N + " k-" + K + ".txt";
	private static final String SOLUTION_GREEDY = "resources/N-" + N + "/Greedy S. n-" + N + ".txt";
	private static final String SOLUTION_EXHAUSTIVE = "resources/N-" + N + "/Exhaustive S. n-" + N + ".txt";

	public static void main(String[] args) throws IOException {

		//generateNumbers();
		
		readFile();
		randomSearch();
		exhaustiveSearch();
		greedySearch();
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

	public static void readFile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(INPUT));

		G = new Integer[N + 1];
		V = new Integer[N + 1];
		int i, v, g;

		String[] values;
		String line = br.readLine();
		line = br.readLine();

		while (line != null) {

			values = line.split("\t");
			i = Integer.parseInt(values[1]);
			v = Integer.parseInt(values[2]);
			g = Integer.parseInt(values[3]);
			V[i] = v;
			G[i] = g;
			line = br.readLine();
		}

		br.close();
	}

	public static void randomSearch() throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(SOLUTION_RANDOM, "UTF-8");
		Random rn = new Random();
		Boolean[] S = new Boolean[N + 1];

		writer.println("Rucsac: Solutie Random n-" + N + " k-" + K);
		writer.println("V\tG\tSolutie");

		int bestG = 0, bestV = 0, tGreutate = 0, tValoare = 0, avgG = 0, avgV = 0;
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
				tGreutate = 0;
				tValoare = 0;
				for (int i = 0; i < N; i++) {
					if (S[i]) {
						tGreutate += G[i];
						tValoare += V[i];
					}
				}
				// print valid and best solution, update bestSolution
				if (tGreutate <= capacitateRucsac && tValoare > bestV) {
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
			} else {
				writer.println("\nNu exista solutie valida " + (z + 1));
			}
		}

		bestV = 0;
		bestG = 0;
		for (int z = 0; z < 10; z++) {
			int v = objRulari[z].getValoare();
			int g = objRulari[z].getGreutate();

			if (v > bestV) {
				bestV = v;
				bestG = g;
				bestS = objRulari[z].getBooleanSol().clone();
			}

			avgV += v;
			avgG += g;
		}
		avgV = avgV / 10;
		avgG = avgG / 10;
		// print Best Solution
		writer.println("\n!!Best Solution:");
		writer.println(printSolution(bestV, bestG, bestS));
		writer.println("\n!!Average Solution:");
		writer.println(avgV + " " + avgG);

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;

		writer.println("\nExecution time " + duration + "ms!");
		writer.close();
	}

	public static void exhaustiveSearch() throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(SOLUTION_EXHAUSTIVE, "UTF-8");
		writer.println("Rucsac: Solutie Exhaustiva n-" + N + "\n");
		writer.println("V\tG\tSolutie");

		String S, bestS = "";
		int bestG = 0, bestV = 0;
		int tGreutate, tValoare;

		// generate all solutions N must be < 60
		for (Long j = 1L; j < Math.floor(Math.pow(2, N)); j++) {
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

			if (j % 1000000 == 0)
				System.out.println(j);

			// update best solution &&
			if (tValoare > bestV && tGreutate < capacitateRucsac) {
				bestV = tValoare;
				bestG = tGreutate;
				bestS = S;
			}
		}

		// print execution time and Best Solution
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;

		writer.print("\nBest Solution: ");
		writer.println(printStringSolution(bestV, bestG, bestS));
		writer.println("\nExecution time " + duration + "ms!");
		writer.close();
	}

	public static void greedySearch() throws FileNotFoundException, UnsupportedEncodingException {
		long startTime = System.nanoTime();
		PrintWriter writer = new PrintWriter(SOLUTION_GREEDY, "UTF-8");
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
			public int compare(ObiectRucsac obj1, ObiectRucsac obj2) {
				return obj1.getCalitate().compareTo(obj2.getCalitate());
			}
		});
		
		int bestG = 0, bestV = 0, k=0;
		Boolean[] bestS = new Boolean[N + 1];
		for(int z=0; z<N+1; z++) {
			bestS[z] = false;
		}
		
		while(k<N) {
			if((bestG + objectsList.get(k).getGreutate()) < capacitateRucsac) {
				bestG+=objectsList.get(k).getGreutate();
				bestV+=objectsList.get(k).getValoare();
				bestS[objectsList.get(k).getIndex()] = true;
				k++;
			} else {
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

	private static String printSolution(int v, int g, Boolean[] s) {
		StringBuilder str;
		str = new StringBuilder("");
		str.append(v);
		str.append("\t");
		str.append(g);
		str.append("\t");
		for (int i = 0; i < N; i++) {
			if (s[i])
				str.append('1');
			else
				str.append('0');
		}

		return str.toString();
	}

	private static String printStringSolution(int v, int g, String s) {
		StringBuilder str;
		str = new StringBuilder("");
		str.append(v);
		str.append("\t");
		str.append(g);
		str.append("\t");
		str.append(s);
		return str.toString();
	}

}
