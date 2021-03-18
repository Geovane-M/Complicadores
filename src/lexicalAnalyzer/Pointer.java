package lexicalAnalyzer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Pointer {
	private static Pointer pointer;
	private String data;
	private int index;
	private int EOF;	
	private Pointer () throws IOException {
		this.init();
		this.index = 0;
	}
	public static synchronized Pointer getInstance() throws IOException {
		if (pointer == null)
			pointer = new Pointer();
		return pointer;
	}
	
	private void init() throws IOException {
//#################################################################################################
		//Para facilitar os testes :) Altere o diretório de acordo com a sua máquina.			  #
		String directory;																	    //#
		if (System.getProperty("os.name").compareTo("Linux") == 0) {						    //#
			directory = "/home/migeo/Documentos/eclipse-workspace/Complicadores/src/teste1.txt";//#
		} else {																			    //#	
			directory = "D:/Java_projects/analisador_lexico/src/teste.txt";                     //#
		}                                                                                       //#
//#################################################################################################
			File file = new File(directory);
			FileInputStream fis = new FileInputStream(file);
			byte[] _data = new byte[(int) file.length()];
			fis.read(_data);
			fis.close();
			this.data = new String(_data, "UTF-8");
			this.EOF = this.data.length();
	}
	
	char nextChar() {
		char ch = ' ';
		if(!this.isEOF()) {
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
}