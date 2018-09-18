/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pagerankwithtransition;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cagatay
 * This program calculates pagerank vector of each node in the graph, using pagerank algortihm
 * After that it calculates cosine similarity value of all nodes pairs in the grap
 * you should manually prepare adjaceny matrix as text file and give file name as a parameter
 * please check sample input.txt file, please also know that you must give correct adjaceny matrix as in input.txt, 
 * program doesnt check  erroneous file structure
 */
public class PagerankWithTransition {

    /**
     * @param args the command line arguments
     */
    static String fileName;
    public static void prepareForInput(String[] args){
        BufferedReader input;
        String exp;
        int res;
        if(args.length == 0){
            try {
                input = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Enter Adjaceny Matrix File Name of Your Graph:");
                    fileName = input.readLine();
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

        }
    }
    public static void main(String[] args) throws FileNotFoundException, IOException {
        prepareForInput(args);
        // TODO code application logic here
        FileInputStream fstream = new FileInputStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));  
        String strLine;
        //int[][] komsuluk={{0,1,0,1,0,0,0},{1,0,0,0,0,0,1},{0,0,0,0,1,1,1},{1,0,1,0,0,0,1},{0,0,1,0,0,0,0},{0,0,1,0,0,0,0},{0,1,1,1,0,0,0}};
        Map<Integer,List<String>> sampleMap=new HashMap<Integer,List<String>>();
        int x=0;
        while ((strLine = br.readLine()) != null) {
            List<String> ls=new ArrayList<>();
            for(String s:strLine.split(","))
            {
                ls.add(s);
            }
            sampleMap.put(x, ls);
            x++;
        }
        int[][] komsuluk=new int[x][x];
        x=0;
        for(Map.Entry<Integer, List<String>> entry : sampleMap.entrySet()) {
            int y=0;
            for(String s:entry.getValue())
            {
                komsuluk[x][y]=Integer.valueOf(s);
                y++;
            }
            x++;
        }
        
        float[] outDegree=new float[komsuluk.length];
        for (int i=0;i<komsuluk.length;i++)
        {
            outDegree[i]=satirTopla(komsuluk[i]);
        }
        float[][] M=new float[komsuluk.length][komsuluk.length];
       for (int i=0;i<komsuluk.length;i++)
        {          
            for (int j=0;j<komsuluk.length;j++)
            {
                if(outDegree[i]!=0)
                {
                    M[i][j]=komsuluk[i][j]/outDegree[i];
                }
            }
        }
        float[][] rankAll=new float[komsuluk.length][komsuluk.length];
        for(int i=0;i<rankAll.length;i++)
        {
            for(int j=0;j<rankAll[i].length;j++)
            {
                if(i==j)
                {
                    rankAll[i][j]=(float)1.0;
                }
                else
                {
                   rankAll[i][j]=(float)0.0; 
                }
            }
        }
        System.out.println("*******************************************"); 
        float[][] pageRank=new float[rankAll.length][rankAll.length];
        for (int i=0;i<rankAll.length;i++)
        {
               System.out.print("Pagerank Vector of Node"+i+":"); 
               pageRank[i]=pageRankBul(rankAll[i],transpoze(M),100);
               tekBoyutluMatrisYaz(pageRank[i]);
        }
        System.out.println("*******************************************"); 
        System.out.println("Cosine Similarity Values Between Each Nodes"); 
        for(int i=0;i<pageRank.length;i++)
        {
            for(int j=0;j<pageRank.length;j++)
            {
                System.out.println("Node"+i+",Node"+j+"="+cosSim(pageRank[i], pageRank[j]));
            }
        }
  
    }
    
  private static int satirTopla(int[] komsuluk) {
       int returnVal=0;
       for(int i=0;i<komsuluk.length;i++)
       {
           returnVal=returnVal+komsuluk[i];
       }
       return returnVal;
    }
   private static float[][] transpoze(float[][] matris) {
       float[][] sonuc=new float[matris.length][matris.length];
       for(int i=0;i<matris.length;i++)
       {
           for(int j=0;j<matris.length;j++)
           {
               sonuc[j][i]=matris[i][j];
           }
       }
       return sonuc;
    }
    private static float[] skalerVektorCarp(float s,float[] vektor) {
       float[] sonuc=new float[vektor.length];
       for(int i=0;i<vektor.length;i++)
       {
           sonuc[i]=vektor[i]*s;
       }
       return sonuc;
    }
    private static float[][] skalerMatrisCarp(float s,float[][] matris) {
       float[][] sonuc=new float[matris.length][matris.length];
       for(int i=0;i<matris.length;i++)
       {
            for(int j=0;j<matris.length;j++)
            {
                sonuc[i][j]=matris[i][j]*s;
            }
       }
       return sonuc;
    }
    private static float[] vektorTopla(float[] v1,float[] v2) {
       float[] v=new float[v1.length];
       for(int i=0;i<v.length;i++)
       {
           v[i]=v1[i]+v2[i];
       }
       return v;
    }
    private static float[] matrisVektorCarp(float[][] matris,float[] vektor) {
       float[] sonuc=new float[vektor.length];
       for(int i=0;i<vektor.length;i++)
       {
           
           for (int j=0;j<vektor.length;j++)
           {
               sonuc[i]=sonuc[i]+matris[i][j]*vektor[j];
               
           }
       }
       return sonuc;
    }
    private static float cosSim(float[] x,float[] y) {
       float ust=0;
       float alt1=0;
       float alt2=0;
       for(int i=0;i<x.length;i++)
       {
           ust=ust+x[i]*y[i];
           alt1=alt1+x[i]*x[i];
           alt2=alt2+y[i]*y[i];
       }
       return ust/((float)Math.sqrt(alt1)*(float)Math.sqrt(alt2));
    }

    private static float[][] transitionBul(int[][] komsuluk, float[] outDegree) {
        float[][] transition=new float[komsuluk.length][komsuluk.length];
        for(int i=0;i<komsuluk.length;i++)
        {
            for(int j=0;j<komsuluk.length;j++)
            {
                float a=0;
                if (outDegree[i]>0)
                {
                    a=(float)0.85*(float)komsuluk[i][j]/(float)outDegree[i];
                }
                transition[i][j]=a+(float)0.15/(float)outDegree.length;
            }
        }
        return transition;
    }

    private static void ikiBoyutluMatrisYaz(float[][] transition) {
        for(int i=0;i<transition.length;i++)
        {
            for(int j=0;j<transition.length;j++)
            {
                System.out.print(transition[i][j]+" ");
            }
            System.out.println();
        }
    }

    private static float[] pageRankBul(float[] rank, float[][] M, int iteration) {
    float[] pageRank=rank;
    for (int i=0;i<iteration;i++)
    {

        //pageRank=vektorTopla(skalerVektorCarp((float)0.85,matrisVektorCarp(M, pageRank)),skalerVektorCarp((float)0.15, rank));
         //System.out.println(i+".inci iterasyonda pagerank matrisi");
         //tekBoyutluMatrisYaz(pageRank);
         pageRank=vektorTopla(matrisVektorCarp(skalerMatrisCarp((float)0.85, M), pageRank),skalerVektorCarp((float)0.15, rank));
                 
        
    }
    return pageRank;
    }

    private static float[] pageRankBulOld(float[] rank, float[][] transition, int iteration) {
    float[] pageRank=rank;
    for (int i=0;i<iteration;i++)
    {
        pageRank=vektorMatrisCarp(pageRank,transition);
        //pageRank=matrisVektorCarp(transition, pageRank);
    }
    return pageRank;
    }
        
    private static float[] vektorMatrisCarp(float[] pageRank, float[][] transition) {
    float[] result=new float[pageRank.length];
    for (int i=0;i<pageRank.length;i++)
    {
        for(int j=0;j<pageRank.length;j++)
        {
            result[i]=result[i]+pageRank[j]*transition[j][i];
        }
    }
    return result;
    }

    private static void tekBoyutluMatrisYaz(float[] pageRank) {
    for(int i=0;i<pageRank.length;i++)
    {
        System.out.print(pageRank[i]+" ");
    }
        System.out.println();
    }
    
    public static float[][] matrixMultiply(float[][] a, float[][] b) {
        int m1 = a.length;
        int n1 = a[0].length;
        int m2 = b.length;
        int n2 = b[0].length;
        if (n1 != m2) throw new RuntimeException("Illegal matrix dimensions.");
        float[][] c = new float[m1][n2];
        for (int i = 0; i < m1; i++)
            for (int j = 0; j < n2; j++)
                for (int k = 0; k < n1; k++)
                    c[i][j] += a[i][k] * b[k][j];
        return c;
    }

    private static float[] randomWalkPageRank(float[][] transition, int page, int numOfHit) {
        float[] rwPageRank=new float[transition.length];
        int[] freq=new int[transition.length];
        int N=transition.length;
        for(int t=0;t<numOfHit;t++)
        {
            //one random move
            double r=Math.random();
            double sum=0;
            for(int j=0;j<N;j++)
            {
                //find interval containing r
                sum+=transition[page][j];
                if(r<sum)
                {
                    page=j;
                    break;
                }
            }
            freq[page]++;
        }
        for(int i=0;i<N;i++)
        {
            rwPageRank[i]=(float)freq[i] / numOfHit;
        }
        return rwPageRank;
    }
    
}
