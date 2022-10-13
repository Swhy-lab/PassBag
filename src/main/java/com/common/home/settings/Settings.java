package com.common.home.settings;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@PropertySource("classpath:config/common.properties")
@Data
public class Settings {
	private Path home_dir;
	
	@Value("${home_dir.env}")
	public void setHomeDir(String env) {
		home_dir = update_to_path(System.getenv(env));
	}
	
	
	private static Path update_to_path(String env) {
		if(StringUtils.isBlank(env)) {
			return null;
		}
		if(Files.exists(Paths.get(env))){
			return Paths.get(env);
		}
		return null;
	}
	
}
