package com.pociot.hcb.account.config;

import com.pociot.hcb.account.service.security.CustomUserInfoTokenServices;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
  private final ResourceServerProperties rsp;

  public ResourceServerConfig(
      ResourceServerProperties rsp) {
    this.rsp = rsp;
  }

  @Bean
  @ConfigurationProperties(prefix = "security.oauth2.client")
  public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
    return new ClientCredentialsResourceDetails();
  }

  @Bean
  public RequestInterceptor oauth2FeignRequestInterceptor() {
    return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), clientCredentialsResourceDetails());
  }

  @Bean
  public OAuth2RestTemplate clientCredentialsRestTemplate() {
    return new OAuth2RestTemplate(clientCredentialsResourceDetails());
  }

  @Bean
  public ResourceServerTokenServices tokenServices() {
    return new CustomUserInfoTokenServices(rsp.getUserInfoUri(), rsp.getClientId());
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/", "/demo").permitAll()
        .anyRequest().authenticated();
  }
}
