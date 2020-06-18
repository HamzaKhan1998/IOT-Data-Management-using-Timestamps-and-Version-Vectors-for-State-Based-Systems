
import java.io.File;
import Globals.*;

public class SyncServer {
	static void usage() {
		System.out.println("Usage:");
		System.out.println("Server ROOT_DIRECTORY FILE_SERVER_PORT META_DATA_SERVER_PORT");
	}
	
	public static void main(final String[] args) {

		if (args.length != 4) {
			usage();
			System.exit(-1);
		}

		final String rootDirStr = args[0];
		final File rootDir = new File(rootDirStr);
		if (!rootDir.isDirectory()) {
			System.err.println("Root Directory '" + rootDir + "' is no directory.");
			System.exit(-1);
		}
		if (!rootDir.exists()) {
			System.err.println("Root Directory '" + rootDir + "' does not exist.");
			System.exit(-1);
		}

		int fileServerPort = -1;
		try {
			fileServerPort = Integer.parseInt(args[1]);
		} catch (final NumberFormatException e) {
			System.err.println("Invalid FileServer Port.");
			System.exit(-1);
		}

		int metaDataServerPort = -1;
		try {
			metaDataServerPort = Integer.parseInt(args[2]);
		} catch (final NumberFormatException e) {
			System.err.println("Invalid MetaDataServer Port.");
			System.exit(-1);
		}
		
		//int server_id = -1;
		try {
			//server_id = Integer.parseInt(args[3]);
			GlobalConstants.serverid=args[3];
		} catch (final NumberFormatException e) {
			System.err.println("Invalid ServerID.");
			System.exit(-1);
		}

		FileServer fileServer = new FileServer(fileServerPort, rootDir);
		fileServer.start();

		final MetaDataServer metaDataServer = new MetaDataServer(metaDataServerPort, rootDir,GlobalConstants.geek);
		metaDataServer.start();

		try {
			// This will wait forever (until server is killed) since we don't implement a
			// mechanism for shutting down the servers (gracefully).
			fileServer.join();
			metaDataServer.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

}
