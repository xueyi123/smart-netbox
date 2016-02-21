/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The SmartORM Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
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
