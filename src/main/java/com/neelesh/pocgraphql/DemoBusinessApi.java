package com.neelesh.pocgraphql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
class DemoBusinessApi {

    private static Logger logger = LoggerFactory.getLogger(DemoBusinessApi.class);

    private final RestTemplate restTemplate;

    public DemoBusinessApi(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Map<String, List<String>> getGitHubBranchesAndTags(GitHubDetails gitHubDetails) {

        // map of list to store 2 list 
        // one for branches and one for tags
        Map<String, List<String>> mapOfBranchAndTag = new HashMap<>();
        mapOfBranchAndTag.put("branches", getRefs(gitHubDetails,"heads"));
        mapOfBranchAndTag.put("tags",getRefs(gitHubDetails,"tags"));
        return mapOfBranchAndTag;
    }

    private List<String> getRefs(GitHubDetails gitHubDetails,String refName) {

        // return object of list of branches
        List<String> listOfRefs = new ArrayList<>();

        // header
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.set("Authorization", "bearer "+gitHubDetails.getAccessToken());

        // body  
        MultiValueMap<String, String> requestBodyMap = new LinkedMultiValueMap<>();
        requestBodyMap.add("query", getQueryForRefs(refName));
        requestBodyMap.add("variables", "{\"url\":\""+gitHubDetails.getRepoUrl()+"\"}");

        // httpentity to hold header and body
        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(requestBodyMap,requestHeader); 
     
        // restcall
        ResponseEntity<String> responseForGetRefs = restTemplate.postForEntity("https://api.github.com/graphql",
                                                                    requestEntity, String.class);

        // checking status
        if(HttpStatus.OK.equals(responseForGetRefs.getStatusCode())){
            logger.info("voila for refs");
        }else{
            logger.info("try again for refs");
        }

        // extracting result
        return listOfRefs;
    }

    private String getQueryForRefs(String refName) {
      String refPrefix = "\"refs/"+refName+"/\"";
      return String.join(
        System.getProperty("line.separator"), 
        "query branches($url:URI!){",
            "resource(url:$url){",
              "... on Repository{",
                "refs(refPrefix:"+refPrefix+",first:100,orderBy:{field:TAG_COMMIT_DATE,direction:DESC}){",
                  "totalCount",
                  "pageInfo{",
                    "hasNextPage",
                    "endCursor",
                  "}",
                  "edges{",
                    "node{",
                      "name",
                    "}",
                  "}",
                "}",
              "}",
            "}",
          "}" 
        );
    }

}