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

import com.iih5.netbox.actor.scheduler.LoopScheduledTask;
import com.iih5.netbox.actor.scheduler.ScheduledTask;
import com.iih5.netbox.actor.scheduler.TaskFutureListener;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 并发包中核心的类，负责处理所有任务，并且保证同一Actor提交的任务
 * 都在同一线程中顺序执行,每个Actor对应一个
 * ActorExecutor但是同一个ActorExecutor 可以为多个Actor服务
 * Actor可以切换ActorExecutor 切换时为了保证线程安全一致性
 * 会改变Actor状态为ActorState.TRANSITIVE 处于此状态时，
 * 任务不会马上提交执行，而是会先放入缓冲队列中等待状态恢复正常时下次任务激活执行
 * */
public class Actor implements IActor {
	protected volatile IActorExecutor executor;
	private BlockingQueue<Runnable> waitQueue=new LinkedBlockingQueue<Runnable>();
	private volatile ActorState state= ActorState.NORMAL;
	private Lock lock=new ReentrantLock(true);
	private IActorManager parent;
	private ScheduledExecutorService scheduledExecutorService;
	private Map<String, Future<?>> futures;
	public Actor(IActorManager parent){
		this.parent=parent;
		this.scheduledExecutorService=parent.getScheduledExecutorService();
		this.futures=new TreeMap<String, Future<?>>();
	}
	public IActorExecutor getExecutor(){
		return executor;
	}
	/**
	 * 如果是切换Executor,在切换Executor之前，必须将State设置为SubmiterState.TRANSITIVE
	 * */
	public void setExecutor(IActorExecutor executor){
		lock.lock();
		try {
			if(executor==null){
				throw new NullPointerException("任务处理器executor不能为空,否则提交的任务不能执行");
			}
			if(this.executor!=null&&this.executor.workThread()!=null&&this.executor!=executor){//切换Executor
				System.err.println("Actor 切换线程:old={},new={} "+this.executor.workThread()+executor.workThread());
				state= ActorState.TRANSITIVE;
				transfer(executor);
				state= ActorState.NORMAL;
			}
			this.executor=executor;
		}finally{
			lock.unlock();
		}

		
	}
    /**
     * 将缓冲队列中的任务提交到executor的执行队列中
     * */
	private void transfer(IActorExecutor executor) {
		if(state== ActorState.TRANSITIVE){//必须是过渡状态才允许此操作
			for(Runnable task:waitQueue){
				executor.submit(task);
			}
		}else{
			throw new IllegalStateException("非"+ ActorState.TRANSITIVE+"状态不能执行此操作");
		}
	}
	public ActorState getActorState() {
		// TODO Auto-generated method stub
		return state;
	}
    /**
     * @param  task 构造的Future
     * @return true 过渡期任务，提交到缓冲waitQueue中 false 任务会直接提交到执行队列中,走executor.submit()流程
     * */
	private boolean executeTransTask(Runnable task) {
        if(state== ActorState.TRANSITIVE){
            lock.lock();
            try{
                //双重检查，避免多余锁开销，先判定为过渡状态加一次锁，然后再判断状态
                if(state== ActorState.TRANSITIVE){
                	System.err.println("过渡期新任务:actor={}"+this.toString());
                    waitQueue.put(task);
                    return true;
                }else{
                    return  false;
                }
            }catch (Exception e){
            	System.err.println("处理过渡期任务异常"+e);
            }finally {
                lock.unlock();
            }

        }

		return false;
	}
	/**
	 * 保证同一Session中的任务必须在同一线程中顺序执行
	 * 判断Submiter是否为过渡状态，如果是过渡状态
	 * 则任务不直接提交到Executor执行队列中，而且是提交到过渡任务队列中
	 * 当submiter切换Executor完毕后，将过渡任务队列中的内容提交到新的Executor执行。
	 * */
	public Future<?> execute(Runnable task) {
		FutureTask<Object> future=new FutureTask<Object>(task, null);
		if(executor==null){
			synchronized (parent.getActorExecutors()) {
				executor=parent.assignActorExecutor();
				executor.incrActorCount();
			}
		}
		//过渡期任务,Actor即将变更Executor
		if(executeTransTask(future)){
			executor.decrActorCount();
		}else{
			executor.submit(future);
		}
		return future;
	}
	public Future<?> scheduledTask(final Runnable task,
			long delay, TimeUnit unit) {
		if(delay<=0){
			return execute(task);
		}
		Future<?> result=scheduledExecutorService.schedule(new Runnable() {
			public void run() {
				execute(task);
			}
		}, delay, unit);
		return result;
	}
	public Future<?> scheduleAtFixedRateTask(final Runnable task, long delay, long period, TimeUnit unit) {
		return scheduledExecutorService.scheduleAtFixedRate(new Runnable() {			
			public void run() {
				execute(task);
			}
		}, delay,period, unit);
	}
	public Future<?> scheduleWithFixedDelayTask(final Runnable task, long delay, long period, TimeUnit unit) {
		return scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {			
			public void run() {
				execute(task);
			}
		}, delay,period, unit);
	}

    public <T> Future<T> execute(final Callable<T> task) {
        FutureTask<T> future=new FutureTask<T>(task);
        if(executor==null){
            synchronized (parent.getActorExecutors()) {
                executor=parent.assignActorExecutor();
            }
        }
        if(executeTransTask(future)){

        }else{
            executor.submit(future);
        }
        return future;
    }

    public Future<?> scheduledTask(final Callable<?> task, long delay, TimeUnit unit) {
        if(delay<=0){
            return execute(task);
        }
		return scheduledExecutorService.schedule(new Runnable() {
            public void run() {
                execute(task);
            }
        }, delay, unit);
    }
	/**
	 * @param task 添加定时循环执行的任务
	 * */
	public Future<?> addLoopTask(LoopScheduledTask task){
		if(!futures.containsKey(task.getName())){
			Future<?> future=null;
			if(task.isFixRate()){
				future=scheduleAtFixedRateTask(task, task.getDelay(), task.getPeriod(), task.getUnit());
			}else{
				future=scheduleWithFixedDelayTask(task, task.getDelay(), task.getPeriod(), task.getUnit());
			}
			futures.put(task.getName(), future);
			return futures.get(task.getName());
		}else{
			System.err.println("定时任务已存在:name={}"+task.getName());
			return null;
		}
	}
	/**
	 * @param  task 添加延迟执行一次的任务
	 * */
	public Future<?> addTask(ScheduledTask task){
		if(!futures.containsKey(task.getName())){
			task.addListener(new DefaultTaskFutureListener());
			Future<?> future=scheduledTask(task, task.getDelay(), task.getUnit());
			futures.put(task.getName(), future);
			return futures.get(task.getName());
		}else{
			System.err.println("定时任务已存在:name={}"+task.getName());
			return null;
		}

	}
	/**
	 * 取消指定名称的任务
	 * @param name 任务唯一标示名 @see com.jcwx.frm.current.scheduled.ScheduledTask#getName()
	 * @param mayInterruptIfRunning 是否允许任务线程正在执行时中断
	 * */
	public boolean cancelTask(String name,boolean mayInterruptIfRunning){
		Future<?> future=futures.get(name);
		if(future!=null){
            futures.remove(name);
			boolean b=future.cancel(mayInterruptIfRunning);
			return b;
		}else{
			System.err.println("任务不存在:name = {}"+ name);
			return false;
		}

	}
	/**
	 * 任务执行完毕后，会从缓存中移除保存的Future对象，只针对ScheduledTask
	 * */
	private class DefaultTaskFutureListener implements TaskFutureListener {

		public void completed(ScheduledTask task) {
			cancelTask(task.getName(), true);
		}

	}

	protected void finalize() throws Throwable {
		super.finalize();
		if(executor!=null){
			executor.decrActorCount();
		}
	}
}
