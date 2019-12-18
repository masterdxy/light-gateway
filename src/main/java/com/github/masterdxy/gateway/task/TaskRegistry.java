package com.github.masterdxy.gateway.task;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

public class TaskRegistry {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskRegistry.class);
	
	private static final List<Task> tasks = Lists.newArrayList();
	
	//start all tasks
	public static void startAll (List<Task> taskList) {
		taskList.sort(Comparator.comparingInt(Task::order));
		tasks.addAll(taskList);
		tasks.forEach(task -> {
			try {
				task.startAfterRunOnce();
			}
			catch (Exception e) {
				logger.error("task:{} start fail {}", task.name(), e);
			}
		});
	}
	
	public static void stopAll () {
		tasks.forEach(Task::stop);
	}
	
	public static void addAll (List<Task> taskList) {
		tasks.addAll(taskList);
	}
	
	public interface Task {
		void startAfterRunOnce () throws Exception;
		
		String name ();
		
		void stop ();
		
		int order ();
	}
}
