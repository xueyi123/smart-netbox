
package com.iih5.netbox.core;


import com.iih5.netbox.session.ISession;

public abstract class ConnectExtension {
	/**连接成功*/
	public abstract void connect(ISession session) ;
	/**断开连接*/
	public abstract void disConnect(ISession session);
	
}
