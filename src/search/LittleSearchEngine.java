package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Kavya
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	@SuppressWarnings("resource")
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		
		// get the noise words from file 
		File file = new File(noiseWordsFile);
		Scanner scan = new Scanner(file);
		
		// load them into the hash table
		while (scan.hasNext()) 
		{
			String str = scan.next();
			noiseWords.put(str,str);
		}
		
		// indexing the keywords
		File doc = new File(docsFile);
		scan = new Scanner(doc);
		while (scan.hasNext()) 
		{
			String docFile = scan.next();
			HashMap<String,Occurrence> keywords = loadKeyWords(docFile);
			mergeKeyWords(keywords);
		}
		scan.close();		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD		
		File doc = new File(docFile);
		Scanner scan = new Scanner(doc);
		// a HashMap for the keywords
		HashMap<String,Occurrence> keywords = new HashMap<String,Occurrence>(1000,2.0f);
		
		while(scan.hasNext())
		{
			String word = getKeyWord(scan.next().trim());
			if(word != null)
			{
				// create a new instance of Occurrence while checking if the keyword is already present in the hashMap
				// if found, then increment the frequency
				Occurrence occ = keywords.get(word);
				if(occ == null)
				{
					Occurrence next_occ = new Occurrence(docFile,1);
					keywords.put(word,next_occ);
				}
				else
				{
					occ.frequency+=1;
				}
			} 
		}
		scan.close();
		return keywords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD

		Set<String> value = kws.keySet();		
		for(String str: value){
			ArrayList<Occurrence> occ = keywordsIndex.get(str);
			// check if keywordsIndex has the word
			if(keywordsIndex.get(str) == null)
			{
				occ = new ArrayList<Occurrence>();
			}
			occ.add(kws.get(str));
			keywordsIndex.put(str, occ);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		String str = word.toLowerCase(); 
		int length = str.length()-1;  
	//	System.out.println("word1 : "+str);
	//	System.out.println("word length : "+length);
		
		if(length==0)
		{
	//		System.out.println("word length is 0!!");
			return null; 
		}
		
		//if the word has a number
		if(str.matches(".*\\d.*"))  
		{
	//		System.out.println("word is a number!");
			return null; 
		}
		
		//Searching for punctuation at the beginning of the word, if found then remove them 
		int i = 0;
		while(length >-1 && (Character.toString(str.charAt(i)).equals(".") || Character.toString(str.charAt(i)).equals(",") || Character.toString(str.charAt(i)).equals("?") || Character.toString(str.charAt(i)).equals(":") || Character.toString(str.charAt(i)).equals(";") || Character.toString(str.charAt(i)).equals("!")))
		{ 
			str = str.substring(i+1,length); 
			i++; 
		}
	//	System.out.println("punctuation removed from start! : "+s1);
		
		//Searching for punctuation in the end of the word, if found then remove them
		length = str.length()-1;
		while(length >-1 && (Character.toString(str.charAt(length)).equals(".") || Character.toString(str.charAt(length)).equals(",") || Character.toString(str.charAt(length)).equals("?") || Character.toString(str.charAt(length)).equals(":") || Character.toString(str.charAt(length)).equals(";") || Character.toString(str.charAt(length)).equals("!")))
		{ 
			str = str.substring(0,length); 
			length--; 
		}
	//	System.out.println("punctuation removed from end! : "+str);
	
		if(length==0)
		{
	//		System.out.println("word length is 0!");
			return null; 
		}
		
		//Searching for punctuation in the middle of the word, if found then retun null
		if(str.contains(".") || str.contains(",") || str.contains("'")  || str.contains("-") || str.contains("?") || str.contains(":") || str.contains(";") || str.contains("!"))
		{
	//		System.out.println("punctuations in the middle, hence null!");
			return null;
		}
		
		//Searching in noiseWords hashtable, if found then return null, else return the word 
		String found = null; 	
 		found = noiseWords.get(str);
		if(found!=null)
		{
	//		System.out.println("word not found in noiseWords!");
			return null; 
		}
		
		else 
		{
	//		System.out.println("word found in noiseWords!");
			return str; 
		}	
			
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		int size = occs.size();
		if(size == 1) 
		{
			return null;
		}
		System.out.println("Size : "+size);
		Occurrence occ_hold = occs.get(size-1);
		occs.remove(size-1);
		int frequency = occ_hold.frequency;		
		int high = size;
		int low = 0, mid = 0;
		ArrayList<Integer> middle = new ArrayList<Integer>();
		// binary search
		while( high > low )
		{
			mid = (high + low)/2;
			middle.add(mid);
			Occurrence next_occ = occs.get(mid);
			if(frequency == next_occ.frequency)
			{
				break;
			}
			if(frequency > next_occ.frequency)
			{
				high = mid;
			}
			else
			{
				low = mid + 1;
			}
		}
		mid = (high + low)/2;
		occs.add(mid, occ_hold);
		return middle;		
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		ArrayList<String> docs = new ArrayList<String>();
		ArrayList<Occurrence> occ_kw1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> occ_kw2 = keywordsIndex.get(kw2);
		if(occ_kw1 == null && occ_kw2 == null)
		{
			return docs;
		}
		
		ArrayList<Occurrence> frequencies = new ArrayList<Occurrence>();
		
		if(occ_kw1!= null)
		{
			for(int i = 0; i < occ_kw1.size(); i++)
			{
				frequencies.add(occ_kw1.get(i));
			}
		}
		if(occ_kw2!= null)
		{
			for(int i = 0; i < occ_kw2.size(); i++)
			{
				if(frequencies.indexOf(occ_kw2.get(i))== -1)
				{
					frequencies.add(occ_kw2.get(i));
				}
			}
		}
		
		int i;
		Occurrence Keys;
		for(int j = 1; j < frequencies.size(); j++)
		{
			Keys = frequencies.get(j);
			i = j;
			while(i > 0 && frequencies.get(i-1).frequency < Keys.frequency)
			{
				frequencies.set(i, frequencies.get(i-1));
				i--;
			}
			frequencies.set(i, Keys);
		}
		
		for(int k = 0; k<frequencies.size(); k++)
		{
			if(!docs.contains(frequencies.get(k).document))
			{
				docs.add(frequencies.get(k).document);
			}
		}
		return docs;
	}
}