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
package com.iih5.netbox.actor.scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Chenlong
 * 延时线程任务封装抽象类
 * */
public abstract class ScheduledTask implements Runnable{
	protected String name;//任务名
	protected long delay;//初次执行延迟时间
	protected TimeUnit unit;//时间单位
	protected List<TaskFutureListener> listeners=new LinkedList<TaskFutureListener>();
    /**
     * @param name 任务对象唯一标识名
     * 可以用该标识名取消任务
     * */
	public ScheduledTask(String name){
		this.name=name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    /**
     * @return  延迟执行时间值
     * */
	public long getDelay() {
		return delay;
	}
    /**
     * @param delay 延迟执行时间值
     * */
	public void setDelay(long delay) {
		this.delay = delay;
	}
    /**
     * @return  延迟执行任务的时间单位
     * */
	public TimeUnit getUnit() {
		return unit;
	}
    /**
     * @param  unit 延迟执行任务的时间单位
     * */
	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}
    /**
     * @param promise 任务监听器,任务完成后会调用TaskFutureListener.completed();
     * */
	public void addListener(TaskFutureListener promise){
		listeners.add(promise);
	}
    /**
     * @param promise 移除指定的任务监听器
     * */
	public void removeListener(TaskFutureListener promise){
		listeners.remove(promise);
	}
    /**
     * 任务执行逻辑
     * */
	public abstract void execute();
	public void run(){
		execute();
		for(TaskFutureListener listener:listeners){
			listener.completed(this);
		}
	}
	
}
