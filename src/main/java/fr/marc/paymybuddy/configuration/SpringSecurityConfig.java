package fr.marc.paymybuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(AuthenticationManagerBuilder  auth) throws Exception {
		auth.inMemoryAuthentication()
			//.withUser("PMBuser")
			//.password(passwordEncoder().encode("user123"))
			//.roles("USER")
			//.and()
			//.withUser("PMBadmin")
			//.password(passwordEncoder().encode("admin123"))
			//.roles("ADMIN","USER")
			//.and()
			.withUser("acall@mail.fr")
			.password(passwordEncoder().encode("Excalibur"))
			.roles("USER");
		
	}
	
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		
	
		http
			.csrf().disable()
			.authorizeRequests()
				//.antMatchers("/home**").permitAll()
				.antMatchers("/static/**").permitAll()
				//.antMatchers("/admin").hasRole("ADMIN")
				//.antMatchers("/").permitAll()
				//.antMatchers("/**").permitAll()
				//.antMatchers("/loginRequest").permitAll()
				.antMatchers("/login").permitAll()
				
				.anyRequest().authenticated()
			.and()
			.formLogin()
				//.loginPage("/login?message=")
				.loginPage("/login")
				.defaultSuccessUrl("/home?id=1",true)
				.permitAll()
			.and()
			.logout()
	            .invalidateHttpSession(true)
	            .clearAuthentication(true)
	            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	            .permitAll()
				;
		
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
