
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer extends Thread {
	private class RequestHandler extends Thread {
		public final static int BufferSize = 1024;
		
		private Socket socket;
		
		public RequestHandler(Socket socket) {
			this.socket = socket;
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
		
		private void resetModificationBit(File file, String clientID) throws IOException {
			File metafile = new File("./myfiles/"+ file.getName()+Globals.GlobalConstants.MetaDataFileSuffix);
			updatefunc(metafile,clientID+",1",clientID+",0");
		}
		
		@Override
		public void run() {
			byte[] buffer = new byte[BufferSize];
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String clientID = reader.readLine();
				String fileName = reader.readLine();
				System.out.println(clientID+"  "+fileName);

				File file = new File("./myfiles/"+ fileName);
				
				OutputStream os = new BufferedOutputStream(socket.getOutputStream());
				
				InputStream fis = new BufferedInputStream(new FileInputStream(file));
				
				while (fis.read(buffer) != -1) {
					os.write(buffer);
				}
				
				resetModificationBit(file, clientID);
				
				fis.close();
				os.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int port;
	private File rootDir;
	
	public FileServer(int port, File rootDir) {
		this.port = port;
		this.rootDir = rootDir;
	}

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket socket = serverSocket.accept();
				RequestHandler requestHandler = new RequestHandler(socket);
				requestHandler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
