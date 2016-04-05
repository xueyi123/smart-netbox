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

package actor;

import com.iih5.actor.ActorManager;
import com.iih5.actor.IActor;
import com.iih5.actor.util.ThreadFactoryUtil;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestActor {

	public static void main(String[] args) {
		// System.out.println(DateUtil.convertDateToStr(new Date(), DateUtil.TIME_FORMAT_MSEL));
		ActorManager manager = new ActorManager(8, ThreadFactoryUtil.createThreadFactory("Game-Table-Pool-"));
//		for (int i = 0; i < 1000; i++) {
//			IActor actor= manager.createActor();
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			System.err.println("========================"+ actor.getExecutor().workThread().getId());
//
//		}
		IActor actor= manager.createActor();
		//actor.setExecutor();
		for (int i = 0; i < 10000; i++) {

			Future<?> d= actor.scheduledTask(new Runnable() {
				public void run() {
					System.out.println("hello world");
				}
			},1, TimeUnit.SECONDS);
		}
		//d.cancel(true);
		//System.out.println("取消了");
	}
}
