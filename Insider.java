import java.io.*;
import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.util.Arrays;
import java.lang.*;

public class Insider{

	static List<String> sales;
	static List<Integer> trainSale;
	static List<Integer> trainNoSale;
	static List<Integer> testScores;
	static List<String> testIds;
	static List<String> willBuy = new ArrayList<String>();
	static List<String> wontBuy = new ArrayList<String>();

	public Insider() throws IOException{

		try{
			
			File csvFile = new File("Likelihood_to_puchase_sample_data.csv");
			Scanner file = new Scanner(csvFile);
			//file.useDelimiter(",");
			sales		 = new ArrayList<String>();
			trainSale 	 = new ArrayList<Integer>();
			trainNoSale  = new ArrayList<Integer>();
			testScores 	 = new ArrayList<Integer>();
			testIds 	 = new ArrayList<String>();
			
			int i = 0;
			while(file.hasNextLine()){
				String string = file.nextLine();
				String[] fields = string.split(","); 
				if(i != 0 && i<= 200000){
					Integer score = 0;

					for(int j = 1; j < 6; j++){
						score += Integer.parseInt(fields[j]);
					}

					if(Integer.parseInt(fields[6]) == 1) trainSale.add(score);
					else trainNoSale.add(score);
				}

				if(i > 200000){
					Integer score = 0;
					testIds.add(fields[0]);
					for(int j = 1; j < 6; j++){
						score += Integer.parseInt(fields[j]);
					}
					if(Integer.parseInt(fields[5]) == 0)score-=100;
					testScores.add(score);
				}
				i++;
			}
			/*System.out.println("SATIS YAPILDI");
			System.out.println(trainSale);
			System.out.println("SATIS YAPLIMADI");
			System.out.println(trainNoSale);*/
			file.close();
		}catch (FileNotFoundException e){
			System.out.println("Something went wrong!");
		}
	}

	public static void main(String args[]){

		try{
			Insider in = new Insider();
			Integer scoreMeanSale = in.scoreMean(trainSale);
			Integer scoreMeanNoSale = in.scoreMean(trainNoSale);
			double devSale = in.scoreDeviation(trainSale, scoreMeanSale);
			double devNoSale = in.scoreDeviation(trainNoSale, scoreMeanNoSale);
			//System.out.println(scoreMeanSale);
			//System.out.println(Math.floor(devSale/2));
			//System.out.println(scoreMeanNoSale);
			//System.out.println(Math.floor(devNoSale/2));

			in.classify(testScores, scoreMeanSale, devSale, scoreMeanNoSale, devNoSale);
			FileWriter writer1 = new FileWriter("WillBuy.txt");
			FileWriter writer2 = new FileWriter("WontBuy.txt");
			//System.out.println("IDs That Will Buy: ");
			for(String str : willBuy){
				writer1.write(str + "\n");
			}
			writer1.close();
			//System.out.println("IDs That Will NOT Buy");
			for(String str : wontBuy){
				writer2.write(str + "\n");
			}
			writer2.close();

			System.out.println("Done");

		}
		catch (IOException e){
			System.out.println("Problem with file input.");
		}
	}

	public Integer scoreMean(List<Integer> data){
		Integer total = 0;
		for(int i = 0; i < data.size() ; i++){
			total+= data.get(i);
		}

		return total/data.size();
	}

	public double scoreDeviation(List<Integer> data, Integer mean){

		int n = data.size();
		double sum = 0;
		for(int i = 0; i < n; i++){
			sum += (data.get(i) - mean)*(data.get(i) - mean);
		}
		double variance = sum/n;
		return Math.sqrt(variance);

	}

	public void classify(List<Integer> data, Integer saleScore, double devSale, Integer noSaleScore, double devNoSale){
		double nearestNeghborMid =  ((saleScore - devSale/2) + (noSaleScore + devNoSale/2))/2;
		/*System.out.println("sale score " + saleScore);
		System.out.println("sale dev " + devSale);
		System.out.println("No Sale Score " + noSaleScore);
		System.out.println("No Sale Dev " + devNoSale);
		System.out.println(nearestNeghborMid);*/
		for(int i = 0; i < data.size(); i++){
			//System.out.println(testIds.get(i) +" "+ data.get(i));
			if(data.get(i) < nearestNeghborMid) wontBuy.add(testIds.get(i));
			else if(data.get(i) >= nearestNeghborMid) willBuy.add(testIds.get(i));
		}
	}
}