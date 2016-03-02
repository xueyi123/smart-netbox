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


import com.iih5.netbox.actor.scheduler.LoopScheduledTask;
import com.iih5.netbox.actor.scheduler.ScheduledTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 任务提交者接口,同一个Submiter提交的所有任务，能确保在同一个线程中顺序执行
 * @author Chenlong
 * */
public interface IActor {
	/**
	 * @return 当前Actor状态
	 * */
	ActorState getActorState();
	/**
	 * 获取任务执行Executor（线程）
	 * */
	IActorExecutor getExecutor();
	/**
	 * 设置提交者的任务执行Executor（线程）
	 * */
	void setExecutor(IActorExecutor executor);
	/**
	 * 立即将任务提交到队列中
	 * */
	Future<?> execute(Runnable task);
	/**
	 * 延时将任务提交到队列中
	 * @param task 任务
	 * @param delay 延迟时间
	 * @param unit 时间单位
	 * @return future
	 * */
	Future<?> scheduledTask(Runnable task, long delay, TimeUnit unit);
	/**
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
	 * */
	Future<?> scheduleAtFixedRateTask(Runnable task, long delay, long period, TimeUnit unit);
	/**
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)
     * */
	Future<?> scheduleWithFixedDelayTask(Runnable task, long delay, long period, TimeUnit unit);
    /**
     * 立即将任务提交到队列中
	 * @param task 任务
	 * @return future
     * */
    <T> Future<T> execute(Callable<T> task);

    /**
     * 延时将任务提交到队列中
     * */
    Future<?> scheduledTask(Callable<?> task, long delay, TimeUnit unit);
	/**
	 * @param task 添加定时循环执行的任务
	 * */
	public Future<?> addLoopTask(LoopScheduledTask task);
	/**
	 * @param  task 添加延迟执行一次的任务
	 * @return  future
	 * */
	public Future<?> addTask(ScheduledTask task);
	/**
	 * 取消指定名称的任务
	 * @param name 任务唯一标示名 @see com.jcwx.frm.current.scheduled.ScheduledTask#getName()
	 * @param mayInterruptIfRunning 是否允许任务线程正在执行时中断
	 * @return true 成功取消 false 取消失败或者已经被执行
	 * */
	public boolean cancelTask(String name, boolean mayInterruptIfRunning);

}
