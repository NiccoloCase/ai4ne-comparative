package org.caselli.comparativecognitiveworkflow.controller;

import org.caselli.comparativecognitiveworkflow.services.AI4NeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai4ne")
public class AI4NeController {
    private final AI4NeService ai4NeService;

    public AI4NeController(AI4NeService ai4NeService) {
        this.ai4NeService = ai4NeService;
    }

    @PostMapping("/simple_llm")
    public ResponseEntity<String> routeWithSimpleLLM(@RequestBody RequestPayload request) {

        Object res = this.ai4NeService.performRoutingWithSimpleLLM(request.request);
        return ResponseEntity.ok(res.toString());
    }


    @PostMapping("/function_calling")
    public ResponseEntity<String> routeWithFunctionCalling(@RequestBody RequestPayload request) {
        Object res = this.ai4NeService.performRoutingWithFunctionCallingLLM(request.request);
        return ResponseEntity.ok(res.toString());
    }

    public static class RequestPayload {
        private String request;
        public String getRequest() {
            return request;
        }
        public void setRequest(String request) {
            this.request = request;
        }
    }
}
