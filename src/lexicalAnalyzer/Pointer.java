package lexicalAnalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Pointer {
	private static Pointer pointer;
	private Scanner scanner;
	private String data;
	private int index;
	private int EOF;

	public Pointer() throws IOException {
		this.init();
		this.index = 0;
	}

	public static synchronized Pointer getInstance() throws IOException {
		if (pointer == null)
			pointer = new Pointer();
		return pointer;
	}

	private void init() throws IOException {
		String directory = "./src/teste.txt";
		File file = new File(directory);
		FileInputStream fis = new FileInputStream(file);
		byte[] _data = new byte[(int) file.length()];
		fis.read(_data);
		fis.close();
		this.data = new String(_data, "UTF-8").trim();
		this.scanner = new Scanner(this.data);
		this.scanner.useDelimiter("[\r\n]+");
		this.EOF = this.data.length();
	}

	Character nextChar(){
		Character ch = null;
		if (!this.isEOF()) {
			ch = this.data.charAt(index);
			this.index++;
		}
		return ch;
	}

	public boolean isEOF() {
		return this.index >= this.EOF;
	}
	
	public void comeBack(int num) {
		this.index -= num;
	}

	String nextLine() {
		if(this.scanner.hasNextLine())
		return this.scanner.nextLine();
		else return "";
	}
		
	void jumpLine() {
		this.index += 1 + (this.index - this.scanner.nextLine().length());
	}
}