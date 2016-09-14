import java.io.*;
import java.util.regex.*;
import java.util.*;


public class LiteratureProc_main {
	
	//String folder_dir = "E:\\Google Drive\\PhDResearch\\Literature Read\\5.9";
	String folder_dir = "C:\\Users\\Tianji\\Google 云端硬盘\\PhDResearch\\Literature Read\\5.9";
	
	public static void fdfnotes_to_txts(String[] folders_dirlist) {
		// 		
		for (final String folder_dir: folders_dirlist){
			fdfnotes_to_txt(folder_dir);
		}
	}
	
	private static void fdfnotes_to_txt(String folder_dir)
	{
		//Create single txt file for writing
		
		Err_Output err = new Err_Output();

		File folder = new File(folder_dir);
		
		if(!folder.isDirectory()){
			err.err_exit(err.NOT_DIR);
		}
		
		File sumfile = new File(folder.getName() + ".txt");

		// if file doesn't exists, then create it
		if (!sumfile.exists()){
			try{
				sumfile.createNewFile();
			}catch(IOException ex){
				err.err_exit(err.FAIL_CREATE);
			}
		}
		
        // FileWriter writes text files in the default encoding.
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        try{
			fileWriter = new FileWriter(sumfile.getAbsoluteFile());
			writer = new BufferedWriter(fileWriter);
		}catch(IOException ex){
			err.err_exit(err.FAIL_OPEN);
		}		
        
        //Main loop for reading .fdf files, extract contents and write to the target txt file
        System.out.println("The notes will be recorded in: " + sumfile.getAbsoluteFile());
        fdfnotes_to_txt_write(folder.listFiles(), writer);
		
		try{
			writer.close();
		}catch(IOException ex){
			// For bufferedReader section
			err.err_exit(err.FAIL_CLOSE);
		}
	}
	
	private static void fdfnotes_to_txt_write(File[] filelists,BufferedWriter writer)
	{
		//Open the fdf files to write
		Err_Output err = new Err_Output();
		int num_fdf_file = 0;
		
		for (final File filefdf: filelists) {
	        if (filefdf.isFile()) {
	        	CharSequence _ftype = ".fdf";
	        	String fdffilename = filefdf.getName();
	        	
	        	if (fdffilename.contains(_ftype)){
	        		FileReader fileReader = null;
	        		BufferedReader reader = null;
	        		
	        		try{
	        			fileReader = new FileReader(filefdf.getAbsoluteFile());
	        			reader = new BufferedReader(fileReader);
	        		}catch(FileNotFoundException ex){
	        			err.err_exit(err.FAIL_OPEN);
	        		}
	        		
	                fdfnote_to_txt_write(reader, writer,fdffilename);
	                System.out.println(fdffilename + " is recorded into the .txt file.");
	                
		            try{
		            	reader.close();
		            }catch(IOException ex){
		            	err.err_exit(err.FAIL_CLOSE);
		            }
	        	}
	        	else
	        	{
	        		System.out.println(fdffilename + " is not " + _ftype +" comment file.");
	        		continue;
	        	}
	            
	        } else {
	        	System.out.println(filefdf.getName() + " is a directory or not exist.");
	        	continue;
	        }
	        num_fdf_file++;
	    }
		System.out.println("Folder contains " + num_fdf_file + " fdf note files");
	}
	
	private static void fdfnote_to_txt_write(BufferedReader reader, BufferedWriter writer, String fdffilename)
	{
		//Write the fdf file to the txt file
		Err_Output err = new Err_Output();
		
		StringBuffer content = new StringBuffer();
        String line = new String();
        
        try{
            while((line = reader.readLine()) != null){
                content.append(line);
            }
        }catch(IOException ex){
        	err.err_exit(err.FAIL_READ);;
        }
        
        // Extract comments in the fdf files with regex
        Pattern pattern = Pattern.compile("Contents" + "[(]" + "(.*?)" + "[)]" + "/CreationDate");
        Matcher matcher = pattern.matcher(content);
        
        try{
            writer.write("File Name" + ": " + fdffilename);
            writer.newLine();
            writer.newLine();
            int j = 0;
            while (matcher.find()) {
               line = matcher.group(1);
               writer.write("Record " + j + ":");
               line.replaceAll("\\\\[(]", "(");
               line.replaceAll("\\\\[)]", ")");
               writer.newLine();
               String[] lines = line.split("\\\\" + "[r]");
               for (String rec: lines){
            	   writer.write(rec);
            	   writer.newLine();
               }
               writer.newLine();
               j++;
            }
            //Separate the next record
            writer.newLine();
            writer.newLine();
        }catch(IOException ex){
        	err.err_exit(err.FAIL_WRITE);
        }
	}
	
	// Err processing class: message, output and quit
	private static class Err_Output {
		
		// Err info index
		private final int NOT_DIR = 0;
		private final int FAIL_CREATE = 1;
		private final int FAIL_OPEN = 2;
		private final int FAIL_CLOSE = 3;
		private final int FAIL_WRITE = 4;
		private final int FAIL_READ = 5;
		
		private Map<Integer, String> map = new HashMap<Integer, String>();
		
		public Err_Output()
		{
			
			map.put(NOT_DIR, "Not a valid directory or not exist, quit the job");
			map.put(FAIL_CREATE,"File is not created, quit");
			map.put(FAIL_OPEN, "Failed to open file, it may not exist, quit");
			map.put(FAIL_CLOSE, "Failed to close file, quit");
			map.put(FAIL_WRITE, "Failed to write file, quit");
			map.put(FAIL_READ, "Failed to read file, quit");
		}
		public void err_exit(int i){
			System.out.println(map.get(i));
			System.exit(0);
		}
	}
}

