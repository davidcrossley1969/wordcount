package com.dsc.java.wordcount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rest")
public class Controller {

	/*
	 * @PostMapping public ResponseEntity<String> uploadFile(@RequestPart("file")
	 * MultipartFile file) { if (null == file.getOriginalFilename()) { return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST); } try { byte[] bytes =
	 * file.getBytes(); Path path = Paths.get(file.getOriginalFilename());
	 * Files.write(path, bytes); System.out.println(path.getFileName()); } catch
	 * (IOException e) { System.out.println(e.getMessage()); } return new
	 * ResponseEntity<>("Good Job", HttpStatus.OK); }
	 * 
	 */
	@GetMapping("/getHello")
	  public String getHello() {
	    return "Hello David";
	  }
	
	@PostMapping("/upload/txt")
	public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
		if (null == file.getOriginalFilename()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		String txtContent = "";
		
			try {
				InputStream input = file.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(input));
				String line = "";

				while ((line = rd.readLine()) != null) {
					if (!"null".equalsIgnoreCase(line)) {
						txtContent += line;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		List<String> splitData = Arrays.asList(txtContent.trim().split("\\s+"));

		StringBuilder response = new StringBuilder();

		response.append(wordCountAndAverage(splitData));

		HashMap<Integer, HashMap<String, Integer>> wordsBySizeMap = new HashMap<Integer, HashMap<String, Integer>>();

		// Add words to the wordsBySizeMap
		addWordsToMap(splitData, wordsBySizeMap);

		// now get the data output from wordsBySizeMap
		List<String> resp2 = getWordlengthInfo(wordsBySizeMap);
		for (String res : resp2) {
			response.append(res);
		}

		response.append(whichWordLengthAppearMost(wordsBySizeMap));

		//return Response.ok(response.toString()).build();
		return ResponseEntity.ok(response.toString());
	}

	private String whichWordLengthAppearMost(HashMap<Integer, HashMap<String, Integer>> wordsBySizeMap) {
		// The most frequently occurring word length is 2, for word lengths of 4 & 5
		HashMap<String, Integer> maxKeys = new HashMap<String, Integer>();

		for (Entry<Integer, HashMap<String, Integer>> e : wordsBySizeMap.entrySet()) {
			maxKeys.put(e.getKey().toString(), e.getValue().size());
		}

		int maxLen = 0;
		for (Entry<String, Integer> e : maxKeys.entrySet()) {
			int len = e.getValue();

			if (len > maxLen) {
				maxLen = len;
			}
		}
		ArrayList<String> words = new ArrayList<String>();
		for (Entry<String, Integer> e : maxKeys.entrySet()) {
			if (e.getValue().equals(maxLen)) {
				words.add(e.getKey().toString());
			}
		}
		StringBuilder sb = (new StringBuilder());
		int count = 0;
		for (String word : words) {
			if (count == 0) {
				sb.append(word);
				count++;
			} else {
				sb.append(" & " + word);
			}

		}
		return "The most frequently occurring word length is " + maxLen + " for word lengths of " + sb;
	}

	private List<String> getWordlengthInfo(HashMap<Integer, HashMap<String, Integer>> wordsBySizeMap) {
		List<String> list = new ArrayList<>();
		for (Entry<Integer, HashMap<String, Integer>> key : wordsBySizeMap.entrySet()) {
			list.add("Number of words of length " + key.getKey() + " is " + wordsBySizeMap.get(key.getKey()).size()
					+ "\r\n");
		}
		return list;
	}

	private void addWordsToMap(List<String> splitData, HashMap<Integer, HashMap<String, Integer>> wordsBySizeMap) {
		for (String currentWordLenght : splitData) {
			if (wordsBySizeMap.get(currentWordLenght.length()) == null) {
				HashMap<String, Integer> wordsOfThisLenght = new HashMap<String, Integer>();
				int lenghtOfCurrentWord = currentWordLenght.length();
				int wordPosition = 0;
				for (String currentWord : splitData) {
					wordPosition = wordPosition + 1;
					if (currentWord.length() == lenghtOfCurrentWord) {
						wordsOfThisLenght.put(currentWord, wordPosition);
					}
				}
				wordsBySizeMap.put(currentWordLenght.length(), wordsOfThisLenght);
			}
		}
	}

	private String wordCountAndAverage(List<String> splitData) {

		int chars = 0;
		for (String s : splitData) {
			chars = chars + s.length();
		}
		int size = splitData.size();
		double avg = (double) chars / size;

		return "Word count = " + splitData.size() + "\r\n" + "Average word length = " + avg + "\r\n";
	}
}