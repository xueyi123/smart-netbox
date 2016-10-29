
package com.iih5.netbox.core;

import java.util.HashMap;
import java.util.Map;

public class CmdHandlerCache {
	//单例操作
	private static CmdHandlerCache cmdStore=new CmdHandlerCache();
	public static synchronized CmdHandlerCache getInstance(){
		if (cmdStore==null) {
			cmdStore = new CmdHandlerCache();
		}
		return cmdStore;
	}
	
	private Map<Short, AnnObject> msgHandlers=new HashMap<Short, AnnObject>(300);
	
	public void putCmdHandler(short cmdId,AnnObject obj) {
		if (msgHandlers.containsKey(cmdId)) {
			throw new NullPointerException("重复加载cmd:"+cmdId+" method="+obj.getMethod().getName());
		}else {
			msgHandlers.put(cmdId, obj);
		}
	}
	public Map<Short, AnnObject> getCmdHandlers() {
		return msgHandlers;
	}
	public AnnObject getAnnObject(short cmdId) {
		try {
			AnnObject cls=msgHandlers.get(cmdId);
			if (cls==null) {
				return null;
			}         	
			return cls;
		} catch (Exception e) {
            e.printStackTrace();
		}
		return null;
	}
}
