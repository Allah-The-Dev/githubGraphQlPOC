package com.neelesh.pocgraphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/v1")
public class DemoRestApi{

    @Autowired
    private DemoBusinessApi demoBusinessApi;

    @GetMapping(path="/getBranchesAndTags",produces = "application/json")
    public String getBranchesAndTags(@RequestBody GitHubDetails githubDetails){
        
        return "Hello ";
    }

}