/**
 * 10605 - HW3
 * Logistic Regression
 * @author Hao Wang (haow2)
 * */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CountLabels {
	private Set<String> set = new HashSet<String>();
	
	@SuppressWarnings("resource")
	private void begin(String fileName) throws IOException {
		// Read test data
        File inFile = new File(fileName);
        
        // If file doesnt exists, then exit
        if (!inFile.exists()) {
            System.err.println("No file called: " + fileName);
            System.exit(-1);
        }

        // Read string from the input file
        BufferedReader br = null;
        String currLine;
        
        br = new BufferedReader(new FileReader(inFile));

        while ((currLine = br.readLine()) != null) {
            String[] ss = currLine.split("\t");
            if (ss.length != 3) {
            	continue;
            }

            String[] labels = ss[1].trim().split(",");
        	for (int j = 0; j < labels.length; j++) {
        		String word = labels[j].replaceAll("\\W", "");
        		if (word.length() == 0) {
        			continue;
        		}

        		// Check unique word
        		if (!set.contains(word)) {
        			set.add(word);
        		}
        	}
        }
	}
	
	private void printLabels() {
		List<String> list = new ArrayList<String>();
		for (String key : set) {
			list.add(key);
		}
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException("The number of args is not 1!");
		}
		
		CountLabels cl = new CountLabels();
		cl.begin(args[0]);
		cl.printLabels();
	}
}
