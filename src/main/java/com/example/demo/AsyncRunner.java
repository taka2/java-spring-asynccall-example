package com.example.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		int resultOld = addAsyncOld(1, 2);
		System.out.println(resultOld);
		int result1_1 = addAsync1_1(1, 2);
		System.out.println(result1_1);
		int result1_2 = addAsync1_2(1, 2);
		System.out.println(result1_2);
		int result2 = addAsync2(1, 2);
		System.out.println(result2);
		int result3 = addAsync3(1, 2);
		System.out.println(result3);
	}

	// 昔のやり方
	private int addAsyncOld(int a, int b) throws Exception {
		RunnableOld runnableOld = new RunnableOld(a, b);
		Thread t = new Thread(runnableOld);
		// 非同期処理開始
		t.start();
		// 終わるまで待つ
		t.join();
		return runnableOld.getResult();
	}
	
	private class RunnableOld implements Runnable {
		private int a;
		private int b;
		private int result;

		public RunnableOld(int a, int b) {
			this.a = a;
			this.b = b;
		}
	
		public void run() {
			this.result = a + b;
		}
		
		public int getResult() {
			return this.result;
		}
	}
	
	// やり方その1-1
	private int addAsync1_1(int a, int b) throws Exception {
		ExecutorService executorService = Executors.newCachedThreadPool();
		Future<Integer> future = executorService.submit(new Callable<Integer>() {
			public Integer call() {
				return a + b;
			}
		});
		executorService.shutdown();
		
		return future.get();
	}
	
	// やり方その1-2（lambda版）
	private int addAsync1_2(int a, int b) throws Exception {
		ExecutorService executorService = Executors.newCachedThreadPool();
		Future<Integer> future = executorService.submit(() -> {
			return a + b;
		});
		executorService.shutdown();
		
		return future.get();
	}
	
	// やり方その2
	private int addAsync2(int a, int b) throws Exception {
		CompletableFuture<Integer> completableFuture = CompletableFuture.<Integer>supplyAsync(() -> {
			return a + b;
		});
		
		return completableFuture.get();
	}
	
	@Autowired
	private AsyncFunction asyncFunction;
	
	// やり方その3
	private int addAsync3(int a, int b) {
		return asyncFunction.add(a, b);
	}
	
	@Component
	private class AsyncFunction {
		@Async
		public int add(int a, int b) {
			return a + b;
		}
	}
}
