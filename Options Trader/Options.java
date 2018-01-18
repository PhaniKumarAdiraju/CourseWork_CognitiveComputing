/* 
 *  if
 *  openInt increases and lastSalePrice increases => strongmarket
 *  openInt decreases and lastSalePrice decreases => strongmarket
 *  else
 *   => weakmarket
 * 
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class Options {

	static Scanner sc = null;
	static String method ;
	static double count;
	static String fileName;
	static TreeMap<String, SortedSet<Integer>> items = new TreeMap<>();
	static ConcurrentHashMap<SortedSet<String>, Double> items_Master_Support = new ConcurrentHashMap<>();
	static ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> items_temp = new ConcurrentHashMap<>();
	static ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> resultItemSets = new ConcurrentHashMap<>();

	
	static int level;
	static double minSup;
	static double minConf;
	static double finalFreqItemSetSupport;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		long startTime = System.currentTimeMillis();
		method = args[0];

		sc = new Scanner(System.in, "UTF-8");
		if(method.equalsIgnoreCase("-g")){
			//generate method
			 
			generate();
			
		}
		else if(method.equalsIgnoreCase("-m")){
			//master method
			minSup = Double.parseDouble(args[1]);
			minConf = Double.parseDouble(args[2]);
			master();
			SortedSet<String> r = null;
			

			Iterator<Entry<SortedSet<String>, SortedSet<Integer>>> iter1 = resultItemSets.entrySet().iterator();
			while (iter1.hasNext()) {
				Entry<SortedSet<String>, SortedSet<Integer>> items1 = iter1.next();
				finalFreqItemSetSupport = items1.getValue().size() / count;
				// System.out.println("support: "+finalFreqItemSetSupport);
			}
			for (Entry<SortedSet<String>, SortedSet<Integer>> s : resultItemSets.entrySet()) {
				r = s.getKey();
			
			
			if(r!=null){
				
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
			}}else{
				System.out.println("All values pruned with given support and conf. Please reduce the values");
			}
			}
			//System.out.println("total number of freq items: " + resultItemSets.keySet().size());

			System.out.println("total number of records: " + count);

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("time to execute in ms: " + elapsedTime);

			System.out.println("end of program");

		}

	}
	
	private static void master() {
		// TODO Auto-generated method stub
		try {
			//String input = br.readLine();
			String input = sc.nextLine();
			while ((sc.hasNextLine())) {
				count = count + 1;
				input = sc.nextLine();
				String[] values = input.split(",");

				
				for(int index=2;index<values.length;index++){
					if(items.containsKey(values[index])){
						SortedSet<Integer> ids = items.get(values[index]);
						ids.add(Integer.parseInt(values[0]));
						items.put(values[index], ids);
					} else{
						SortedSet<Integer> ids = new TreeSet<>();
						ids.add(Integer.parseInt(values[0]));
						items.put(values[index], ids);
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
					//System.out.println("[Pruned] : " + items1.getKey() + " as support is " + currentItemSupport);
					iter.remove();
				} else {
					//System.out.println("[Not Pruned] : " + items1.getKey() + " as support is " + currentItemSupport);
				}
			}

			level = 2;
			do {
				if (level == 2) {
					TreeMap<String, SortedSet<Integer>> itemSet = items;
					items_temp = generateCandidateLevel2(itemSet);
					items_temp = pruneItemSets(items_temp);
				} else {
					// multiple
					//System.out.println("\n\n\n****************generating candidates level: "+level+"\n**********************\n\n");
					items_temp = generateCandidateLevel_L(items_temp);
					items_temp = pruneItemSets(items_temp);
				}
				level++;
				if (items_temp.size() != 0) {
					resultItemSets = items_temp;
				}
			} while (items_temp.size() != 0 && level<4);

			//System.out.println("end of freq item sets at level: " + (level - 1));

		} finally {
			if (sc != null) {
				sc.close();
			//	System.out.println("end of verbose");
			}
		}

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
			for (@SuppressWarnings("unused") String s1 : key) {
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

	
	@SuppressWarnings("unchecked")
	static ConcurrentHashMap<SortedSet<String>, SortedSet<Integer>> generateCandidateLevel2(
			TreeMap<String, SortedSet<Integer>> itemSet) {

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
			//	System.out.println("[Pruned in level " + level + "] : " + items1.getKey() + " as support is "+ currentItemSupport);
				iter1.remove();
			} else {
			//	System.out.println("[Not Pruned in level " + level + "] : " + items1.getKey() + " as support is "+ currentItemSupport);
			}
		}
		return pruneItems;

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


	private static void generate() {

		try {
			//String input = br.readLine();
			
			/*File fileTwo = new File(fileName);
			FileOutputStream fos = new FileOutputStream(fileTwo);
			PrintWriter pw = new PrintWriter(fos);*/
			
			String input = sc.nextLine();
			String oldDate = null;
		    String currentDate;
		    
		    int StrikePrice_old=-1,StrikePrice_current=0;
		    Double LSP_old=null, LSP_current=null;
		    Long OpenInt_old_call=null, OpenInt_current_call=null;
		    Long OpenInt_old_put=null, OpenInt_current_put=null;
		    
		    
			while (sc.hasNextLine()) {
				// System.out.println("line is: " + input);
				// count = count.add(BigInteger.valueOf(1));
				input = sc.nextLine();
				//System.out.println("input: "+input);
				count = count + 1;
				String[] values = input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				
				if(DoubleCheck(values[0].replaceAll("^\"|\"$", "").replaceAll("\\$", ""))){
					LSP_current = Double.parseDouble(values[0].replaceAll("^\"|\"$", "").replaceAll("\\$", ""));
					if(LSP_old==null){
						LSP_old = LSP_current;
					}
				}
				
				if(LongCheck(values[8].replaceAll("^\"|\"$", ""))){
					OpenInt_current_call = Long.parseLong(values[8].replaceAll("^\"|\"$", ""));
					if(OpenInt_old_call==null){
						OpenInt_old_call = OpenInt_current_call;
					}
				}
				
				if(LongCheck(values[17].replaceAll("^\"|\"$", ""))){
					OpenInt_current_put = Long.parseLong(values[17].replaceAll("^\"|\"$", ""));
					if(OpenInt_old_put==null){
						OpenInt_old_put = OpenInt_current_put;
					}
				}
				
			
				if(IntegerCheck(values[10].replaceAll("^\"|\"$", ""))){
					StrikePrice_current = Integer.parseInt(values[10].replaceAll("^\"|\"$", ""));
					if(StrikePrice_old==-1){
						StrikePrice_old = StrikePrice_current;
					}
				}
				
				currentDate = values[1].split(",")[0].trim();
				if(oldDate==null || oldDate.length()==0){
					oldDate = currentDate;
				}
				
				
				
				System.out.println(StrikePrice_current+","+LSP_current+","+compareLSP(LSP_old, LSP_current)+
						","+compareOpenInt_call(OpenInt_old_call, OpenInt_current_call)+
						","+marketStrength(compareLSP(LSP_old, LSP_current),compareOpenInt_call(OpenInt_old_call, OpenInt_current_call)));
				
				System.out.println(StrikePrice_current+","+LSP_current+","+compareLSP(LSP_old, LSP_current)+
						","+compareOpenInt_put(OpenInt_old_put, OpenInt_current_put)+
						","+marketStrength(compareLSP(LSP_old, LSP_current),compareOpenInt_put(OpenInt_old_put, OpenInt_current_put)));
				
						
				oldDate = currentDate;
				
			} 
		}finally {
				if (sc != null) {
					sc.close();
					// System.out.println("end of br");
					//System.out.println("end of generate");
				}
			}
			//return newColumnMap;

		
	}
	
	public static String marketStrength(String LSPresult, String openIntResult) {
		if(LSPresult.contains("Increase") && openIntResult.contains("Increase")){
			return "StrongMarket";
		} else if(LSPresult.contains("Decrease") && openIntResult.contains("Decrease")){
			return "StrongMarket";
		} /*else if(LSPresult.contains("Stable") || openIntResult.contains("Stable")){
			return "StableMarket";
		}*/
		else {
			return "WeakMarket";
		}
	}

	public static boolean IntegerCheck(String str) {
		boolean integerFlag = false;
		try {
			Integer.parseInt(str);
			integerFlag = true;
		} catch (NumberFormatException ex) {
		}
		return integerFlag;
	}
	
	public static boolean DoubleCheck(String str) {
		boolean DoubleFlag = false;
		try {
			Double.parseDouble(str);
			DoubleFlag = true;
		} catch (NumberFormatException ex) {
		}
		return DoubleFlag;
	}
	
	public static boolean LongCheck(String str) {
		boolean LongFlag = false;
		try {
			Long.parseLong(str);
			LongFlag = true;
		} catch (NumberFormatException ex) {
		}
		return LongFlag;
	}
	
	public static String compareLSP(Double LSPold, Double LSPcurrent){
		if(LSPcurrent >= LSPold){
			return "LSP_Increase";
		}
		else{
			return "LSP_Decrease";
		}
		/*else{
			return "LSP_Stable";
		}*/
	}
	
	public static String compareOpenInt_call(Long openIntCall_Old, Long openIntCall_current){
		if(openIntCall_current >= openIntCall_Old){
			return "OpenIntCall_Increase";
		}
		else{
			return "OpenIntCall_Decrease";
		}
		/*else{
			return "OpenIntCall_Stable";
		}*/
	}
	
	public static String compareOpenInt_put(Long openIntPut_Old, Long openIntPut_current){
		if(openIntPut_current >= openIntPut_Old){
			return "OpenIntPut_Increase";
		}
		else {
			return "OpenIntPut_Decrease";
		}
		/*else{
			return "OpenIntPut_Stable";
		}*/
	}
	

}
