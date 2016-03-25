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

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


/**
 * 任务处理服务接口
 * @author Chenlong
 * */
public interface IActorManager {
	/**
	 * 用来提交延迟任务的缓冲线程池,需要提交延迟处理的任务时，通过该线程池延迟提交到执行任务队列中
	 * */
	ScheduledExecutorService getScheduledExecutorService();
	/**
	 * 创建一个TaskSubmiter
	 * */
	IActor createActor();
	/**
	 * 分配一个RunnableExecutor,多个不同的TaskSubmiter可以共用同一个RunnableExecutor
	 * RunnableExecutor是负责消费执行任务队列的
	 * */
	IActorExecutor assignActorExecutor();
	/**
	 * 获取所有RunnableExecutor
	 * */
	List<IActorExecutor> getActorExecutors();

    void shutdown();

    IActor createActor(String actorName);

    IActor getActor(String actorName);
}
