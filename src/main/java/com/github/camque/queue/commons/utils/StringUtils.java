package com.github.camque.queue.commons.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtils {

	/**
     * Get stacktrace
     * @param e Exception
     * @return
     */
    public static String getStackTrace(final Exception e){
    	final StringWriter errors = new StringWriter();
		e.printStackTrace( new PrintWriter(errors) );
		return errors.toString();
    }
    
    /**
     * Converts a separate | (pipe) string in a list
     * @param value Value to separate
     * @param separator Character delim
     * @return
     */
	public static List<String> separatedListToList(final String value, final String separator){
		final List<String> list = new ArrayList<>();

		final StringTokenizer tokens = new StringTokenizer( value, separator );
		while( tokens.hasMoreTokens() ){
			list.add( tokens.nextToken().trim() );
		}

		return list;
	}


}
