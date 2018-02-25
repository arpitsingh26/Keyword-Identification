import java.util.*;
import java.io.*;

public class ki{
	public static void main(String[] args) throws IOException{ 
		
		Scanner sc = new Scanner(new File("input_file.txt"));
		sc.useDelimiter("\n");
		while (sc.hasNext()) {
		    String input = sc.next();
		    String input1=input;
		    if (input.trim().isEmpty()) {
		        continue;
		    }

			//taking input
	     	//BufferedReader br = new BufferedReader (new InputStreamReader(System.in));
	     	//String input=br.readLine();

	     	//cleaning input
	     	input=input.replaceAll("'s", "");
	     	input=input.replaceAll("'", "");
	     	input=input.replaceAll("[^a-zA-Z0-9 ]", " ").toLowerCase();
	     	String[] inpwords = input.split("\\s+");
	     	Map<String, String> strpos = new HashMap<String, String>(); 

	     	try{
	     		String[] args1 = new String[] {"python","findpos.py",input};
				Process p1 = new ProcessBuilder(args1).start();

				BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
				String line1 = "";
				int index1=0;

				while((line1 = reader1.readLine()) != null) {
					strpos.put(inpwords[index1],line1);
					index1++;
				}
	     	}
	     	catch(Exception e){}
	     	
	     	//removing stopwords
	     	File stopwordsfile = new File("stopwords.txt");
	     	Scanner scanner=new Scanner(stopwordsfile);
	     	List<String> stopwords=new ArrayList<>();
			while(scanner.hasNextLine()){
			    stopwords.add(scanner.nextLine()); 
			}

			List<String> inpwords2=new ArrayList<>();

			for (int i=0;i<inpwords.length;i++){
				if (!stopwords.contains(inpwords[i])){
					inpwords2.add(inpwords[i]);
				}
			}

			System.out.println(input1);

			//adding corpus data
			String[] corpus=new String[2];
			corpus[0]="../word2vec/trunk/vectors-phrase-expertlarge.bin";
			corpus[1]="../word2vec/trunk/vectors-phrase.bin";

			long[] totalwords=new long[2];
			totalwords[0]=582592;
			totalwords[1]=169900;

			if (inpwords2.size()==0){
				System.out.println();
				continue;
			}

			if (inpwords2.size()==1){
				System.out.println(inpwords2.get(0)+" ( "+strpos.get(inpwords2.get(0))+" )");
				System.out.println();
				continue;
			}

			if (inpwords2.size()==2){

				float sem_relation=-1.0f;
				float cooccur_relation=-1.0f;
				float cal_relation=-1.0f;
				float k1=0.95f;
				for (int i1=0;i1<corpus.length && (sem_relation==Float.parseFloat("-1"));i1++){
					try{
						String[] args2 = new String[] {"./distance-pairs",corpus[i1],inpwords2.get(0),inpwords2.get(1)};
						Process pt = new ProcessBuilder(args2).start();

				        BufferedReader reader = new BufferedReader(new InputStreamReader(pt.getInputStream()));
				        String line = "";
				        int index2=0;

				        while((line = reader.readLine()) != null) {
				        	if (index2==0) {  
				        		long l1=Long.parseLong(line);
				        		float f1=l1;
				        		if (f1!=-1.0f)
							    	cooccur_relation=f1/totalwords[i1];
							}
							else if (index2==1){
									sem_relation=Float.parseFloat(line);
							}

							index2++;
						}
						pt.waitFor();
						pt.destroy();
					}
					catch(Exception e){}
				}

				if (((inpwords2.get(0)=="JJ") || (inpwords2.get(0)=="JJR") || (inpwords2.get(0)=="JJS") || 
					(inpwords2.get(0)=="NN") || (inpwords2.get(0)=="NNS") || (inpwords2.get(0)=="NNP") || (inpwords2.get(0)=="NNPS")) &&
					((inpwords2.get(1)=="NN") || (inpwords2.get(1)=="NNS") || (inpwords2.get(1)=="NNP") || (inpwords2.get(1)=="NNPS"))){
					if (cooccur_relation!=-1.0f)
						cal_relation=cooccur_relation*k1;
				}
				else
					cal_relation=cooccur_relation;

				if (cal_relation<0.2 && cal_relation!=-1.0f){
					System.out.println(inpwords2.get(0)+" "+inpwords2.get(1)+" ( "+strpos.get(inpwords2.get(0))+" "+strpos.get(inpwords2.get(1))+" )");
				}
				else{
					System.out.println(inpwords2.get(0)+" ( "+strpos.get(inpwords2.get(0))+" )");
					System.out.println(inpwords2.get(1)+" ( "+strpos.get(inpwords2.get(1))+" )");
				}

				System.out.println();
				continue;
			}

			//getting phrases and words from the words (after removing stopwords)
			int maxphraselength=4;
			int numphrases2=(inpwords2.size()*(inpwords2.size()+1))/2;
			String[] temp_phrases=new String[numphrases2];
			String[] temp_phrases2=new String[numphrases2];
			float[] temp_phraselengthratio=new float[numphrases2];
			for (int i=0;i<numphrases2;i++){
				temp_phrases[i]="";
				temp_phrases2[i]="";
			}

			int index=0;
			for (int i=inpwords2.size();i>0;i--){
				for (int j=0;j<=inpwords2.size()-i;j++){
					int temp_phraselength=0;
					for (int k=j;k<j+i;k++){
						temp_phrases[index]=temp_phrases[index]+" "+inpwords2.get(k);
						temp_phrases2[index]=temp_phrases2[index]+"_"+inpwords2.get(k);
						temp_phraselength++;
					}
					temp_phraselengthratio[index]=((float)temp_phraselength)/inpwords2.size();
					if ((!(temp_phrases[index]==null || temp_phrases[index].length()==0)) && temp_phrases[index].substring(0,1).equals(" "))
						temp_phrases[index]=temp_phrases[index].substring(1);
					if (!(temp_phrases2[index]== null || temp_phrases2[index].length() == 0) && temp_phrases2[index].substring(0,1).equals("_"))
						temp_phrases2[index]=temp_phrases2[index].substring(1);

					//System.out.println(temp_phrases2[index]);
					index++;
				}
			}

			int n1=inpwords2.size()-maxphraselength;
			int n2=(inpwords2.size()*(inpwords2.size()+1))/2;
			int numphrases;
			if (n1>0)
				numphrases=n2-(n1*(n1+1))/2;
			else
				numphrases=n2;

			String[] phrases=new String[numphrases];
			String[] phrases2=new String[numphrases];
			float[] phraselengthratio=new float[numphrases];
			for (int i=0;i<numphrases;i++){
				phrases[i]=temp_phrases[numphrases2-1-i];
				phrases2[i]=temp_phrases2[numphrases2-1-i];
				phraselengthratio[i]=temp_phraselengthratio[numphrases2-1-i];
			}
			
			//create graph from the phrases
			float[][] sem_graph=new float[numphrases][numphrases];
			float[][] cooccur_graph=new float[numphrases][numphrases];
			for (int i=0;i<numphrases;i++){
				for (int j=0;j<numphrases;j++){
					sem_graph[i][j]=Float.parseFloat("-1");
					cooccur_graph[i][j]=Float.parseFloat("-1");
					if (i==j){
						sem_graph[i][j]=Float.parseFloat("0");
					}
					else{
						String[] arr1=phrases2[i].split("_");
						String[] arr2=phrases2[j].split("_");
						int common_found=0;
						for(int i1=0;i1<arr1.length;i1++){
				            for(int j1=0;j1<arr2.length;j1++){
				                if(arr1[i1].equals(arr2[j1])){
				                    common_found=1;
				                    break;
				                }
				            }
		        		}
						if (common_found==1){
							sem_graph[i][j]=Float.parseFloat("0");
						}
					}	
				}
			}

			//store in graph edges the semantic and cooccurence relation
			for (int i=0;i<numphrases;i++){
				for (int j=0;j<numphrases;j++){
					if (i<j){
						for (int i1=0;i1<corpus.length && sem_graph[i][j]==Float.parseFloat("-1");i1++){
							try{
								String[] args2 = new String[] {"./distance-pairs",corpus[i1],phrases2[i],phrases2[j]};
								Process pt = new ProcessBuilder(args2).start();

						        BufferedReader reader = new BufferedReader(new InputStreamReader(pt.getInputStream()));
						        String line = "";
						        int index2=0;

						        while((line = reader.readLine()) != null) {
						        	if (index2==0) {  
						        		long l1=Long.parseLong(line);
						        		float f1=l1;
						        		if (f1==-1.0f){
						        			cooccur_graph[i][j]=1.0f;
						        			cooccur_graph[j][i]=1.0f;
						        		}
						        		else{
						        			if (f1==totalwords[i1]){
						        				cooccur_graph[i][j]=1.0f;
						        				cooccur_graph[j][i]=1.0f;
						        			} 
						        			else{
									    		cooccur_graph[i][j]=1/(1-(f1/totalwords[i1]));
									    		cooccur_graph[j][i]=1/(1-(f1/totalwords[i1]));
						        			}
										}
									}
									else if (index2==1){
										if (sem_graph[i][j]!=Float.parseFloat("0")){
											sem_graph[i][j]=Float.parseFloat(line);
											sem_graph[j][i]=Float.parseFloat(line);
										}
									}

									index2++;
								}
								pt.waitFor();
								pt.destroy();
							}
							catch(Exception e){}
						}
					}
				}
			}

			float sigma=0.002f;		
			float[][] cal_graph=new float[numphrases][numphrases];
			int[][] edge_visited=new int[numphrases][numphrases];
			int[] node_visited=new int[numphrases];
			for (int i=0;i<numphrases;i++){
				node_visited[i]=0;
				for (int j=0;j<numphrases;j++){
					if (cooccur_graph[i][j]==-1.0f) cooccur_graph[i][j]=1.0f;
					cal_graph[i][j]=sem_graph[i][j]*cooccur_graph[i][j];
					if (cal_graph[i][j]<sigma && cal_graph[i][j]!=0.0f) cal_graph[i][j]=sigma;
					if (i!=j) edge_visited[i][j]=0;
					else edge_visited[i][j]=1;
					//System.out.println(phrases2[i]+" "+i+"  "+ phrases2[j]+" "+j+ " "+ cooccur_graph[i][j]);
				}
			}

			//finding the best segmentation of the text
			List<Integer> final_nodes=new ArrayList<>();
			float avg_edge_weight=0.0f;
			while (true){

				//check if all edges have been visited
				int flag=0;
				for (int i=0;i<numphrases;i++){
					for (int j=0;j<numphrases;j++){
						if (edge_visited[i][j]==0){
							flag=1;
							break;
						}
					}
					if (flag==1) break;
				}
				if (flag==0) break;

				//choosing the nodes with maximum edge weight
				int node1=-1, node2=-1;
				float max_edgeweight=-1.0f;
				for (int i=0;i<numphrases;i++){
					for (int j=0;j<numphrases;j++){
						if (i<j && edge_visited[i][j]==0){
							if (cal_graph[i][j]>max_edgeweight) {
								max_edgeweight=cal_graph[i][j];
								node1=i;
								node2=j;
							}
						}
					}
				}

				if (node1!=-1 && node2!=-1){
					edge_visited[node1][node2]=1;
					edge_visited[node2][node1]=1;
				}
				if (!final_nodes.contains(node1))
					final_nodes.add(node1);
				if (!final_nodes.contains(node2))
				final_nodes.add(node2);

				//marking all the edges corresponding to the disconnected edges as disconnected
				if (node1!=-1){
					for (int i=0;i<numphrases;i++){
						if (i==node1) continue;
						if (cal_graph[node1][i]==0.0f){
							edge_visited[node1][i]=1;
							edge_visited[i][node1]=1;
							//System.out.println("qwe: "+ i);
							for (int j=0;j<numphrases;j++){
								edge_visited[i][j]=1;
								edge_visited[j][i]=1;
							}
						}	
					}
				}

				if (node2!=-1){
					for (int i=0;i<numphrases;i++){
						if (i==node2) continue;
						if (cal_graph[node2][i]==0.0f){
							edge_visited[node2][i]=1;
							edge_visited[i][node2]=1;
							for (int j=0;j<numphrases;j++){
								edge_visited[i][j]=1;
								edge_visited[j][i]=1;
							}
						}	
					}
				}

			}

			List<Integer> indexofphrases=new ArrayList<>();
			for (int i=0;i<final_nodes.size();i++){
				indexofphrases.add(inpwords2.indexOf((phrases[final_nodes.get(i)]).split(" ")[0]));
			}
			Map<Integer, String> m1 = new HashMap<Integer, String>(); 
			for (int i=0;i<final_nodes.size();i++){
				m1.put(indexofphrases.get(i),phrases[final_nodes.get(i)]);
			}
			Collections.sort(indexofphrases);

			//printing the segmented chunks of text
			for (int i=0;i<final_nodes.size();i++){
				String final_phrase=m1.get(indexofphrases.get(i));
				String[] final_phrase_arr = final_phrase.split("\\s+");
				System.out.print(final_phrase+" ( ");
				for (int j=0;j<final_phrase_arr.length;j++){
					System.out.print(strpos.get(final_phrase_arr[j])+" ");
				}
				System.out.println(")");
			}
			System.out.println();

		}
    }
}

