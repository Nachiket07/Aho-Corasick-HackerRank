import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Trie
{
    HashMap<String,Trie> map;
    boolean value;
    Trie failure;
}
class Node
{
    Trie current;
    Trie prefix;
    Node(Trie a,Trie b)
    {
        current=a;
        prefix=b;
    }
}
public class Solution 
{
    static String current="";
    static int count=1;
    static HashMap<Trie,HashMap<String,Integer>> outputmap;
    static void construct(Trie head,String gene)
    {
        if(gene.compareTo("")==0)
        {
            head.value=true;
            outputmap.put(head,new HashMap<String,Integer>());
            outputmap.get(head).put(current,0);
        }
        else
        {
            Trie temp;
            if(!head.map.containsKey(gene.substring(0,1)))
            {
                temp=new Trie();
                temp.map=new HashMap<String,Trie>();
                temp.value=false;
                head.map.put(gene.substring(0,1),temp);
                count+=1;           
            }
            construct(head.map.get(gene.substring(0,1)),gene.substring(1));
        }
    }
    
    public static void main(String[] args) throws IOException 
    {
        InputStreamReader r=new InputStreamReader(System.in);
        BufferedReader br=new BufferedReader(r);
        int n=Integer.parseInt(br.readLine());
        String[] genes = new String[n];

        String[] genesItems = br.readLine().split(" ");
        for (int i = 0; i < n; i++) {
            String genesItem = genesItems[i];
            genes[i] = genesItem;
        }

        int[] health = new int[n];

        String[] healthItems = br.readLine().split(" ");

        //Aho-Corasick
        // construction of Trie
        outputmap=new HashMap<Trie,HashMap<String,Integer>>();
        Trie head=new Trie();
        head.value=false;
        head.map=new HashMap<String,Trie>();
        head.failure=head;
        for (int i = 0; i < n; i++) 
        {
            int healthItem = Integer.parseInt(healthItems[i]);
            health[i] = healthItem;
            current=genes[i];
            construct(head,genes[i]);
        }
        // Managing failure links using BFS.
        ArrayList<Node> queue=new ArrayList<Node>();
        for(Map.Entry<String,Trie> m:head.map.entrySet())
        {
            queue.add(new Node(m.getValue(),head));
        }
        Node temp;
        Trie temp_prefix;
        while(queue.size()!=0)
        {
            temp=queue.get(0);
            temp.current.failure=temp.prefix;
            queue.remove(0);
            for(Map.Entry<String,Trie> m:temp.current.map.entrySet())
            {
                temp_prefix=temp.prefix;
                while(temp_prefix!=head && !temp_prefix.map.containsKey(m.getKey()))
                {
                    temp_prefix=temp_prefix.failure;
                }
                if(temp_prefix.map.containsKey(m.getKey()))
                {
                    temp_prefix=temp_prefix.map.get(m.getKey());
                }
                if(temp_prefix.value)// output mapping
                {
                    m.getValue().value=true;
                    if(!outputmap.containsKey(m.getValue()))
                    {
                        outputmap.put(m.getValue(),new HashMap<String,Integer>());
                    }
                    outputmap.get(m.getValue()).putAll(outputmap.get(temp_prefix));
                }
                queue.add(new Node(m.getValue(),temp_prefix));
            }
        }
        BigInteger min=new BigInteger(Integer.toString(Integer.MAX_VALUE));
        BigInteger max=new BigInteger("-1");
        BigInteger tempgeanhealth;
        int s=Integer.parseInt(br.readLine());
        HashMap<String,BigInteger> pairs=new HashMap<String,BigInteger>();
        for (int sItr = 0; sItr < s; sItr++) // for each DNA calculating health 
        {
            tempgeanhealth=new BigInteger("0");
            String[] firstLastd = br.readLine().split(" ");
            //tempgeanhealth=0;
            int first = Integer.parseInt(firstLastd[0]);

            int last = Integer.parseInt(firstLastd[1]);

            String d = firstLastd[2];
            temp_prefix=head;
            for(int i=first;i<=last;i++)
            {
                if(pairs.containsKey(genes[i]))
                {
                    pairs.replace(genes[i],pairs.get(genes[i]).add(new BigInteger(Integer.toString(health[i]))));
                }
                else
                {
                    pairs.put(genes[i],new BigInteger(Integer.toString(health[i])));
                }
            }
            for(int i=0;i<d.length();i++)
            {
                while(temp_prefix!=head && !temp_prefix.map.containsKey(d.substring(i,i+1)))
                {
                    temp_prefix=temp_prefix.failure;
                }
                if(temp_prefix.map.containsKey(d.substring(i,i+1)))
                {
                    temp_prefix=temp_prefix.map.get(d.substring(i,i+1));
                }
                if(temp_prefix.value==true)
                {
                    for(Map.Entry<String,Integer> m:outputmap.get(temp_prefix).entrySet())
                    {
                        if(pairs.containsKey(m.getKey()))
                        {
                            tempgeanhealth=tempgeanhealth.add(pairs.get(m.getKey()));
                        }
                    }
                }
            }
            pairs.clear();
            if(tempgeanhealth.compareTo(max)==1)
            {
                max=tempgeanhealth;
            }
            if(tempgeanhealth.compareTo(min)==-1)
            {
                min=tempgeanhealth;
            }
        }
        System.out.println(min.toString()+" "+max.toString());
    }
}
