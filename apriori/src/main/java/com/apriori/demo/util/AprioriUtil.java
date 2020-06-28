package com.apriori.demo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.apriori.demo.modal.GroceryData;

public class AprioriUtil {
	
	final int SUPPORT_VAL = 22;

	public static String process(List<GroceryData> data, int val) throws IOException {

		InputFile masterList = parseInput(data,val);

		LinkedHashSet<HashSet<Integer>> frequentItemSet = aprioriAlgorithm(masterList.bucketList,
				masterList.minimumSupportThreshold, masterList);

		// Display Frequent Item List
//		Boolean firstItem;
		StringBuilder items = new StringBuilder();
		items.append("Below are Frequent Item List :: ");
		items.append(System.lineSeparator());
		for (HashSet<Integer> frequentItem : frequentItemSet) {
			//firstItem = true;
			if (frequentItem.size() > 1) {
				for (Integer itemIndex : frequentItem) {					
					items.append(masterList.hashItemMap.get(itemIndex)); 
					items.append(",");
					
				}
				items.append(System.lineSeparator());

			}
		}
		return items.toString();

	}

	public static LinkedHashSet<HashSet<Integer>> aprioriAlgorithm(ArrayList<HashSet<Integer>> transaction,
			Integer minimumSupportCount, InputFile masterList) {

		HashSet<HashSet<Integer>> candidateKey = new HashSet<HashSet<Integer>>();
		HashMap<HashSet<Integer>, Integer> frequentItemMap = new HashMap<HashSet<Integer>, Integer>();
		LinkedHashSet<HashSet<Integer>> retFrequentItemSet = new LinkedHashSet<HashSet<Integer>>();

		// First-Pass
		// Generate Candidate Key of Set Size 1: C1
		System.out.println(" Starting Pass 1");
		candidateKey = generateUniqueItem(transaction);

		// Count frequency for candidate Key: L1
		frequentItemMap = countFrequency(candidateKey, transaction);

		// Prune L1 based on MST
		frequentItemMap = pruneFrequentItemMap(frequentItemMap, minimumSupportCount);

		// Add to return frequent set
		Boolean firstItem;
		HashSet<HashSet<Integer>> frequentItemSet = new HashSet<HashSet<Integer>>();
		for (Map.Entry<HashSet<Integer>, Integer> entry : frequentItemMap.entrySet()) {
			retFrequentItemSet.add(entry.getKey());
			// Add Item-Set to frequentItemSet for generating candidate key for Pass 2
			frequentItemSet.add(entry.getKey());
		}

		// Second-Pass
		// Generate Candidate Key of Set Size 2: C2 using L1
		System.out.println(" Starting Pass 2");
		candidateKey = generateCandidateKeyByJoining(frequentItemSet, 2);

		// Count frequency for candidate Key
		frequentItemMap = countFrequency(candidateKey, transaction);

		// Prune L2 based on MST
		frequentItemMap = pruneFrequentItemMap(frequentItemMap, minimumSupportCount);

		// Add Item-Set to frequentItemSet for generating candidate key for Next Pass
		frequentItemSet.clear();
		for (Map.Entry<HashSet<Integer>, Integer> entry : frequentItemMap.entrySet()) {
			retFrequentItemSet.add(entry.getKey());
			frequentItemSet.add(entry.getKey());
		}
		System.out.println(" Finished PAss 2");
		// Subsequent-Pass
		Integer k = 3;
		while (frequentItemSet.size() > 0) {
			System.out.println(" Starting Pass " + k);
			// Generate Candidate Key of set size k: C[K] using L[K-1] JOIN L[K-1]
			candidateKey = generateCandidateKeyByJoining(frequentItemSet, k);

			// Prune Candidate Key using Apriori Property
			candidateKey = pruneUsingApriori(candidateKey, frequentItemSet);

			// Count frequency for candidate Key
			frequentItemMap = countFrequency(candidateKey, transaction);

			// Filter candidate key based on minimum support threshold
			frequentItemMap = pruneFrequentItemMap(frequentItemMap, minimumSupportCount);

			// Add Item-Set to frequentItemSet for generating candidate key for Next Pass
			frequentItemSet.clear();
			for (Map.Entry<HashSet<Integer>, Integer> entry : frequentItemMap.entrySet()) {
				frequentItemSet.add(entry.getKey());
				retFrequentItemSet.add(entry.getKey());
			}
			System.out.println(" Finished Pass " + k);
			k = k + 1;

		}

		// Generate Frequent Itemset
		return retFrequentItemSet;
		//return new LinkedHashSet<HashSet<Integer>>(frequentItemSet);
	}

	public static HashSet<HashSet<Integer>> generateUniqueItem(ArrayList<HashSet<Integer>> transactions) {

		// Candidate List
		HashSet<HashSet<Integer>> candidateKey = new HashSet<HashSet<Integer>>();
		HashSet<Integer> uniqueItemSet = new HashSet<Integer>();
		// For each transaction find uniqueItem
		for (HashSet<Integer> t : transactions) {
			uniqueItemSet.addAll(t);
		}

		for (Integer uniqueItem : uniqueItemSet) {
			HashSet<Integer> temp = new HashSet<Integer>();
			temp.add(uniqueItem);
			candidateKey.add(temp);
		}
		return candidateKey;
	}

	public static HashSet<HashSet<Integer>> generateCandidateKeyByJoining(HashSet<HashSet<Integer>> seedCandidateKey,
			Integer setSize) {
		// Generate Candidate key by joining previous frequent item set
		HashSet<HashSet<Integer>> candidateKey = new HashSet<HashSet<Integer>>();

		for (HashSet<Integer> candidateKeyItemA : seedCandidateKey) {
			for (HashSet<Integer> candidateKeyItemB : seedCandidateKey) {
				HashSet<Integer> temp = new HashSet<Integer>();
				if (candidateKeyItemA != candidateKeyItemB) {
					temp.addAll(candidateKeyItemA);
					temp.addAll(candidateKeyItemB);
				}
				if (!candidateKey.contains(temp) && temp.size() == setSize) {
					candidateKey.add(temp);
				}
			}
		}

		return candidateKey;
	}

	public static HashMap<HashSet<Integer>, Integer> countFrequency(HashSet<HashSet<Integer>> candidateKeySet,
			ArrayList<HashSet<Integer>> transactions) {
		// Count frequency of candidate Key in list of transactions
		HashMap<HashSet<Integer>, Integer> frequentItemMap = new HashMap<HashSet<Integer>, Integer>();

		for (HashSet<Integer> candidateKey : candidateKeySet) {
			frequentItemMap.put(candidateKey, 0);
		}

		for (HashSet<Integer> transaction : transactions) {
			for (HashSet<Integer> candidateKey : candidateKeySet) {

				Boolean candidateKeyExist = true;
				for (Integer candidateItem : candidateKey) {
					if (!transaction.contains(candidateItem)) {
						candidateKeyExist = false;
						break;
					}
				}
				if (candidateKeyExist) {
					frequentItemMap.put(candidateKey, frequentItemMap.get(candidateKey) + 1);
				}
			}
		}
		return frequentItemMap;
	}

	public static HashMap<HashSet<Integer>, Integer> pruneFrequentItemMap(
			HashMap<HashSet<Integer>, Integer> frequentItemMap, Integer minimumSupportCount) {
		// Remove any candidate key having support count less than minimum support count
		HashMap<HashSet<Integer>, Integer> retMap = new HashMap<HashSet<Integer>, Integer>();
		for (Map.Entry<HashSet<Integer>, Integer> entry : frequentItemMap.entrySet()) {
			if (entry.getValue() >= minimumSupportCount) {
				retMap.put(entry.getKey(), entry.getValue());
			}
		}
		return retMap;
	}

	public static HashSet<HashSet<Integer>> pruneUsingApriori(HashSet<HashSet<Integer>> candidateKeySet,
			HashSet<HashSet<Integer>> previousFrequencyItemSet) {
		// For Every Candidate CK to be frequent, its subset has to be frequent also

		HashSet<HashSet<Integer>> frequentItemSet = new HashSet<HashSet<Integer>>();
		HashSet<HashSet<Integer>> candidateKeySubSet;
		for (HashSet<Integer> candidateKey : candidateKeySet) {
			// Find all subset of candidateKey with size K-1
			candidateKeySubSet = findSubset(candidateKey);
			Boolean isAllSubsetFrequent = true;
			for (HashSet<Integer> subSet : candidateKeySubSet) {
				if (!previousFrequencyItemSet.contains(subSet)) {
					isAllSubsetFrequent = false;
					break;
				}
			}
			if (isAllSubsetFrequent) {
				frequentItemSet.add(candidateKey);
			}
		}

		return frequentItemSet;
	}

	public static HashSet<HashSet<Integer>> findSubset(HashSet<Integer> set) {
		// Find all subset of size K-1 for k-size set
		HashSet<HashSet<Integer>> subset = new HashSet<HashSet<Integer>>();
		HashSet<Integer> tempSet;

		for (Integer i : set) {
			tempSet = new HashSet<Integer>(set);
			tempSet.remove(i);
			subset.add(tempSet);
		}

		return subset;
	}

	public static List<String> readInput() throws IOException {
		// Initialize Input Stream
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		String s;
		List<String> input = new ArrayList<String>();
		while ((s = in.readLine()) != null) {
			// System.out.println(s);
			input.add(s);
		}

		return input;
	}

	public static InputFile parseInput(List<GroceryData> input, int val) {
		ListIterator<GroceryData> litr = null;
		InputFile masterList = null;
		// Start Parsing
		masterList = new InputFile();
		litr = input.listIterator();

		HashSet<Integer> itemSetList;
		Map<String, GroceryData> consolidatedList = new HashMap<String, GroceryData>();
		GroceryData groceryData = null;
		while (litr.hasNext()) {
			groceryData = litr.next();

			if (consolidatedList.containsKey(groceryData.getKey())) {
				GroceryData temp = null;
				temp = consolidatedList.get(groceryData.getKey());
				temp.setItemDescription((new StringBuffer().append(temp.getItemDescription().trim()).append(",")
						.append(groceryData.getItemDescription().trim()).toString()));
				consolidatedList.put(temp.getKey(), temp);
			} else {
				consolidatedList.put(groceryData.getKey(), groceryData);
			}
		}
		
		// First Line must be Integer having minimumSupportThreshold
		try {
			masterList.minimumSupportThreshold = Integer.valueOf(val);
		} catch (NumberFormatException e) {
			// System.err.println("Error: Minimum Support Threshold '" + temp + "' is not
			// valid Integer.");
			System.exit(1);
		} catch (Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
			System.exit(1);
		}

		// Subsequent line must be itemSet in CSV format
		litr = new ArrayList<GroceryData>(consolidatedList.values()).listIterator();
		Integer itemNo = 1;
		String[] tempItemList;
		while (litr.hasNext()) {
			GroceryData temp = litr.next();
			if (temp == null)
				continue; // If line has valid items
			// Split CSV value
			tempItemList = temp.getItemDescription().split(","); // Assuming that no item has comma(,) in its name
			itemSetList = new HashSet<Integer>();

			for (String item : tempItemList) {
				item = item.trim();

				if (item.length() != 0) {
					if (!masterList.itemMap.containsKey(item)) {
						masterList.itemMap.put(item, itemNo);
						masterList.hashItemMap.put(itemNo, item);
						itemNo++;
					}
					itemSetList.add(masterList.itemMap.get(item));
				}
			}
			masterList.bucketList.add(itemSetList);
		}

		return masterList;
	}

	public static class InputFile {
		public Integer minimumSupportThreshold;
		public ArrayList<HashSet<Integer>> bucketList;
		public HashMap<String, Integer> itemMap;
		public HashMap<Integer, String> hashItemMap;

		public InputFile() {
			minimumSupportThreshold = 2;
			bucketList = new ArrayList<HashSet<Integer>>(); // Contains All ItemNo.
			itemMap = new HashMap<String, Integer>(); // Contains Item & No. Mapping
			hashItemMap = new HashMap<Integer, String>(); // Contains No. & Item Mapping
		}
	}
}
