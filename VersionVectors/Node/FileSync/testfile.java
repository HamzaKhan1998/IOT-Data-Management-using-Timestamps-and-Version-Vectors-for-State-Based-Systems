import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import Globals.*;

public class testfile {
    public static boolean parseFile(String fileName,String searchStr) throws FileNotFoundException{
        Scanner scan = new Scanner(new File(fileName));
        while(scan.hasNext()){
            String line = scan.nextLine().toLowerCase().toString();
            if(line.contains(searchStr)){
                return true;
            }
        }
        return false;
    }
	public static void main(String[] args) throws IOException{
        //String filename_timestamp="132897.meta-2218972178";
        //StringTokenizer multiTokenizer = new StringTokenizer(filename_timestamp, "-");
        //System.out.println(multiTokenizer.nextToken());
        //System.out.println(multiTokenizer.nextToken());
        boolean x= parseFile("./myfiles/10951047.meta","2,0");
        if (x==true){
            System.out.println("Helllooo jee");
        }
        // String a="5";
        // String b="6";
        // if (Integer.valueOf(a)<Integer.valueOf(b)){
        //     System.out.println("True");
        // }
        // else{
        //     System.out.println("false");
        // }
        
        // String line="A,1";
        // String clientID="A";
        // System.out.println(Character.toString(line.charAt(0)));
        // System.out.println(clientID);
        // String x=String.valueOf(line.charAt(0));
        // if (line.charAt(0) == clientID.charAt(0)){
        //     System.out.println(Character.toString(line.charAt(0)));
        // }
        //System.out.print(GlobalConstants.geek);
        //GlobalConstants.x+=1;
        //System.out.print(GlobalConstants.x);
        
        // String name="B";
        // String filename="File";
        // String path="./myfiles/";
        // File tmp = new File(path+name);
        // boolean bool = tmp.mkdir();
        // File tmp_file=new File(path+name+"/"+filename);
        // tmp_file.createNewFile();
        // if(bool){
        //     System.out.println("Directory created successfully");
        // }else{
        //     System.out.println("Sorry couldn’t create specified directory");
        // }

        //  //Initializing a Dictionary 
        //  Dictionary geek = new Hashtable(); 
  
        //  // put() method 
        //  geek.put("123", "Code"); 
        //  geek.put("456", "Program"); 
        //  geek.put("456", "Program1.1");

        // System.out.print(geek);

        //  Enumeration<String> e = geek.keys();
        //  while(e.hasMoreElements()) {
        //      String k = e.nextElement();
        //      System.out.println(k + ": " + geek.get(k));
        //  }
         // elements() method : 
        //  for (Enumeration i = geek.elements(); i.hasMoreElements();) 
        //  { 
        //      System.out.println("Value in Dictionary : " + i.nextElement()); 
        //  } 
   
        //  // get() method : 
        //  System.out.println("\nValue at key = 6 : " + geek.get("6")); 
        //  System.out.println("Value at key = 456 : " + geek.get("123")); 
   
        //  // isEmpty() method : 
        //  System.out.println("\nThere is no key-value pair : " + geek.isEmpty() + "\n"); 
   
        //  // keys() method : 
        //  for (Enumeration k = geek.keys(); k.hasMoreElements();) 
        //  { 
        //      System.out.println("Keys in Dictionary : " + k.nextElement()); 
        //  } 
   
        //  // remove() method : 
        //  System.out.println("\nRemove : " + geek.remove("123")); 
        //  System.out.println("Check the value of removed key : " + geek.get("123")); 
   
        //  System.out.println("\nSize of Dictionary : " + geek.size()); 
    }
}