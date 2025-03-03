package com.example.Sample.util;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TextProcessor {

	public static List<String> tokenize(String text) {
	    if (text == null || text.trim().isEmpty()) {
	        return List.of(); // Return an empty list instead of null
	    }

	    return Arrays.stream(text.split("\\s+"))
	            .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase())
	            .filter(word -> !word.isEmpty())
	            .collect(Collectors.toList());
	}
    // Optional: Remove stopwords using a predefined list
    public static List<String> removeStopwords(List<String> tokens, List<String> stopwords) {
        return tokens.stream()
                .filter(word -> !stopwords.contains(word))
                .collect(Collectors.toList());
    }

    // Compute cosine similarity between two text lists
    public static double cosineSimilarity(List<String> list1, List<String> list2) {
        long commonWords = list1.stream().filter(list2::contains).count();
        return (double) commonWords / (Math.sqrt(list1.size()) * Math.sqrt(list2.size()));
    }
}
