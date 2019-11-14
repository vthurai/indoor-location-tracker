package com.main;

import java.io.IOException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class Application {

	public static void main(String[] args) throws IOException, InterruptedException {
		try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class)){
			LoadData loadData = context.getBean(LoadData.class);
			loadData.run();
		}
	}
}
