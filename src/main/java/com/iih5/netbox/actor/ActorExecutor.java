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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 顺序执行的RunnableExecutor
 * 确保先提交的任务一定先执行
 * @author Chenlong
 * */
public class ActorExecutor implements IActorExecutor {
	private BlockingQueue<Runnable> queue=new LinkedBlockingQueue<Runnable>(500000);
	private Thread thread;
	private AtomicInteger actorCount=new AtomicInteger(0);
	public void run() {
		this.thread=Thread.currentThread();
		for(;;){
			try {
				Runnable task=queue.take();
				task.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void submit(Runnable task){
		if(thread==Thread.currentThread()){
			task.run();
		}else{
			try {
				queue.put(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public int getUndoneTaskSize(){
		return queue.size();
	}
	/**
	 * 根据RunnableExecutor中等待完成任务的数量比较优先级
	 * */
	public int compareTo(IActorExecutor o) {
	
		// TODO Auto-generated method stub
		int compare=getUndoneTaskSize()-o.getUndoneTaskSize();
		if(compare==0){
			compare=actorCount.get()-o.getActorCount();
		}
		return compare;
	}
	public Thread workThread() {
		// TODO Auto-generated method stub
		return thread;
	}

	public void incrActorCount() {
		actorCount.getAndIncrement();
	}

	public void decrActorCount() {
		actorCount.getAndDecrement();
	}

	public int getActorCount() {
		return actorCount.get();
	}

}
