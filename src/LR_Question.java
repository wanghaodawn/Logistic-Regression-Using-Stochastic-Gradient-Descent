/**
 * 10605 - HW3
 * Logistic Regression
 * @author Hao Wang (haow2)
 * */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

public class LR_Question {
	/**
	 * Instance variables
	 * */
	private Set<String> labelSet = new HashSet<String>();
	private double[][] B;
	private int[] A;
	private List<String> labelTotal = new ArrayList<String>();
	private static int N;
	private static double lambda;
	private static double mu;
	private static int maxIteration;
	private static int D;
	private static String testFileName;
	
	private int correct = 0;
	private int total = 0;
	
	private List<Double> listSumLCL = new ArrayList<Double>();
	
	/**
	 * Static variables
	 * */
	public static BufferedReader br;
	
	/**
	 * Constant variables
	 * */
	private final double OVERFLOW_THRESHOLD = 20.0;
	
	private void init(String args[]) {
		N = Integer.parseInt(args[0]);
		lambda = Double.parseDouble(args[1]);
		mu = Double.parseDouble(args[2]);
		maxIteration = Integer.parseInt(args[3]);
		D = Integer.parseInt(args[4]);
		testFileName = args[5];
		
		// Hardcode all 17 labels into labelSet
		labelSet.add("Activity");
		labelSet.add("Agent");
		labelSet.add("Biomolecule");
		labelSet.add("CelestialBody");
		labelSet.add("ChemicalSubstance");
		labelSet.add("Device");
		labelSet.add("Event");
		labelSet.add("Location");
		labelSet.add("MeanOfTransportation");
		labelSet.add("Organisation");
		labelSet.add("Person");
		labelSet.add("Place");
		labelSet.add("Species");
		labelSet.add("SportsSeason");
		labelSet.add("TimePeriod");
		labelSet.add("Work");
		labelSet.add("other");
		
        labelTotal.addAll(labelSet);
        Collections.sort(labelTotal);
        
        B = new double[labelTotal.size()][N];
//        array = new Entry[labelTotal.size()][N];
//        for (int i = 0; i < labelTotal.size(); i++) {
//        	for (int j = 0; j < N; j++) {
//        		array[i][j] = new Entry();
//        	}
//        }
        
//        System.out.println(array.length + "\t" + array[0].length);
	}
	
	/**
	 * Compute the sigmoid value and considered overflow
	 * */
	private double sigmoid(double score) {
		if (score > OVERFLOW_THRESHOLD) {
			score = OVERFLOW_THRESHOLD;
		} else if (score < -OVERFLOW_THRESHOLD) {
			score = -OVERFLOW_THRESHOLD;
		}
		double exp = Math.exp(score);
		return exp / (1 + exp);
	}
	
	/**
	 * Read training data and train
	 * */
	private void train(double new_lambda, double constant) throws IOException {
        // Read string from the input file
        String currLine;
        
        int k = 0;
        A = new int[N];

        while ((currLine = br.readLine()) != null) {
            String[] ss = currLine.split("\t");
            if (ss.length != 3) {
            	continue;
            }
            
            // Get all labels of this line
            List<String> currLabels = new ArrayList<String>();
            String[] strLabels = ss[1].trim().split(",");
        	for (int j = 0; j < strLabels.length; j++) {
        		strLabels[j] = strLabels[j].replaceAll("\\W", "");
        		if (strLabels[j].length() == 0) {
        			continue;
        		}
        		currLabels.add(strLabels[j]);
        	}
            
            // Get all words of this line
            List<String> words = new ArrayList<String>();
            String[] strWords = ss[2].trim().split("\\s+");
            for (int j = 0; j < strWords.length; j++) {
            	strWords[j] = strWords[j].replaceAll("\\W", "");
            	if (strWords[j].length() == 0) {
            		continue;
            	}
            	words.add(strWords[j]);
            }
            
            // DEBUG
//            for (int i = 0; i < currLabels.size(); i++) {
//            	System.out.println(currLabels.get(i));
//            }
//            for (int i = 0; i < words.size(); i++) {
//            	System.out.println(words.get(i));
//            }
            
            k++;
            // Train for all labels
            for (int i = 0; i < labelTotal.size(); i++) {
            	String label = labelTotal.get(i);
            	
            	// Compute y
            	double y;
            	if (currLabels.contains(label)) {
            		y = 1.0;
            	} else {
            		y = 0.0;
            	}
            	// DEBUG
//            	System.out.println(label + "\t" + y);
            	
            	// Compute p & traverse all words
            	double p = 0.0;
            	for (int j = 0; j < words.size(); j++) {
            		// Compute the id
            		int id = words.get(j).hashCode() % N;
            		if (id < 0) {
            			id += N;
            		}
//            		System.out.println(id);
//            		System.out.println(words.get(j) + "\t" + array[i][id].getValue());
            		p += B[i][id];
//            		System.out.println(words.get(j) + "\t" + p);
            	}
            	p = sigmoid(p);
            	// DEBUG
//            	System.out.println(label + "\t" + p);
            	
            	// Update map & traverse all words
            	for (int j = 0; j < words.size(); j++) {
            		// Compute the id
            		int id = words.get(j).hashCode() % N;
            		if (id < 0) {
            			id += N;
            		}
                	double value = B[i][id];
                	int lastTimeModified = A[id];
                	
                	// DEBUG
//                	System.out.println(words.get(j) + "\tvalue: " + entry.getValue());
//                	System.out.println(words.get(j) + "\tlastTimeModified: " + entry.getLastTimeModified());
//                	System.out.println(words.get(j) + "\tk: " + k);
                	
                	value = value * Math.pow(constant, k - lastTimeModified);
                	value = value + new_lambda * (y - p);
                	
                	B[i][id] = value;
                	A[id] = k;
                	
                	// DEBUG
//                	System.out.println(words.get(j) + "\tvalue: " + entry.getValue());
//                	System.out.println(words.get(j) + "\tlastTimeModified: " + entry.getLastTimeModified());
            	}
            }
            
            // Keep k <= D
            if (k >= D) {
            	break;
            }
        }
        
        // Finished this iteration
        for (int i = 0; i < labelTotal.size(); i++) {
        	for (int j = 0; j < N; j++) {
        		double value = B[i][j];
        		
        		// Skip unnecessary entries
        		if (value == 0.0) {
        			continue;
        		}
        		
            	int lastTimeModified = A[j];
            	
            	value = value * Math.pow(constant, k - lastTimeModified);
            	B[i][j] = value;
        	}
        }
        
        // Q1
        double sumLCL = 0.0;
        File inFile = new File("abstract.small.train");
        
     // Read string from the input file
        BufferedReader brQ1 = null;
        brQ1 = new BufferedReader(new FileReader(inFile));
        
        k = 0;
        
        while ((currLine = brQ1.readLine()) != null) {
        	String[] ss = currLine.split("\t");
            if (ss.length != 3) {
            	continue;
            }
            
            // Get all labels of this line
            List<String> currLabels = new ArrayList<String>();
            String[] strLabels = ss[1].trim().split(",");
        	for (int j = 0; j < strLabels.length; j++) {
        		strLabels[j] = strLabels[j].replaceAll("\\W", "");
        		if (strLabels[j].length() == 0) {
        			continue;
        		}
        		currLabels.add(strLabels[j]);
        	}
            
        	// Get all words of this line
            List<String> words = new ArrayList<String>();
            String[] strWords = ss[2].trim().split("\\s+");
            for (int j = 0; j < strWords.length; j++) {
            	strWords[j] = strWords[j].replaceAll("\\W", "");
            	if (strWords[j].length() == 0) {
            		continue;
            	}
            	words.add(strWords[j]);
            }
            
            k++;
            
            // Train for all labels
            for (int i = 0; i < labelTotal.size(); i++) {
            	String label = labelTotal.get(i);
            	
            	// Compute y
            	double y;
            	if (currLabels.contains(label)) {
            		y = 1.0;
            	} else {
            		y = 0.0;
            	}
            	
            	// Compute p & traverse all words
            	double p = 0.0;
            	for (int j = 0; j < words.size(); j++) {
            		// Compute the id
            		int id = words.get(j).hashCode() % N;
            		if (id < 0) {
            			id += N;
            		}
//            		System.out.println(id);
//            		System.out.println(words.get(j) + "\t" + array[i][id].getValue());
            		p += B[i][id];
//            		System.out.println(words.get(j) + "\t" + p);
            	}
            	p = sigmoid(p);
            	// Q1
            	if (y >= 0.5) {
            		sumLCL += Math.log(p);
            	} else {
            		sumLCL += Math.log(1-p);
            	}
            }
            
            if (k >= D) {
            	break;
            }
        }
        brQ1.close();
        
        listSumLCL.add(sumLCL);
//        br.close();
        
        // Debug
//        System.out.println("size of map: " + map.size());
//        for (Integer id : map.keySet()) {
//        	Entry entry = map.get(id);
//        	
//        	double value = entry.getValue();
//        	
//        	System.out.println(id + "\t" + value);
//        }
	}
	
	/**
	 * Test the result
	 * @throws IOException 
	 * */
	private void test() throws IOException {
		// Read test data
        File inFile = new File(testFileName);
        
        // If file doesnt exists, then exit
        if (!inFile.exists()) {
            System.err.println("No file called: " + testFileName);
            System.exit(-1);
        }

        // Read string from the input file
        BufferedReader brTest = null;
        String currLine;
        
        brTest = new BufferedReader(new FileReader(inFile));
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

        while ((currLine = brTest.readLine()) != null) {
        	String[] ss = currLine.split("\t");
            if (ss.length != 3) {
            	continue;
            }
        	
        	// Get all labels of this line
            List<String> labels = new ArrayList<String>();
            String[] strLabels = ss[1].trim().split(",");
        	for (int j = 0; j < strLabels.length; j++) {
        		strLabels[j] = strLabels[j].replaceAll("\\W", "");
        		if (strLabels[j].length() == 0) {
        			continue;
        		}
        		labels.add(strLabels[j]);
        	}
            
            // Get all words of this line
            List<String> words = new ArrayList<String>();
            String[] strWords = ss[2].trim().split("\\s+");
            for (int j = 0; j < strWords.length; j++) {
            	strWords[j] = strWords[j].replaceAll("\\W", "");
            	if (strWords[j].length() == 0) {
            		continue;
            	}
            	words.add(strWords[j]);
            }
            
            // Test
            for (int i = 0; i < labelTotal.size(); i++) {
            	String label = labelTotal.get(i);
            	bw.append(label);
            	bw.append("\t");
            	
            	// Compute p
            	double p = 0.0;
            	for (int j = 0; j < words.size(); j++) {
            		// Compute the id
            		int id = words.get(j).hashCode() % N;
            		if (id < 0) {
            			id += N;
            		}
            		p += B[i][id];
            	}
            	p = sigmoid(p);
            	
            	// Add to sb
            	bw.append(String.format("%8.8f", p));
            	if (i != labelTotal.size() - 1) {
            		bw.append(",");
            	}
            	
            	// Get accuracy
            	if (labels.contains(label) && p >= 0.5) {
            		correct++;
            	} else if (!labels.contains(label) && p < 0.5) {
            		correct++;
            	}
            	total++;
            }
            bw.append("\n");
//            System.out.println(bw.toString());
        }
        
        bw.append("Accuracy: " + 1.0 * correct / total + "\n");
        
        brTest.close();
		bw.flush();
		bw.close();
	}
	
	// Q1
	private void printLCL() throws IOException {
		FileWriter outFile = new FileWriter("Q1");
		BufferedWriter bw = new BufferedWriter(outFile);
		
		for (int i = 0; i < listSumLCL.size(); i++) {
			bw.append(listSumLCL.get(i) + "\n");
		}
		
		bw.close();
	}
	
	/**
	 * Main function
	 * */
	public static void main(String args[]) throws IOException {
		if (args.length != 6) {
			throw new IllegalArgumentException("The number of args is not 6!");
		}
		
		LR_Question lr = new LR_Question();
		
		br = new BufferedReader(new InputStreamReader(System.in));
		
		//Initialize
		lr.init(args);
		
		// Train
		// Have to start from 0, or it cannot be divided by lambda, causes NaN
		for (int i = 1; i <= maxIteration; i++) {
			// Lambda decreases, which affects learning rate
			double new_lambda = lambda / i / i;
			double constant = 1.0 - 2.0 * new_lambda * mu;
			lr.train(new_lambda, constant);
		}
		
		// Test
		lr.test();
		
		br.close();
		
		// Q1
		lr.printLCL();
	}
}
