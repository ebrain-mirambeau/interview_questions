//Author: Ebrain O. Mirambeau
//Date: May 26th 2014
//Purpose: Language identification system

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


public class Language {
	
	private HashMap<String, HashMap<String, Integer>> dict; //Dictionary of words from training set
	private HashMap<String, Integer> lang_word_counts; //Number of words in each language
	private HashMap<String, Integer> word_counts;
	private double total_word_count = 0;
	private HashMap<Integer, String> predictions = new HashMap<Integer, String>(); //Sentence language predictions (language codes)
	private HashMap<Integer, String> language_ids = new HashMap<Integer, String>(); //Language codes (a set)
	private HashMap<Integer, String> actual_languages = new HashMap<Integer, String>(); //Language codes from testing set (used for comparison with predictions)
	
	public void training_procedures(String string){
		// Input: A file path to the training set
		// Output: None
		// Purpose: Basic calculations and counts
		
		Charset charset = Charset.forName("UTF-8");//UTF-8
		Path p = Paths.get(string);
		dict = new HashMap<String, HashMap<String, Integer>>();
		lang_word_counts = new HashMap<String, Integer>();
		word_counts = new HashMap<String, Integer>();
		
		
		try (BufferedReader reader = Files.newBufferedReader(p, charset)) {
		    String line = null;
		    
		    while ((line = reader.readLine()) != null) {
		    	
		    	String[] string2 = line.split("[\t\t]");
		    	String sentence = string2[0];
		    	String language = string2[1];
		    	String[] words = sentence.split("[ ]+");
		    	total_word_count += words.length;
		    	String pattern = "[^\u0041-\u005A\u0061-\u007A\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF\u0100-\u016F\u0180-\u01BF\u0114-\u024F]+";
		    	
		    	if (! lang_word_counts.containsKey(language)){
		    		lang_word_counts.put(language, words.length);
		    	}
		    	else{
		    		lang_word_counts.put(language, lang_word_counts.get(language) + words.length);
		    	}
		    	
		    	for (int i = 0; i < words.length; i++){
		    		String word = words[i].replaceAll(pattern, "");
		    		
		    		
		    		if (! dict.containsKey(word)){
		    			HashMap<String, Integer> lang = new HashMap<String, Integer>();
		    			lang.put(language, 1);
		    			dict.put(word, lang);
		    			word_counts.put(word, 1);
		    		}
		    		else{
		    			HashMap<String, Integer> temp = dict.get(word); // returns another HashMap
		    			word_counts.put(word, word_counts.get(word)+1);
		    			
		    			if (! temp.containsKey(language)){
		    				temp.put(language, 1);
		    				dict.put(word, temp);
		    			}
		    			else{
		    				temp.put(language, temp.get(language)+1);
		    			}
		    		}
		    	}

		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		
	}
	
	public void testing_procedures(String string){
		// Input:  A file path to the testing set
		// Output: None
		// Purpose: Compute appropriate word counts 
		
		HashMap<String, Double> language_probabilities = new HashMap<String, Double>();
		
		Charset charset = Charset.forName("UTF-8");//UTF-8
		Path p = Paths.get(string);

		int number_of_sentences = 0;
		int number_of_language_ids = 0;
		int counter = 0;
		
		try (BufferedReader reader = Files.newBufferedReader(p, charset)) {
		    String line = null;
		    
		    while ((line = reader.readLine()) != null) {
		    	double prediction_probability;
		    	
		    	String[] string2 = line.split("[\t]+");
		    	String sentence = string2[0];
		    	String language = string2[1];
		    	
		    	String pattern = "[^\u0041-\u005A\u0061-\u007A\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF\u0100-\u016F\u0180-\u01BF\u0114-\u024F ]+";
		    	sentence = sentence.replaceAll(pattern, "");
		    	actual_languages.put(counter, language);
		    	counter++;
		    	
		    	if(!language_ids.containsValue(language)){
		    		language_ids.put(number_of_language_ids, language);
			    	number_of_language_ids++;
		    	}
		    	
		    	
		    	String[] words = sentence.split("[ ]+");
		    	Object[] langs = lang_word_counts.keySet().toArray();
		    	
		    	for (int i = 0; i < langs.length; i++){
		    		language_probabilities.put((String) langs[i], Math.log(lang_word_counts.get((String) langs[i])/total_word_count));
		    	}
		    		
		    	for (int j = 0; j < words.length; j++){
		    		language_probabilities = conditional_probabilities(langs, language_probabilities, words[j]);
		    	}
		    	
		    	//Determine probability of language
		    	
		    	String prediction = (String) langs[0];
		    	prediction_probability = language_probabilities.get(prediction);
	
		    	
		    	for(int i = 0; i < langs.length; i++){
		    		if (language_probabilities.get(langs[i]) > prediction_probability){
		    			prediction = (String) langs[i];
		    			prediction_probability = language_probabilities.get(prediction);
		    		}
		    	}
		 
		    	
		    	predictions.put(number_of_sentences, prediction);
		    	number_of_sentences++;
		    }	
		    
		    } catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		    }
		
	}
	
	
	public HashMap<String, Double> conditional_probabilities(Object[] langs, HashMap<String, Double> lp, String word){
		// Input: 1) langs - language word counts 2) lp - probability calculation HashMap 3) A word
		// Output: Updated calculations
		// Purpose: For each word in a sentence a probability is calculated for each language in questions
		
		double number_of_classes;
		double total_number_of_words;
		
		Object[] classes = language_ids.keySet().toArray();
		number_of_classes = classes.length;
		Object[] lang_codes = language_ids.keySet().toArray();
		
		if(dict.containsKey(word)){
			HashMap<String, Integer> temp = dict.get(word);
			Object[] word_counts = temp.values().toArray();
			total_number_of_words = 0;
			
			for(int i = 0; i < word_counts.length; i++){ //Calculate local word totals (total number of ocurrances across languages)
				total_number_of_words += (int)word_counts[i];
			}
			
			// Expression: Sum [log P(word|language)]
			// Laplace smoothing
			// If word encountered in not in training dictionary then, log(1/number of classes) is factored in the calculation
			for(int i = 0; i < lang_codes.length; i++){
				
				if(temp.containsKey(language_ids.get(lang_codes[i]))){
					lp.put(language_ids.get(lang_codes[i]), lp.get(language_ids.get(lang_codes[i])) 
							+ Math.log(temp.get(language_ids.get(lang_codes[i]))+1/(total_number_of_words + number_of_classes)));
				}
				else{
					lp.put(language_ids.get(lang_codes[i]), lp.get(language_ids.get(lang_codes[i])) 
							+ Math.log(1/(total_number_of_words + number_of_classes)));
				}
				
			}

		}
		else{ // log(1/number of classes)
			for(int i = 0; i < lang_codes.length; i++){

				lp.put(language_ids.get(lang_codes[i]), lp.get(language_ids.get(lang_codes[i])) 
						+ Math.log(1/number_of_classes));
			}			
			
		}
		
		return lp;
		
	}
	
	
	public void generate_statistics(){
		// Input: None
		// Output: None
		// Purpose: Output match counts, total word counts, and accuracy statistics
		
		int matches = 0;
		int grand_total = 0;
		
		Object[] temp = predictions.keySet().toArray();
		 
		for(int i = 0; i < temp.length; i++){
			
			if (predictions.get(i).equals((String)actual_languages.get(i))){
				matches++;
			}
			grand_total++;
		}
		
		System.out.print("Correct predictions: " + matches + "\n");
		System.out.print("Sentence total: " + grand_total + "\n");
		System.out.print("Accuracy: " + (double) matches/grand_total + "\n"); 
	}
	
	public static void main(String[] argv){
		Language lang = new Language();
		lang.training_procedures(argv[1]);
		lang.testing_procedures(argv[2]);
		lang.generate_statistics();
	}

}
