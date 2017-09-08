package uk.gov.hmcts.bar.api.controllers.hello;


import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.bar.api.model.HelloRepository;

import java.util.List;


@RestController
@Validated
public class HelloController {

    private final HelloDtoMapper helloDtoMapper;
    private final HelloRepository helloRepository;

    @Autowired
    public HelloController(HelloDtoMapper helloDtoMapper,HelloRepository helloRepository) {
        this.helloDtoMapper = helloDtoMapper;
        this.helloRepository = helloRepository;
    }


    @ApiOperation(value = "Find  all hellos.",
        notes = "This endpoint returns all helos .", response = List.class)
    @GetMapping("/hello")
    public String getHello() {

        return helloRepository.findByHello("hello").get().getDescription();
    }



}



