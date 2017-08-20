package net.sourceforge.veditor.builder;

public class ParseErrorBase {

	protected int linenr;
	protected String message;
	protected int startinmatchedstring;
	protected int endinmatchedstring;
	protected String filename;
	
	
	public boolean parse(String string) {return false;}
	
	
	

}