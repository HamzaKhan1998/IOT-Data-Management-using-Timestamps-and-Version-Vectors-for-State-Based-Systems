import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Random;
import java.util.StringTokenizer;

import Globals.*;

public class FileShuffler {
	// Change every 10th file
	public final static float ChangeProbability = 25f;
	
	private static void usage() {
		System.out.println("Usage:");
		System.out.println("FileShuffler ROOT_DIRECTORY");
	}

	private static void changeFile(File file) throws IOException {
		Random rand = new Random();
		
		file.delete();
		OutputStream os = new FileOutputStream(file);
		for (int i = 0; i < GlobalConstants.FileSize; i++) {
			os.write(rand.nextInt(256) + Byte.MIN_VALUE);
		}
		os.close();
	}
	
	private static void changeMetaData(File file,String servid) throws IOException {
		File metaDataFile = new File(file.getCanonicalPath() + GlobalConstants.MetaDataFileSuffix);
		BufferedReader reader = new BufferedReader(new FileReader(metaDataFile));
		String oldContent = "";
		String line = reader.readLine();

		int x=0;
		while (line != null)
		{
			if (x>0){
				oldContent = oldContent + line + System.lineSeparator();	
			}
			else{
				StringTokenizer multiTokenizer = new StringTokenizer(line, "_");
				String allline="";
				while (multiTokenizer.hasMoreTokens()){
					String cop=multiTokenizer.nextToken();
					String z="";
					System.out.println("yoooooo " + cop);
					System.out.println(cop.charAt(0));
					System.out.println(cop.charAt(2));
					System.out.println(servid.charAt(0));
					if (cop.charAt(0)==servid.charAt(0)){
						int count=Character.getNumericValue(cop.charAt(2));
						count+=1;
						z+=cop.charAt(0);
						z+=":";
						z+=String.valueOf(count);
						System.out.println(z);
						cop=z;
					}
					cop=cop+"_";
					allline+=cop;
					System.out.println(allline);
				}
				System.out.println(oldContent);
				System.out.println(allline);
				oldContent+=allline;
				oldContent =oldContent+"\n";
				System.out.println(oldContent);	
			}
			line = reader.readLine();
			x+=1;
		}
		String newContent = oldContent.replaceAll("0", "1");
		FileWriter writer = new FileWriter(metaDataFile);
		writer.write(newContent);
		reader.close();
		writer.close();

	}
	
	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
			System.exit(-1);
		}
		String rootDirStr = args[0];
		File rootDir = new File(rootDirStr);
		if (!rootDir.isDirectory()) {
			System.err.println("Root directory '" + rootDir + "' is no directorty.");
			System.exit(-1);
		}
		if (!rootDir.exists()) {
			System.err.println("Root directory '" + rootDir + "' does not exist.");
			System.exit(-1);
		}
		String serverid = args[1];
		Random rand = new Random();
		File[] files = rootDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getPath().endsWith(GlobalConstants.MetaDataFileSuffix)) {
				continue;
			}
			
			double r = rand.nextDouble();
			if (r <= ChangeProbability) {
				try {
					changeFile(file);
					changeMetaData(file,serverid);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
