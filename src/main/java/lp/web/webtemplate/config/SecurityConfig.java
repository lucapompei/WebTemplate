package lp.web.webtemplate.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lp.web.webtemplate.controller.Endpoints;

/**
 * This config class globally configures the spring security module, so it
 * requires authentication to every URL in the application
 * 
 * @author lucapompei
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements Filter {

	/**
	 * This variable allows to enable/disable the spring security module
	 */
	@Value("${security_enabled:true}")
	private boolean securityEnabled;

	/**
	 * This variable allows to enable/disable the security controls to api endpoints
	 */
	@Value("${security_api_enabled:false}")
	private boolean securityApiEnabled;

	@Autowired
	private DataSource dataSource;

	/**
	 * It configures the authentication manager used to authenticate the http
	 * requests
	 * 
	 * @param auth,
	 *            the authentication manager builder
	 * @throws Exception,
	 *             if something goes wrong
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select username, password, enabled from users where username=?")
				.authoritiesByUsernameQuery(
						"select r.id_user, r.role from user_roles r join users u on r.id_user=u.id where u.username=?");
	}

	/**
	 * Configure the password encoder
	 * 
	 * @return the configured password encoder
	 */
	@Bean
	public static PasswordEncoder configurePasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * This method configures the security, enabling or disabling it and permitting
	 * or denying the http requests
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if (this.securityEnabled) {
			// disable CSRF
			// http.csrf().disable();
			if (!this.securityApiEnabled) {
				// permit all api endpoints
				http.authorizeRequests().antMatchers(Endpoints.API_BASE + "/**").permitAll();
			}
			// permit login and logout, require authentication for each other endpoint
			http.authorizeRequests().anyRequest().authenticated().and().httpBasic().and().httpBasic().and().formLogin()
					.loginPage(Endpoints.LOGIN).permitAll().and().logout().logoutUrl(Endpoints.LOGOUT)
					.logoutSuccessUrl(Endpoints.LOGIN).permitAll();
		} else {
			// permit all
			http.authorizeRequests().anyRequest().permitAll();
		}
	}

	/**
	 * Configure filters to allow/deny incoming requests
	 * 
	 * @param req,
	 *            the incoming request
	 * @param res,
	 *            the response to serve
	 * @param chain,
	 *            the filter chain
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, X-Requested-With");
		chain.doFilter(req, res);
	}

}
