package fr.marc.paymybuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(AuthenticationManagerBuilder  auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("PMBuser")
				.password(passwordEncoder().encode("user123"))
				.roles("USER")
			.and()
			.withUser("PMBadmin")
				.password(passwordEncoder().encode("admin123"))
				.roles("ADMIN","USER");
	}
	
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		http.csrf().disable().authorizeRequests()
			.antMatchers("/loginRequest").permitAll()
			.antMatchers("/admin").hasRole("ADMIN")
			.antMatchers("/").hasRole("USER")
			.anyRequest().authenticated()
			.and()
			.formLogin();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}