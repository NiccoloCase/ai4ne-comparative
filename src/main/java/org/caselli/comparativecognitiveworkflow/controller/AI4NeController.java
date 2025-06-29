package org.caselli.comparativecognitiveworkflow.controller;

import lombok.Data;
import org.caselli.comparativecognitiveworkflow.services.AI4NeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai4ne")
public class AI4NeController {
    private final AI4NeService ai4NeService;

    public AI4NeController(AI4NeService ai4NeService) {
        this.ai4NeService = ai4NeService;
    }

    @PostMapping("/simple_llm")
    public Object routeWithSimpleLLM(@RequestBody RequestPayload request) {
        return this.ai4NeService.performRoutingWithSimpleLLM(request.request);
    }

    @PostMapping("/reasoning_llm")
    public Object routeWithReasoningLLM(@RequestBody RequestPayload request) {
        return this.ai4NeService.performRoutingWithReasoningLLM(request.request);
    }


    @PostMapping(value = "/function_calling",  produces = MediaType.APPLICATION_JSON_VALUE)
    public Object routeWithFunctionCalling(@RequestBody RequestPayload request) {
        return this.ai4NeService.performRoutingWithFunctionCallingLLM(request.request);
    }

    @Data
    public static class RequestPayload {
        private String request;

    }
}
