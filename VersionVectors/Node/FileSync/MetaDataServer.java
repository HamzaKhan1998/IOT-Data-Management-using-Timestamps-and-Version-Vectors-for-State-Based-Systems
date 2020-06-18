import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import Globals.*;
public class MetaDataServer extends Thread {
	private class RequestHandler extends Thread {
		public final static int BufferSize = 1024;
		
		private Socket socket;
		Dictionary geek;
		private File rootDir;  
		
		public RequestHandler(Socket socket,Dictionary geek,File rootDir) {
			this.socket = socket;
			this.geek = geek;
			this.rootDir = rootDir;
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
		
		public boolean parseFile(String fileName,String searchStr) throws FileNotFoundException{
			Scanner scan = new Scanner(new File(fileName));
			while(scan.hasNext()){
				String line = scan.nextLine().toLowerCase().toString();
				if(line.contains(searchStr)){
					return true;
				}
			}
			return false;
		}

		private void sendMetaData(PrintWriter printer, String clientID) throws IOException{
			// TODO: Send meta data of local files to client using "modified bit" approach.
			//       That is, send meta data of all files that were modified since
			//       the client downloaded the file the last time or that are new (= have never
			//       been downloaded by this client so far)
			
			
			File rootDir = new File("./myfiles/");
			File[] files = rootDir.listFiles();

			if (geek.get(clientID)==null){
				System.out.println("Condition1");
				String sendingfiles = new String();
				sendingfiles+=GlobalConstants.serverid+" ";

				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (file.getPath().endsWith(GlobalConstants.MetaDataFileSuffix)) {

						if (parseFile(file.getCanonicalPath(),clientID+",0")==false && parseFile(file.getCanonicalPath(),clientID+",1")==false){
							String timestamp_file=new String();
							Scanner myReader = new Scanner(file);

							while (myReader.hasNextLine()) {
								timestamp_file = myReader.nextLine();
								break;
							}
							myReader.close();
							String upd=timestamp_file+clientID+":1_";
							updatefunc(file, timestamp_file, upd);
							FileWriter myWriter = new FileWriter("./myfiles/"+file.getName(),true);
							myWriter.write(clientID+",0\n");
							sendingfiles += file.getName();
							sendingfiles +="-";
							sendingfiles +=upd;
							sendingfiles +=" "; 
							myWriter.close();

						}
					}
				}
				geek.put(clientID, true); //server exists to msla hai duplications
				printer.println(sendingfiles);  // no data right now
			}
			else {
	
				String sendingfiles = new String();
				boolean c2=false;

				sendingfiles+=GlobalConstants.serverid+" ";

				for (int i = 0; i < files.length; i++) {
					File file = files[i];

					System.out.println(file.getName());
					
					if (file.getPath().endsWith(GlobalConstants.MetaDataFileSuffix)) {

						BufferedReader br = new BufferedReader(new FileReader(file)); 
						String line=new String();  

						boolean newfile=false;
						boolean check=false;
						c2=false;
						
						if (newfile==false){
							if ((line = br.readLine()) != null){
								while ((line = br.readLine()) != null)
								{  	
									System.out.println("Condition2");
									
									if (line.length()>0){
										if (line.charAt(0) == clientID.charAt(0)){
											System.out.println("Condition2.1");
											if (line.charAt(2) == '1' ){
												System.out.println("Condition2.1.1");
												String timestamp_file=new String();
												Scanner myReader = new Scanner(file);
												while (myReader.hasNextLine()) {
													timestamp_file = myReader.nextLine();
													System.out.println(timestamp_file);
													break;
												}
												myReader.close();
												sendingfiles += file.getName();
												sendingfiles +="-";
												sendingfiles +=timestamp_file;
												sendingfiles +=" "; 
												updatefunc(file,clientID+",1",clientID+",0");
												check=true;
											}
											else{
												System.out.println("Condition2.2");
												c2=true;
											}
										}
									}
								}
								newfile=true;
							}
						}
						if (c2!=true & check!=true){
							System.out.println("Condition3");//No file exists and we are creating new file to send to the client
							String timestamp_file=new String();
							Scanner myReader = new Scanner(file);
							while (myReader.hasNextLine()) {
								timestamp_file = myReader.nextLine();
								System.out.println(timestamp_file);
								break;
							}
							myReader.close();
							FileWriter myWriter = new FileWriter("./myfiles/"+file.getName(),true);
							myWriter.write(clientID+",0\n");
							sendingfiles += file.getName();
							sendingfiles +="-";
							sendingfiles +=timestamp_file;
							sendingfiles +=" "; 
							myWriter.close();
						}
					}
				}
				if (c2==true){
					sendingfiles="nil";
				}
				System.out.println("These files => "+sendingfiles);
				printer.println(sendingfiles);
			}
		}
		
		@Override
		public void run() {
			try {
				System.out.println("Waiting for Client ID");
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String clientID = reader.readLine();
				System.out.println("Clients on its way .... "+clientID);
				PrintWriter printer = new PrintWriter(socket.getOutputStream(),true);
				sendMetaData(printer, clientID);
				
				reader.close();
				printer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	private int port;
	private File rootDir;
	Dictionary geekm;
	
	public MetaDataServer(int port, File rootDir,Dictionary geekm) {
		this.port = port;
		this.rootDir = rootDir;
		this.geekm=geekm;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				System.out.println("Waiting for Client");
				Socket socket = serverSocket.accept();
				RequestHandler requestHandler = new RequestHandler(socket,geekm,rootDir);
				requestHandler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
