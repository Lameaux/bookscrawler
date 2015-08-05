package com.euromoby.books;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BooksProcessor {

	private static final Logger log = LoggerFactory.getLogger(BooksProcessor.class);
	
	private Config config;
	private BooksManager booksManager;
	
	private LinkedBlockingQueue<Runnable> queue;
	private ThreadPoolExecutor pool;

	@Autowired
	public BooksProcessor(Config config, BooksManager booksManager) {
		this.config = config;
		this.booksManager = booksManager;
		
		queue = new LinkedBlockingQueue<Runnable>(config.getTaskQueueSize());
		pool = new ThreadPoolExecutor(config.getTaskPoolSize(), config.getTaskPoolSize(), 0L, TimeUnit.MILLISECONDS, queue);
		final int taskRetry = config.getTaskRetry();
		
		pool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				try {
					Thread.sleep(taskRetry);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				executor.execute(r);
			}
		});
	}

	public void startProcessing() throws IOException {

		final Path rootDir = Paths.get(config.getLocation());
		final String extension = config.getExtension();
		final String destination = config.getDestination();		

		pool.prestartAllCoreThreads();

		Files.walkFileTree(rootDir, new FileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts) throws IOException {
				if (path.toString().endsWith(extension)) {
					String fileName = path.toString();
					File file = new File(fileName);
					Integer id = Integer.parseInt(file.getName().replace(extension, ""));
					pool.submit(new BookWorker(booksManager, fileName, id, destination));
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path path, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
				log.error("Unable to access: " + path, e);
				return path.equals(rootDir) ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
			}
		});
		pool.shutdown();
		try {
			pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}

	}

}
