import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

// TODO: import GlobalConstants

public class SyncClient {

	private File rootDir;
	private String serverHostName;
	private int fileServerPort;
	private int metaDataServerPort;
	private String clientId;
	String nof;
	public final static int BufferSize = 1024;

	public SyncClient(String clientId, File rootDir, String serverHostName, int fileServerPort, int metaDataServerPort) {
		this.rootDir = rootDir;
		this.serverHostName = serverHostName;
		this.fileServerPort = fileServerPort;
		this.metaDataServerPort = metaDataServerPort;
		this.clientId=clientId;
		nof=new String();
	}

	private void updatefunc(File metaDataFile,String s1,String s2) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(metaDataFile));
		String oldContent = "";
		String line = reader.readLine();
		while (line != null)
		{
				oldContent = oldContent + line + System.lineSeparator();
				line = reader.readLine();
		}
		String newContent = oldContent.replaceAll(s1, s2);
		FileWriter writer = new FileWriter(metaDataFile);
		writer.write(newContent);
		reader.close();
		writer.close();
	}

	public String removeSuffix(final String s, final String suffix)
	{
		if (s != null && suffix != null && s.endsWith(suffix)){
			return s.substring(0, s.length() - suffix.length());
		}
		return s;
	}

	private String parralelcheck(String servervv,String clientvv){

		StringTokenizer multiTokenizer1 = new StringTokenizer(servervv, "_");
		StringTokenizer multiTokenizer2 = new StringTokenizer(clientvv, "_");
		String versionvector1="";
		String versionvector2="";
		int v1=0,v2=0;
		while (multiTokenizer1.hasMoreTokens() && multiTokenizer2.hasMoreTokens()){
			versionvector1=multiTokenizer1.nextToken();
			versionvector2=multiTokenizer2.nextToken();
			System.out.println("V1: "+versionvector1+"\nV2: "+versionvector2);
			if (Character.getNumericValue(versionvector1.charAt(2))>Character.getNumericValue(versionvector2.charAt(2))){
				v1+=1;
			}
			else if (Character.getNumericValue(versionvector1.charAt(2))<Character.getNumericValue(versionvector2.charAt(2))){
				v2+=1;
			}
		}
		if (v1>0 && v2==0 ){
			return servervv;
		}
		else if (v1==0 && v2>0){
			return clientvv;
		}
		else{
			return "conflict";
		}
	}

	private void getMetaData() throws UnknownHostException, IOException {
		// Retrieve meta data from server.
		System.out.println("<===Func SyncMeta===>");

		Socket socket = new Socket(serverHostName, metaDataServerPort);
		PrintWriter printer = new PrintWriter(socket.getOutputStream(),true);
		System.out.println(clientId);
		printer.println(clientId);
	
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.nof=reader.readLine();
		StringTokenizer defaultTokenizer = new StringTokenizer(this.nof);
	
		if (!this.nof.equals("nil")){
			String Server_name = new String();
			if (defaultTokenizer.hasMoreTokens()){
				Server_name=defaultTokenizer.nextToken();
			}
			while (defaultTokenizer.hasMoreTokens())
			{
				String filename_timestamp=defaultTokenizer.nextToken();
				System.out.println("filename&timestamp => "+filename_timestamp);

				StringTokenizer multiTokenizer = new StringTokenizer(filename_timestamp, "-");
				String filename=multiTokenizer.nextToken();
				File file = new File("./myfiles/"+filename);
				String timestamp_file=new String();

				if (file.exists()==true){
					Scanner myReader = new Scanner(file);
					while (myReader.hasNextLine()) {
						timestamp_file = myReader.nextLine();
						break;
					}
					myReader.close();
					String serverfile_timestamp = multiTokenizer.nextToken();


					System.out.println("filename: "+filename+"\nfiletime: "+serverfile_timestamp);

					// breaking the version vector mzeed

					
					String pc=parralelcheck(serverfile_timestamp, timestamp_file);
					if (pc.equals("conflict")){
						if (Integer.valueOf(clientId)>Integer.valueOf(Server_name)){
							System.out.println("my client id is large");
							updatefunc(file, timestamp_file, serverfile_timestamp);
							updatefunc(file,Server_name+",1",Server_name+",0");
							// file.delete();
							// File filex = new File("./myfiles/"+filename);
							// filex.createNewFile();
							// FileWriter myWriter = new FileWriter(filex.getCanonicalPath());
							// myWriter.write(String.valueOf((serverfile_timestamp))+"\n");
							// myWriter.close();
						}
					}
					else{
						System.out.println("no conflict");
						updatefunc(file, timestamp_file, pc);
						updatefunc(file,Server_name+",1",Server_name+",0");
					}


					//////////////

					// if (Long.parseLong(timestamp_file)<Long.parseLong(serverfile_timestamp)){
					// 	System.out.println("my file is old");
					// 	updatefunc(file, timestamp_file, serverfile_timestamp);
					// 	updatefunc(file,Server_name+",1",Server_name+",0");
					// 	//file.delete();
					// 	// File filex = new File("./myfiles/"+filename);
					// 	// filex.createNewFile();
					// 	// FileWriter myWriter = new FileWriter(filex.getCanonicalPath());
					// 	// myWriter.write(String.valueOf(serverfile_timestamp)+"\n");
					// 	// myWriter.close();
					// }
					// else if (Long.parseLong(timestamp_file)>Long.parseLong(serverfile_timestamp)){
					// 	// do nothing
					// 	System.out.println("my file is latest");
					// 	continue;
					// }
					// else if (Long.parseLong(timestamp_file) == Long.parseLong(serverfile_timestamp)){
					// 	if (Integer.valueOf(clientId)>Integer.valueOf(Server_name)){
					// 		System.out.println("my client id is large");
					// 		updatefunc(file, timestamp_file, serverfile_timestamp);
					// 		updatefunc(file,Server_name+",1",Server_name+",0");
					// 		// file.delete();
					// 		// File filex = new File("./myfiles/"+filename);
					// 		// filex.createNewFile();
					// 		// FileWriter myWriter = new FileWriter(filex.getCanonicalPath());
					// 		// myWriter.write(String.valueOf((serverfile_timestamp))+"\n");
					// 		// myWriter.close();
					// 	}
					// 	else{
					// 		System.out.println("my client id is small");
					// 	}
					// }
				}
				else {
					System.out.println("i dont have file");
					String serverfile_timestamp = multiTokenizer.nextToken();
					file.createNewFile();
					FileWriter myWriter = new FileWriter(file.getCanonicalPath());
					myWriter.write(String.valueOf((serverfile_timestamp))+"\n");
					myWriter.write(Server_name+",0\n");
					myWriter.close();
				}
			}
		}else{
			System.out.println("I am already up to date!");
		}
	}
	
	private void syncFiles() throws UnknownHostException, IOException {
		// TODO: Retrieve files to be updated from server (i.e., replace local replica) and change local meta data.
		System.out.println("<===Func SyncFiles===>");

		if (!this.nof.equals("nil")){

			StringTokenizer defaultTokenizer = new StringTokenizer(this.nof);
			String Server_name = new String();

			if (defaultTokenizer.hasMoreTokens()){
				Server_name=defaultTokenizer.nextToken();
			}

			while (defaultTokenizer.hasMoreTokens())
			{
				byte[] buffer = new byte[BufferSize];
				Socket socket = new Socket(serverHostName, fileServerPort);
				PrintWriter printer = new PrintWriter(socket.getOutputStream(),true);
				String filename_timestamp=defaultTokenizer.nextToken();

				System.out.println("filename&timestamp => "+filename_timestamp);

				StringTokenizer multiTokenizer = new StringTokenizer(filename_timestamp, "-");
				String filename=multiTokenizer.nextToken();
				filename=removeSuffix(filename, ".meta");
				printer.println(clientId+"\n"+filename+"\n");
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				File file = new File("./myfiles/"+filename);
				file.createNewFile();

				InputStream  fis = new BufferedInputStream(socket.getInputStream());
				OutputStream os = new FileOutputStream(file);

				while (fis.read(buffer) != -1) {
					os.write(buffer);
				}

				fis.close();
				os.close();
				reader.close();
				printer.close();
			}
		}else{
			System.out.println("I am already up to date!");
		}
	}
	
	public void sync() throws UnknownHostException, IOException {
		getMetaData();
		syncFiles();
	}
	
	private static void usage() {
		System.out.println("Usage:");
		System.out.println("FileSync CLIENT_ID ROOT_DIRECTORY SERVER_HOST_NAME FILE_SERVER_PORT META_DATA_SERVER_PORT");
	}
	
	public static void main(String[] args) {
		if (args.length != 5) {
			usage();
			System.exit(-1);
		}
		
		String clientId = args[0];
		
		String rootDirStr = args[1];
		File rootDir = new File(rootDirStr);
		if (!rootDir.isDirectory()) {
			System.err.println("Root directory '" + rootDir + "' is no directorty.");
			System.exit(-1);
		}
		if (!rootDir.exists()) {
			System.err.println("Root directory '" + rootDir + "' does not exist.");
			System.exit(-1);
		}
	
		String serverHostName = args[2];
		
		int fileServerPort = -1;
		try {
			fileServerPort = Integer.parseInt(args[3]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid file server port.");
			System.exit(-1);
		}
		
		int metaDataServerPort = -1;
		try {
		 metaDataServerPort = Integer.parseInt(args[4]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid meta data server port.");
			System.exit(-1);
		}
		
		SyncClient client = new SyncClient(clientId, rootDir, serverHostName, fileServerPort, metaDataServerPort);
		try {
			client.sync();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
