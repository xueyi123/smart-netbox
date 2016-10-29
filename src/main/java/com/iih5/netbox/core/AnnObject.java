
package com.iih5.netbox.core;

import java.lang.reflect.Method;

public class AnnObject {
	private Class<?> clas;
	private Method method;
	
	public AnnObject( Class<?> clas,Method method){
		this.clas=clas;
		this.method=method;
	}

	public Class<?> getClas() {
		return clas;
	}

	public void setClas(Class<?> clas) {
		this.clas = clas;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
