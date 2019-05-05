package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
@PropertySource(value = { "file://${HOME}/.brewer-S3.properties" }, ignoreResourceNotFound = true)
public class S3Config {

	
	@Autowired
	private Environment env;
	
	@Bean
	public AmazonS3 amazonS3() {
//		AWSCredentials credenciais = new BasicAWSCredentials(env.getProperty("AWS_ACCESS_KEY_ID"), env.getProperty("AWS_SECRET_ACCESS_KEY_ID"));
//		AWSCredentials credenciais = new BasicAWSCredentials("AKIAJYZ3WBDVJODXIFYA", "yPWdEJ1URjqLLadMA3YetPDCDB52Ab7fuuy2KGGD");
		System.out.println("access key: "+ env.getProperty("AWS_ACCESS_KEY_ID"));
		System.out.println("secret key: "+ env.getProperty("AWS_SECRET_ACCESS_KEY_ID"));
		AWSCredentials credenciais = new BasicAWSCredentials("AKIAJFKR6APPGFUNXPOA", "h99DM88ieRnj++V76TFkCCI9k/ulh5q8UyCzc3ZY");
		AmazonS3 amazonS3 = new AmazonS3Client(credenciais, new ClientConfiguration());
		Region regiao = Region.getRegion(Regions.US_EAST_1);
		amazonS3.setRegion(regiao);
		return amazonS3;
	}
	
}




