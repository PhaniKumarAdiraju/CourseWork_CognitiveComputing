
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class AP {

	static double minSup;
	static double minConf;
	static ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> items_temp = new ConcurrentHashMap<>();
	static LinkedHashMap<String, SortedSet<Integer>> items = new LinkedHashMap<>();
	static ConcurrentHashMap<SortedSet<String>, Integer> items_Master = new ConcurrentHashMap<>();
	static ConcurrentHashMap<SortedSet<String>, Double> items_Master_Support = new ConcurrentHashMap<>();

	static ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> resultItemSets = new ConcurrentHashMap<>();

	static ConcurrentHashMap<String, Integer> newColumnItems_Const = new ConcurrentHashMap<>();
	static ConcurrentHashMap<String, Integer> newColumnItems_RD = new ConcurrentHashMap<>();
	static ConcurrentHashMap<String, NewColumn> newColumnMap = new ConcurrentHashMap<>();
	static Scanner sc = null;
	//static BufferedReader br = null;
	static double count;
	static double foundcases_ConstructionNoise = 0;
	static double foundcases_RD = 0;
	static int level;
	static String method;
	static double finalFreqItemSetSupport;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		long startTime = System.currentTimeMillis();
		method = args[0];

		
	    sc = new Scanner(System.in, "UTF-8");

		if (method.equalsIgnoreCase("-v")) {
			minSup = Double.parseDouble(args[1]);
			minConf = Double.parseDouble(args[2]);
			System.out.println(method + "," + minSup + "," + minConf);
			verbose();
			SortedSet<String> r = null;
			for (Entry<SortedSet<String>, SortedSet<Integer>> s : resultItemSets.entrySet()) {
				r = s.getKey();
			}
			Iterator<Entry<SortedSet<String>, SortedSet<Integer>>> iter1 = resultItemSets.entrySet().iterator();
			while (iter1.hasNext()) {
				Entry<SortedSet<String>, SortedSet<Integer>> items1 = iter1.next();
				finalFreqItemSetSupport = items1.getValue().size() / count;
				// System.out.println("support: "+finalFreqItemSetSupport);
			}
			HashMap<SortedSet<String>, SortedSet<String>> rules = generateRules(r);
			for (Entry<SortedSet<String>, SortedSet<String>> rule : rules.entrySet()) {
				// System.out.println(rule.getKey() + "=>" +rule.getValue());
				for (String k : rule.getKey()) {
					System.out.print("\"" + k + "\",");
				}
				System.out.print("=>");
				for (String v : rule.getValue()) {
					System.out.print("\"" + v + "\",");
				}
				Double sup = null;
				Double conf = null;
				if (items_Master_Support.containsKey(rule.getKey())) {
					sup = items_Master_Support.get(rule.getKey());
				}
				conf = finalFreqItemSetSupport / sup;
				System.out.print(" sup= " + sup + " conf= " + conf);
				if (sup < minSup) {
					System.out.print(" : [Pruned]");
				} else {
					if (conf < minConf) {
						System.out.print(" : [Pruned]");
					} else {
						System.out.print(" : [Not Pruned]");
					}
				}
				System.out.println();
			}

			System.out.println("total number of freq items: " + resultItemSets.keySet().size());

			System.out.println("total number of records: " + count);

			// System.out.println("Result item set: " +
			// resultItemSets.keySet());

		}

		if (method.equalsIgnoreCase("-g")) {
			System.out.println(method);
			generate();
			displayNewColumn();
		}

		if (method.equalsIgnoreCase("-m2")) {
			minSup = Double.parseDouble(args[1]);
			minConf = Double.parseDouble(args[2]);
			System.out.println(method + "," + minSup + "," + minConf);
			String fileName = args[3];
			master(fileName);
		}

		if (method.equalsIgnoreCase("-m")) {
			minSup = Double.parseDouble(args[1]);
			minConf = Double.parseDouble(args[2]);
			System.out.println(method + "," + minSup + "," + minConf);
			String fileName = args[3];
			master2(fileName);
			System.out.println("total number of freq items: " + resultItemSets.keySet().size());
			System.out.println("total number of records: " + count);
			// System.out.println("Result item set: " +
			// resultItemSets.keySet());
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("time to execute in ms: " + elapsedTime);

		System.out.println("end of program");

	}

	private static void master2(String fileName) {
		// TODO Auto-generated method stub

		masterVerbose();
		SortedSet<String> r = null;
		for (Entry<SortedSet<String>, SortedSet<Integer>> s : resultItemSets.entrySet()) {
			r = s.getKey();
		}
		Iterator<Entry<SortedSet<String>, SortedSet<Integer>>> iter1 = resultItemSets.entrySet().iterator();
		while (iter1.hasNext()) {
			Entry<SortedSet<String>, SortedSet<Integer>> items1 = iter1.next();
			finalFreqItemSetSupport = items1.getValue().size() / count;
			// System.out.println("support: "+finalFreqItemSetSupport);
		}
		HashMap<SortedSet<String>, SortedSet<String>> rules = generateRules(r);
		for (Entry<SortedSet<String>, SortedSet<String>> rule : rules.entrySet()) {
			// System.out.println(rule.getKey() + "=>" +rule.getValue());
			if (rule.getKey().contains("RD1") || rule.getKey().contains("RD2") || (rule.getValue().size()>=1)) {
				for (String k : rule.getKey()) {
					System.out.print("\"" + k + "\",");
				}
				System.out.print("=>");
				for (String v : rule.getValue()) {
					System.out.print("\"" + v + "\",");
				}
				Double sup = null;
				Double conf = null;
				if (items_Master_Support.containsKey(rule.getKey())) {
					sup = items_Master_Support.get(rule.getKey());
				}
				conf = finalFreqItemSetSupport / sup;
				System.out.print(" sup= " + sup + " conf= " + conf);
				if (sup < minSup) {
					System.out.print(" : [Pruned]");
				} else {
					if (conf < minConf) {
						System.out.print(" : [Pruned]");
					} else {
						System.out.print(" : [Not Pruned]");
					}
				}
				System.out.println();
			}
		}

	}

	private static void master(String fileName) {
		// TODO Auto-generated method stub
		ConcurrentHashMap<String, NewColumn> generatedMap = generate();
		writeGeneratedColumntoFile(fileName);
		displayNewColumn();
		System.out.println("items_master size: " + items_Master.size());
		verboseForMaster(generatedMap);
		/*
		 * for(Entry<String, NewColumn> s: generatedMap.entrySet()){
		 * if(s.getValue().RD==1){ System.out.println(s.getKey()); } }
		 */

	}

	private static void masterVerbose() {
		// TODO Auto-generated method stub

		try {
			//String input = br.readLine();
		    String input = sc.nextLine();
			while (sc.hasNextLine()) {
				count = count + 1;
				input = sc.nextLine();
				String[] values = input.split(",");

				int[] columnsRequired = { 5, 6, 7, 21 };

				for (int index = 0; index < columnsRequired.length; index++) {
					if (!(values[columnsRequired[index]].equalsIgnoreCase("Unspecified")
							|| values[columnsRequired[index]].isEmpty()
							|| values[columnsRequired[index]].equalsIgnoreCase("")
							|| values[columnsRequired[index]].equalsIgnoreCase(" "))) {
						String key;
						if (columnsRequired[index] == 21) {
							if (values[columnsRequired[index]].contains(
									"The Department of Housing Preservation and Development was not able to gain access")) {
								key = "RD1";
							} else {
								key = "RD2";
							}
						} else {
							key = values[columnsRequired[index]];
						}
						if (items.containsKey(key)) {
							SortedSet<Integer> ids = items.get(key);
							ids.add(Integer.parseInt(values[0]));
							items.put(key, ids);
						} else {
							SortedSet<Integer> ids = new TreeSet<>();
							ids.add(Integer.parseInt(values[0]));
							items.put(key, ids);
						}
					}
				}
			} // end of writing all records into hashmap

			// for singleitems : calculating Sup and Conf

			Iterator<Map.Entry<String, SortedSet<Integer>>> iter = items.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, SortedSet<Integer>> items1 = iter.next();
				double currentItemSupport = items1.getValue().size() / count;
				SortedSet<String> temp = new TreeSet<>();
				temp.add(items1.getKey());
				items_Master_Support.put(temp, currentItemSupport);
				if (currentItemSupport < minSup) {
					System.out.println("[Pruned] : " + items1.getKey() + " as support is " + currentItemSupport);
					iter.remove();
				} else {
					System.out.println("[Not Pruned] : " + items1.getKey() + " as support is " + currentItemSupport);
				}
			}

			level = 2;
			do {
				if (level == 2) {
					LinkedHashMap<String, SortedSet<Integer>> itemSet = items;
					items_temp = generateCandidateLevel2(itemSet);
					items_temp = pruneItemSets(items_temp);
				} else {
					// multiple
					items_temp = generateCandidateLevel_L(items_temp);
					items_temp = pruneItemSets(items_temp);
				}
				level++;
				if (items_temp.size() != 0) {
					resultItemSets = items_temp;
				}
			} while (items_temp.size() != 0);

			System.out.println("end of freq item sets at level: " + (level - 1));
		} finally {
			if (sc != null) {
				sc.close();
				System.out.println("end of verbose");
			}
		}

	}

	private static void verboseForMaster(ConcurrentHashMap<String, NewColumn> generatedMap) {
		// TODO Auto-generated method stub

		Iterator<Entry<SortedSet<String>, Integer>> iter = items_Master.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<SortedSet<String>, Integer> items1 = iter.next();
			double currentItemSupport = items1.getValue() / count;
			double currentItemConfidence = items1.getValue() / foundcases_RD;

			if (currentItemSupport < minSup) {
				System.out.println("[Pruned] : " + items1.getKey() + ", s= " + currentItemSupport + " c= "
						+ currentItemConfidence);
				iter.remove();
			} else {

				if (currentItemConfidence < minConf) {
					System.out.println("[Pruned] : " + items1.getKey() + ", s= " + currentItemSupport + " c= "
							+ currentItemConfidence);
					iter.remove();
				} else {
					System.out.println("[Not Pruned] : " + items1.getKey() + ", s= " + currentItemSupport + " c= "
							+ currentItemConfidence);
				}
			}
		}

		System.out.println("\n\n\nRemaining itemsets: ");
		Iterator<Entry<SortedSet<String>, Integer>> iter1 = items_Master.entrySet().iterator();
		while (iter1.hasNext()) {
			Entry<SortedSet<String>, Integer> items1 = iter1.next();
			System.out.println(items1.getKey());
		}

	}

	private static void writeGeneratedColumntoFile(String fileName) {
		// TODO Auto-generated method stub

		try {
			File fileTwo = new File(fileName);
			FileOutputStream fos = new FileOutputStream(fileTwo);
			PrintWriter pw = new PrintWriter(fos);

			for (Entry<String, NewColumn> m : newColumnMap.entrySet()) {
				pw.println(m.getKey() + "," + m.getValue().Construction + "," + m.getValue().RD);
			}

			pw.flush();
			pw.close();
			fos.close();
		} catch (Exception e) {
		}

	}

	private static void verbose() {
		// TODO Auto-generated method stub
		try {
			//String input = br.readLine();
			String input = sc.nextLine();
			while ((sc.hasNextLine())) {
				count = count + 1;
				input = sc.nextLine();
				String[] values = input.split(",");

				int[] columnsRequired = { 1, 5, 6, 7, 21 };

				for (int index = 0; index < columnsRequired.length; index++) {
					if (!(values[columnsRequired[index]].equalsIgnoreCase("Unspecified")
							|| values[columnsRequired[index]].isEmpty()
							|| values[columnsRequired[index]].equalsIgnoreCase("")
							|| values[columnsRequired[index]].equalsIgnoreCase(" "))) {
						if (items.containsKey(values[columnsRequired[index]])) {
							SortedSet<Integer> ids = items.get(values[columnsRequired[index]]);
							ids.add(Integer.parseInt(values[0]));
							items.put(values[columnsRequired[index]], ids);
						} else {
							SortedSet<Integer> ids = new TreeSet<>();
							ids.add(Integer.parseInt(values[0]));
							items.put(values[columnsRequired[index]], ids);
						}
					}
				}
			} // end of writing all records into hashmap

			// for singleitems : calculating Sup and Conf

			Iterator<Map.Entry<String, SortedSet<Integer>>> iter = items.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, SortedSet<Integer>> items1 = iter.next();
				double currentItemSupport = items1.getValue().size() / count;
				SortedSet<String> temp = new TreeSet<>();
				temp.add(items1.getKey());
				items_Master_Support.put(temp, currentItemSupport);
				if (currentItemSupport < minSup) {
					System.out.println("[Pruned] : " + items1.getKey() + " as support is " + currentItemSupport);
					iter.remove();
				} else {
					System.out.println("[Not Pruned] : " + items1.getKey() + " as support is " + currentItemSupport);
				}
			}

			level = 2;
			do {
				if (level == 2) {
					LinkedHashMap<String, SortedSet<Integer>> itemSet = items;
					items_temp = generateCandidateLevel2(itemSet);
					items_temp = pruneItemSets(items_temp);
				} else {
					// multiple
					items_temp = generateCandidateLevel_L(items_temp);
					items_temp = pruneItemSets(items_temp);
				}
				level++;
				if (items_temp.size() != 0) {
					resultItemSets = items_temp;
				}
			} while (items_temp.size() != 0);

			System.out.println("end of freq item sets at level: " + (level - 1));

		} finally {
			if (sc != null) {
				sc.close();
				System.out.println("end of verbose");
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> generateCandidateLevel_L(
			ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> items_temp2) {
		// TODO Auto-generated method stub
		ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> items_temp3 = new ConcurrentHashMap<>();

		for (int index = 0; index < items_temp2.size(); index++) {
			for (int index2 = index + 1; index2 < items_temp2.size(); index2++) {
				SortedSet<String> set1 = (SortedSet<String>) items_temp2.keySet().toArray()[index];
				SortedSet<String> set2 = (SortedSet<String>) items_temp2.keySet().toArray()[index2];

				SortedSet<String> intersectionSetKeys = findStringIntersection(set1, set2);

				if (intersectionSetKeys.size() != 0) {
					SortedSet<Integer> a1 = (SortedSet<Integer>) items_temp2.values().toArray()[index];
					SortedSet<Integer> a2 = (SortedSet<Integer>) items_temp2.values().toArray()[index2];
					SortedSet<Integer> resultIntSet;
					if (a1.size() < a2.size()) {
						resultIntSet = findIntegerIntersection(a1, a2);
					} else {
						resultIntSet = findIntegerIntersection(a2, a1);
					}
					items_temp3.put(intersectionSetKeys, resultIntSet);

				} // else no need to find intersection. So do nothing.
			}
		}

		return items_temp3;

	}

	private static ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> pruneItemSets(
			ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> pruneItems) {
		// TODO Auto-generated method stub
		Iterator<Entry<SortedSet<String>, SortedSet<Integer>>> iter1 = pruneItems.entrySet().iterator();
		while (iter1.hasNext()) {
			Entry<SortedSet<String>, SortedSet<Integer>> items1 = iter1.next();
			double currentItemSupport = items1.getValue().size() / count;
			items_Master_Support.put(items1.getKey(), currentItemSupport);
			if (currentItemSupport < minSup) {
				System.out.println("[Pruned in level " + level + "] : " + items1.getKey() + " as support is "
						+ currentItemSupport);
				iter1.remove();
			} else {
				System.out.println("[Not Pruned in level " + level + "] : " + items1.getKey() + " as support is "
						+ currentItemSupport);
			}
		}
		return pruneItems;

	}

	@SuppressWarnings("unchecked")
	static ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> generateCandidateLevel2(
			LinkedHashMap<String, SortedSet<Integer>> itemSet) {

		ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> items_temp1 = new ConcurrentHashMap<>();
		for (int index = 0; index < itemSet.size(); index++) {
			for (int index2 = index + 1; index2 < itemSet.size(); index2++) {
				SortedSet<Integer> a1 = (SortedSet<Integer>) itemSet.values().toArray()[index];
				SortedSet<Integer> a2 = (SortedSet<Integer>) itemSet.values().toArray()[index2];

				SortedSet<Integer> resultList;
				if (a1.size() < a2.size()) {
					resultList = findIntegerIntersection(a1, a2);
				} else {
					resultList = findIntegerIntersection(a2, a1);
				}
				if (resultList.size() != 0) {
					SortedSet<String> temp_key = new TreeSet<>();
					temp_key.add((String) itemSet.keySet().toArray()[index]);
					temp_key.add((String) itemSet.keySet().toArray()[index2]);
					items_temp1.put(temp_key, resultList);
				}
			}
		}

		return items_temp1;

	}

	static SortedSet<Integer> findIntegerIntersection(SortedSet<Integer> smallList, SortedSet<Integer> bigList) {
		SortedSet<Integer> resultSet = new TreeSet<>();
		for (Integer i : smallList) {
			if (bigList.contains(i)) {
				resultSet.add(i);
			}
		}
		return resultSet;
	}

	static SortedSet<String> findStringIntersection(SortedSet<String> set1, SortedSet<String> set2) {
		SortedSet<String> intersectionSet = new TreeSet<>();
		for (String s : set1) {
			if (set2.contains(s)) {
				intersectionSet.add(s.trim());
			}
		}
		SortedSet<String> resultSet = new TreeSet<>();
		if (intersectionSet.size() == (set1.size() - 1)) {
			for (String s : set1) {
				resultSet.add(s);
			}
			for (String s : set2) {
				resultSet.add(s);
			}
		}
		return resultSet;

	}

	private static ConcurrentHashMap<String, NewColumn> generate() {

		try {
			//String input = br.readLine();
			String input = sc.nextLine();
			while (sc.hasNextLine()) {
				// System.out.println("line is: " + input);
				// count = count.add(BigInteger.valueOf(1));
				input = sc.nextLine();
				count = count + 1;
				String[] values = input.split(",");

				if (values[5].equalsIgnoreCase("Construction")) {
					String key = values[8] + "-" + values[1].split(" ")[0];
					if (!newColumnItems_Const.containsKey(key)) {
						// System.out.println("key in Const: " + key);
						newColumnItems_Const.put(key, 1);
					}
				}

				if (values[5].contains("Noise")) {
					String key = values[8] + "-" + values[1].split(" ")[0];
					// System.out.println("key in Noise: " + key);
					if (newColumnItems_Const.containsKey(key)) {
						// System.out.println("Construction case found");
						newColumnItems_Const.put(key, newColumnItems_Const.get(key) + 1);
						NewColumn newColumn;
						if (newColumnMap.containsKey(values[0])) {
							newColumn = newColumnMap.get(values[0]);
						} else {
							newColumn = new NewColumn();
						}
						newColumn.Construction = 1;
						newColumnMap.put(values[0], newColumn);
					} else {
						NewColumn newColumn;
						if (newColumnMap.containsKey(values[0])) {
							newColumn = newColumnMap.get(values[0]);
						} else {
							newColumn = new NewColumn();
						}
						newColumn.Construction = 0;
						newColumnMap.put(values[0], newColumn);
					}
				}

				if (values[21].contains(
						"The Department of Housing Preservation and Development was not able to gain access")) {
					String key = values[1].split(" ")[0] + "-" + values[5] + "-" + values[7] + "-" + values[8];
					// System.out.println("key in RD: " + key);
					if (newColumnItems_RD.containsKey(key)) {
						// System.out.println("RD case found");
						newColumnItems_RD.put(key, newColumnItems_RD.get(key) + 1);
						NewColumn newColumn;
						if (newColumnMap.containsKey(values[0])) {
							newColumn = newColumnMap.get(values[0]);
						} else {
							newColumn = new NewColumn();
						}
						newColumn.RD = 1;
						newColumnMap.put(values[0], newColumn);
						if (method.equalsIgnoreCase("-m")) {
							SortedSet<String> set = new TreeSet<>();
							int[] columnsRequired = { 5, 6, 7 };
							for (int index = 0; index < columnsRequired.length; index++) {
								set.add(values[columnsRequired[index]]);
							}
							set.add("RD1");
							if (items_Master.containsKey(set)) {
								items_Master.put(set, items_Master.get(set) + 1);
							} else {
								items_Master.put(set, 1);
							}
						}
					} else {
						// System.out.println("key in RD2: " + key);
						newColumnItems_RD.put(key, 1);
						NewColumn newColumn;
						if (newColumnMap.containsKey(values[0])) {
							newColumn = newColumnMap.get(values[0]);
						} else {
							newColumn = new NewColumn();
						}
						newColumn.RD = 1;
						newColumnMap.put(values[0], newColumn);
						if (method.equalsIgnoreCase("-m")) {
							SortedSet<String> set = new TreeSet<>();
							int[] columnsRequired = { 5, 6, 7 };
							for (int index = 0; index < columnsRequired.length; index++) {
								set.add(values[columnsRequired[index]]);
							}
							set.add("RD1");
							if (items_Master.containsKey(set)) {
								items_Master.put(set, items_Master.get(set) + 1);
							} else {
								items_Master.put(set, 1);
							}
						}
					}
				} else {
					if (method.equalsIgnoreCase("-m")) {
						SortedSet<String> set = new TreeSet<>();
						int[] columnsRequired = { 5, 6, 7 };
						for (int index = 0; index < columnsRequired.length; index++) {
							if (!(values[columnsRequired[index]].equalsIgnoreCase("Unspecified")
									|| values[columnsRequired[index]].isEmpty()
									|| values[columnsRequired[index]].equalsIgnoreCase("")
									|| values[columnsRequired[index]].equalsIgnoreCase(" "))) {
								set.add(values[columnsRequired[index]]);
							}
						}
						set.add("RD0");
						if (items_Master.containsKey(set)) {
							items_Master.put(set, items_Master.get(set) + 1);
						} else {
							items_Master.put(set, 1);
						}
					}

				}

			}
		} finally {
			if (sc != null) {
				sc.close();
				// System.out.println("end of br");
				System.out.println("end of generate");
			}
		}
		return newColumnMap;

	}

	static HashMap<SortedSet<String>, SortedSet<String>> generateRules(SortedSet<String> set) {
		int n = set.size();

		List<SortedSet<String>> list = new ArrayList<>();
		for (int i = 0; i < (1 << n); i++) {
			SortedSet<String> s1 = new TreeSet<>();
			for (int j = 0; j < n; j++) {
				if ((i & (1 << j)) > 0) {
					if (!set.toArray()[j].toString().isEmpty()) {
						s1.add((String) set.toArray()[j]);
					}
				}
			}
			if (!s1.isEmpty()) {
				list.add(s1);
			}
		}
		HashMap<SortedSet<String>, SortedSet<String>> rules = new HashMap<>();
		for (SortedSet<String> key : list) {
			SortedSet<String> value = null;
			for (String s1 : key) {
				// System.out.print(s1+"=>");
				value = new TreeSet<>();
				for (int i = 0; i < set.size(); i++) {
					if (!key.contains(set.toArray()[i])) {
						value.add((String) set.toArray()[i]);
					}
				}
			}
			// System.out.println();
			// System.out.println("key - value :");
			// System.out.println(key + " "+value);
			rules.put(key, value);
		}

		return rules;

	}

	private static void displayNewColumn() {
		// TODO Auto-generated method stub

		System.out.println("UniqueKey,ConstructionFlag,RDFlag");
		for (Entry<String, NewColumn> items1 : newColumnMap.entrySet()) {
			System.out.println(items1.getKey() + "," + items1.getValue().Construction + "," + items1.getValue().RD);
			if (items1.getValue().Construction == 1) {
				foundcases_ConstructionNoise++;
			}
			if (items1.getValue().RD == 1) {
				foundcases_RD++;
			}
		}
		System.out.println("repeated cases for construction noise: " + foundcases_ConstructionNoise);
		System.out.println("repeated cases for No Access RD: " + foundcases_RD);
	}

	public static class NewColumn {
		int Construction;
		int RD;
	}

}
