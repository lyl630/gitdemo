package cn.pro.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

@Configuration
//开启认证服务
@EnableAuthorizationServer
public class AuthServerConfig  extends AuthorizationServerConfigurerAdapter {

    @Autowired
    public DataSource dataSource;

    @Bean
    public TokenStore tokenStore(){
        //把sccess——token保存到数据库，也可以保存到内存，或者是redis
        return new JdbcTokenStore(dataSource);
    }
    @Bean
    public ClientDetailsService jdbcClientDetailsServer(){
//从数据库中的第三方应用信息表查询对应的记录，如果不是已经登录在数据中应用，不允许使用oauth授权登录

        return  new JdbcClientDetailsService(dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer client)throws Exception{
       //从数据库中的第三方应用信息表查询对应的记录，如果不是已经登录在数据中应用，不允许使用oauth授权登录
        client.withClientDetails(jdbcClientDetailsServer());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints){

        //把acess_token保存到数据库，也可以保存到内存，或者是redis
        endpoints.tokenStore(tokenStore());
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST);
    }

public void configure(AuthorizationServerSecurityConfigurer security) throws Exception{
//允许第三方应用通过表单传递client_id,client_secret来登录
        security.allowFormAuthenticationForClients();
}

}
