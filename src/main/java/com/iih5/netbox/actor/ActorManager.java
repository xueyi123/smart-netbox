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
package com.iih5.netbox.actor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class ActorManager implements IActorManager {
	protected int threadSize;
	protected ExecutorService threadPool;
	private static final ThreadFactory DEFAULT_THREAD_FACTORY=CurrentUtils.createThreadFactory("Message-Task-Pool-");
	
	public ActorManager(int threadSize, ThreadFactory factory){
		this.threadSize=threadSize;
		if(factory==null){
			factory=DEFAULT_THREAD_FACTORY;
		}
		initThreadPool(factory);
	}
	private ExecutorService initThreadPool(ThreadFactory factory){
		if(threadPool==null){
			this.threadPool=Executors.newFixedThreadPool(this.threadSize,factory);
		}
		return threadPool;
	}
    public void shutdown(){
        threadPool.shutdown();
    }
}
