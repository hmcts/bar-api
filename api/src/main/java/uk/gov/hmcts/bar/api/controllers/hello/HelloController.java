package uk.gov.hmcts.bar.api.controllers.hello;


import java.util.List;
import javax.validation.Valid;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import static java.util.stream.Collectors.toList;

import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@RestController
@Validated
public class HelloController {



    @GetMapping("/hello")
    public String getCategories() {
        return "Hello Not from DB";
    }

 
}
