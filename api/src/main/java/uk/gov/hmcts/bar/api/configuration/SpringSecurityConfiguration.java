package uk.gov.hmcts.bar.api.configuration;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*Uncomment - - when integrating with Idam
import uk.gov.hmcts.auth.checker.spring.useronly.AuthCheckerUserOnlyFilter;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.beans.factory.annotation.Autowired;*/

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    /*Uncomment - - when integrating with Idam
     @Autowired
    private AuthCheckerUserOnlyFilter authCheckerFilter;

    */

    @Override
    @SuppressFBWarnings(value = "SPRING_CSRF_PROTECTION_DISABLED", justification = "It's safe to disable CSRF protection as application is not being hit directly from the browser")
    protected void configure(HttpSecurity http) throws Exception {

     /*  Uncomment - - when integrating with Idam
     authCheckerFilter.setAuthenticationManager(authenticationManager());

       http
            .addFilter(authCheckerFilter)
            .sessionManagement().sessionCreationPolicy(STATELESS).and()
            .csrf().disable()
            .formLogin().disable()
            .logout().disable()
            .authorizeRequests()
            .antMatchers("/swagger-ui.html", "/webjars/springfox-swagger-ui/**", "/swagger-resources/**", "/v2/**", "/health", "/info").permitAll()
            .requestMatchers((r) -> r.getMethod().equals("GET")).permitAll()
            .anyRequest().authenticated();
      */

        //Remove the following line - - when integrating with Idam and uncomment the above
        http.authorizeRequests().antMatchers("/").permitAll().and().csrf().disable();

    }

}
