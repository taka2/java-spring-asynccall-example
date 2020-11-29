package com.example.demo;

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
public class ExceptionHandlerRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			int resultOld = addAsyncOld(1, 2);
			System.out.println(resultOld);
		} catch(Exception e) {
			System.err.println(e);
		}

		try {
			int result1 = addAsync1(1, 2);
			System.out.println(result1);
		} catch(Exception e) {
			System.err.println(e);
		}
		
		try {
			int result2 = addAsync2(1, 2);
			System.out.println(result2);
		} catch(Exception e) {
			System.err.println(e);
		}
		try {
			int result3 = addAsync3(1, 2);
			System.out.println(result3);
		} catch(Exception e) {
			System.err.println(e);
		}
	}

	// 昔のやり方
	private int addAsyncOld(int a, int b) throws Exception {
		RunnableOld runnableOld = new RunnableOld(a, b);
		Thread t = new Thread(runnableOld);
		// 非同期処理開始
		t.start();
		// 終わるまで待つ
		t.join();
		if(runnableOld.getException() != null) {
			throw runnableOld.getException();
		} else {
			return runnableOld.getResult();
		}
	}
	
	private class RunnableOld implements Runnable {
		private int a;
		private int b;
		private int result;
		private Exception exception;

		public RunnableOld(int a, int b) {
			this.a = a;
			this.b = b;
		}
	
		public void run() {
			if(true) {
				this.exception = new Exception("old");
			} else {
				this.result = a + b;
			}
		}
		
		public int getResult() {
			return this.result;
		}
		
		public Exception getException() {
			return this.exception;
		}
	}
	
	// やり方その1
	private int addAsync1(int a, int b) throws Exception {
		ExecutorService executorService = Executors.newCachedThreadPool();
		Future<Integer> future = executorService.submit(() -> {
			if(true) {
				throw new Exception("new1");
			} else {
				return a + b;
			}
		});
		executorService.shutdown();
		
		return future.get();
	}
	
	// やり方その2
	private int addAsync2(int a, int b) throws Exception {
		CompletableFuture<Integer> completableFuture = CompletableFuture.<Integer>supplyAsync(() -> {
			if(true) {
				// 検査例外は使えない
				// https://stackoverflow.com/questions/40795420/try-catch-when-calling-supplyasync
				throw new RuntimeException("new2");
			} else {
				return a + b;
			}
		});
		
		return completableFuture.get();
	}
	
	@Autowired
	private AsyncFunction asyncFunction;
	
	// やり方その3
	private int addAsync3(int a, int b) throws Exception {
		return asyncFunction.add(a, b);
	}
	
	@Component
	private class AsyncFunction {
		@Async
		public int add(int a, int b) throws Exception {
			if(true) {
				throw new Exception("new3");
			} else {
				return a + b;
			}
		}
	}
}
