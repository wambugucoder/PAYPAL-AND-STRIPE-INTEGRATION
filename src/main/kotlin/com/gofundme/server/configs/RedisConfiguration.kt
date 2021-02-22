package com.gofundme.server.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.connection.RedisStandaloneConfiguration







@Configuration
class RedisConfiguration {

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory? {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration("localhost", 6379)
        return JedisConnectionFactory(redisStandaloneConfiguration)
    }
}