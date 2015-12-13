package com.barley.database

import javax.sql.DataSource

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration, Primary}

@Configuration
class DatabaseConfig {


  @ConfigurationProperties(prefix = "spring.datasource")
  @Bean
  @Primary
  def datasource(): DataSource = {
    DataSourceBuilder.create().build()
  }

}
