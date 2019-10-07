package com.neelesh.pocgraphql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
class DemoBusinessApi{
    private final RestTemplate restTemplate;    

    public DemoBusinessApi(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    public Map<String,List<String>> getGitHubBranches(String token,String repoOwner,String repoName){
        Map<String,List<String>> mapOfBranchAndTag = new HashMap<>();
        //header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","bearer");
        //entity
        //urlbuilder
        //restcall
        //checking status
        //extracting result
        return mapOfBranchAndTag;
    }

}