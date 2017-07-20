package search;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Scanner;

public class LittleSearchEngineDriver {

	// Driver
		public static void main(String[] args) throws FileNotFoundException {
		//	Scanner scan = new Scanner(System.in);
			String fileName = "docs.txt";
			String noiseFile = "noisewords.txt";
			
			LittleSearchEngine lse = new LittleSearchEngine();
			try 
			{
				lse.makeIndex(fileName,noiseFile);
			} 
			catch (FileNotFoundException e)
			{
				System.out.println("File Not Found");
			}
			
			//	top5search
			
			System.out.print("\n --> top5search method : ");
			System.out.println("\n");
	/*	 	System.out.print("keyword 1: ");
			String keyword1 =  scan.next();
			System.out.print("keyword 2: ");
			String keyword2 = scan.next();
			scan.close();
	*/	
	//		ArrayList<String> results = lse.top5search(keyword1, keyword2);
			
			ArrayList<String> res = lse.top5search("deep", "world");
			System.out.println(res);			
			
			//	getKeyWord	
			
			System.out.print("\n --> getKeyWord method : ");
			System.out.println("\n");
			System.out.println(lse.getKeyWord("distance."));
			System.out.println(lse.getKeyWord("equi-distant"));
			System.out.println(lse.getKeyWord("Rabbit"));
			System.out.println(lse.getKeyWord("Between"));
			System.out.println(lse.getKeyWord("we're"));
			System.out.println(lse.getKeyWord("World..."));
			System.out.println(lse.getKeyWord("World?!"));
			System.out.println(lse.getKeyWord("What,ever"));
			
			// loadKeyWords
			
			System.out.print("\n --> loadKeyWords method : ");
			System.out.println("\n");
		 	HashMap<String, Occurrence> res1 = lse.loadKeyWords("AliceCh1.txt");
		 	System.out.println(res1);			
				
		}
}
