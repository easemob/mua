package com.easemob.mua.controller;

import com.easemob.mua.utils.SnowflakeShardingKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

/**
 * @author easemob_developer
 * @date 2022/5/26
 */

@Configuration
@EnableAsync
public class PicConfig implements WebMvcConfigurer {
    @Value("${pic.dir}")
    private String picDir;
    @Value("${headimg.dir}")
    private String headimg;

    @Value("${album.dir}")
    private String album;
    @Value("${diaryPic.dir}")
    private String diaryPic;

    @Autowired
    private Environment env;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/**")
                .addResourceLocations("file:" + picDir)
        .addResourceLocations("file:" + headimg)
        .addResourceLocations("file:" + album)
        .addResourceLocations("file:" + diaryPic);
    }


    @Bean
    public SnowflakeShardingKeyGenerator shardingKeyGenerator() {
        SnowflakeShardingKeyGenerator keyGenerator = new SnowflakeShardingKeyGenerator();
        Properties properties = new Properties();
        properties.put("worker.id", env.getProperty("worker.id"));
        properties.put("max.tolerate.time.difference.milliseconds",
                env.getProperty("max.tolerate.time.difference.milliseconds"));
        keyGenerator.setProperties(properties);
        return keyGenerator;
    }
}
