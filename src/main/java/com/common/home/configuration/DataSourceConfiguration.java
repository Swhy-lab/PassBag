package com.common.home.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.json.simple.JSONObject;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.gson.JsonObject;




@Configuration
@EnableTransactionManagement
//@PropertySource("classpath:config/${spring.profiles.active}.properties")
@PropertySource("classpath:config/common.properties")
public class DataSourceConfiguration {
	private final String sqlMapperPath = "classpath:/sqlmap/mappers/*.xml";
	private final String sqlMapperConfig ="classpath:/sqlmap/sql-mapper-config.xml";
	private final String driverClassName ="org.sqlite.JDBC";
	private final String dbName = "sqlite.db";
	private String url = null;
	private Path db;
	//String query ="INSERT INTO dual(x, create_date) values(?, ?) ON CONFLICT(x) DO UPDATE SET create_date=?;";

	
    @Autowired
    private ResourceLoader resourceLoader;
	
	

	public Path update_to_path(String env) {
		String path = System.getenv(env);		
		Path p = null;
		Path db = null;
		if(path == null) {
			return null;
		}
		
		try {
			p =  Paths.get(path);
			if(!Files.exists(p)) {
				Files.createDirectories(p);							
			}
			db = p.resolve(dbName); 
			if(!Files.exists(db)) {
				Files.createFile(db);
			}
			url = "jdbc:sqlite:"+db.toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return db;
	}
	

    
    public boolean isPrepared() {   
    	//파일이 없으면 동작하지 않는다.
    	Connection conn = null;
    	boolean state = false;    	
    	if(Files.exists(db)) {
    		try {
    			Class.forName(driverClassName);
        		conn = DriverManager.getConnection(url);
        		if(conn != null) {
        			conn.close();
        		}
        		state = true;
        	}catch(Exception e) {
        		e.printStackTrace();
        		state = false;
        	}finally {
        		if(conn != null) {
        			try {
        				conn.close();
        			}catch(Exception e) {        				
        			}
        		}
        	}
    	}
    	
    	return state;
    }
    
    private boolean updateDatabase() {
    	
    	//데이터베이스를 생성한다.
    	Connection conn = null;
    	boolean result = false;

    	
    	/*Query*/
    	try {
    		
    		Class.forName(driverClassName);
    		conn = DriverManager.getConnection(url);
    		
    		if(conn == null) {
    			return result;
    		}
    		
    		/*FUNCTION_LOG*/
    		try(Statement stat = conn.createStatement()){
    			//stat.executeUpdate("DROP TABLE IF EXISTS dual;");
    			final String function_log_query = "CREATE TABLE IF NOT EXISTS COMMON_LOG("
    					+ "cl_id int primary key,"
    					+ "type text,"
    					+ "ip text,"
    					+ "content text,"
    					+ "create_date text"
    					+ ");";
    			stat.executeUpdate(function_log_query);

    			final String query = "INSERT INTO COMMON_LOG(cl_id, type, ip, content, create_date) values(?, ?, ?, ?, ?) ON CONFLICT(cl_id) DO UPDATE SET create_date=?";    			
    			
    			try(PreparedStatement prep= conn.prepareStatement(query)){
    				LocalDateTime local = LocalDateTime.now();
    				String create_date = local.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    				prep.setInt(1, 1);
    				prep.setString(2, "system");
    				prep.setString(3, "127.0.0.1");
    				prep.setString(4, new JSONObject() {
    					{
    						put("msg","Database is created.");
    					}
    				}.toJSONString());
    				prep.setString(5, create_date);    				
    				prep.addBatch();
    				conn.setAutoCommit(false);
        			prep.executeBatch();
        			conn.setAutoCommit(true);
    			}
    		}
    		
    		
    		//success
    		result = true;
    		
    	}catch(Exception e) {
    		
    		e.printStackTrace();
    	}finally {
    		if(conn != null) {
    			try {
    				conn.close();
    			}catch(Exception e){
    				
    			}
    		}
    	}
    	
    	return result;
    }
    
	@Bean(name="dataSource")
	public BasicDataSource getDataSource() {
		update_to_path("COMMON_DB_HOME");
		updateDatabase();
		System.out.println("driverClassName : "+driverClassName);
		System.out.println("url : "+url);
		BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(driverClassName);
			dataSource.setUrl(url);
			dataSource.setInitialSize(10);
			dataSource.setMaxActive(200);
			dataSource.setMaxIdle(20);
			dataSource.setDefaultAutoCommit(true);
			dataSource.setTestWhileIdle(true);
			dataSource.setTimeBetweenEvictionRunsMillis(600000);
			
		return dataSource;
	}
	
	
	@Bean(name="transactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") BasicDataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}
	@Bean(name="SqlSessionFactory")
	public SqlSessionFactoryBean getSqlSessionFactory(@Qualifier("dataSource") BasicDataSource dataSource) {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
	
		sqlSessionFactoryBean.setDataSource(dataSource);
		
		sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(sqlMapperConfig));
	
		
		try {
			sqlSessionFactoryBean.setMapperLocations(ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(sqlMapperPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sqlSessionFactoryBean;
	}
	
	@Bean(name="SqlSession")
	public SqlSessionTemplate getSqlSessionTemplate(@Qualifier("SqlSessionFactory")SqlSessionFactory sqlSessionFactory) {
		SqlSessionTemplate sqlSession = new SqlSessionTemplate(sqlSessionFactory);
		return sqlSession;
	}
	
}
