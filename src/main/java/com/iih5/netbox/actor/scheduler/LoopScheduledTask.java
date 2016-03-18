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


/**
 * @author Chenlong
 * 定时循环执行线程封装类
 * */
public abstract class LoopScheduledTask extends ScheduledTask{
	protected boolean fixRate;//
	protected long period;//定时执行时间间隔
	public LoopScheduledTask(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
    /**
     * @return  是否绝对固定频率执行
     * */
	public boolean isFixRate() {
		return fixRate;
	}
    /**
     * @param fixRate  是否绝对固定频率执行
     * */
	public void setFixRate(boolean fixRate) {
		this.fixRate = fixRate;
	}
    /**
     * @return 循环执行任务间隔
     * */
	public long getPeriod() {
		return period;
	}
    /**
     * @param period 循环执行任务间隔
     * */
	public void setPeriod(long period) {
		this.period = period;
	}
}
