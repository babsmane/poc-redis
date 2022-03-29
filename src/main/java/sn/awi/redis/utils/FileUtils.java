package sn.awi.redis.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import net.lingala.zip4j.ZipFile;

public class FileUtils {

	public static boolean isArchive(File f) {
		int fileSignature = 0;
		try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
			fileSignature = raf.readInt();
		} catch (IOException e) {
			// handle if you like
		}
		return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
	}

	public static void unzipFolderZip4j(Path source, Path target) throws IOException {
		new ZipFile(source.toFile()).extractAll(target.toString());
	}
	
	public static void resetTemporaryDirectory(File tmp){
		if(!tmp.exists())
			tmp.mkdir();
		else{
			File[] allContents = tmp.listFiles();
		    if (allContents != null) {
		        for (File file : allContents) {
		            deleteDirectory(file);
		        }
		    }
			tmp.delete();
			tmp.mkdir();
		}
	}
	
	public static void deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    directoryToBeDeleted.delete();
	}

}
