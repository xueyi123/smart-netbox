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

public class Mtest {

	public static void main(String[] args) {
		// System.out.println(DateUtil.convertDateToStr(new Date(), DateUtil.TIME_FORMAT_MSEL));
		QueueActorManager manager = new QueueActorManager(8,CurrentUtils.createThreadFactory("Game-Table-Pool-"));
		for (int i = 0; i < 1000; i++) {
			IActor actor= manager.createActor();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.err.println("========================"+ actor.getExecutor().workThread().getId());
		}
		//  System.out.println(DateUtil.convertDateToStr(new Date(), DateUtil.TIME_FORMAT_MSEL));
	}
}
