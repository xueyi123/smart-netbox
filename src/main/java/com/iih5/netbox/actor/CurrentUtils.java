/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The Smart-NetBox Project licenses this file to you under the Apache License,
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
package com.iih5.netbox.actor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CurrentUtils {
	/**
	 * 创建指定线程名前缀的ThreadFactory，后缀ID自增
	 * @param prefix 线程名前缀
	 * @return 指定线程工厂类
	 * */
	public static ThreadFactory createThreadFactory(final String prefix){
		return new ThreadFactory() {
			private AtomicInteger size=new AtomicInteger();
			public Thread newThread(Runnable r) {
				Thread thread=new Thread(r);
				thread.setName(prefix+size.incrementAndGet());
				if(thread.isDaemon()){
					thread.setDaemon(false);
				}
				//thread.setPriority(9);
				return thread;
			}
		};
	}
}
