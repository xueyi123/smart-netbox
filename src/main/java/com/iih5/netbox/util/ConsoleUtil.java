package com.iih5.netbox.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ConsoleUtil {
	public static String errException(Exception e){
		StringBuffer buffer = new StringBuffer();
		buffer.append(e.getMessage()+"\n");
		StackTraceElement[] errElements= e.getStackTrace();
		for (StackTraceElement s : errElements) {
			buffer.append(s.toString()+"\n");
		}
		return buffer.toString();
	}
}
