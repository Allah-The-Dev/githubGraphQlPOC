package com.neelesh.pocgraphql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
class DemoBusinessApi {
    private final RestTemplate restTemplate;

    public DemoBusinessApi(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Map<String, List<String>> getGitHubBranchesAndTags(GitHubDetails gitHubDetails) {
        Map<String, List<String>> mapOfBranchAndTag = new HashMap<>();
        mapOfBranchAndTag.put("branch", getBranches(gitHubDetails));

        return mapOfBranchAndTag;
    }

    private List<String> getBranches(GitHubDetails gitHubDetails) {

        // return object of list of branches
        List<String> listOfBranches = new ArrayList<>();

        // header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "bearer "+gitHubDetails.getAccessToken());

        // body
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("query", getQueryForBranch(gitHubDetails));
        map.add("variable", "{\"url\":"+gitHubDetails.getRepoUrl().replace(".git", "")+"}");

        // httpentity to hold header and body
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(map,headers); 
     
        // restcall
        ResponseEntity<String> responseForGetBranch = restTemplate.postForEntity("https://api.github.com/graphql",
                             entity, String.class);

        // checking status
        

        // extracting result
        return null;
    }

    private String getQueryForBranch(GitHubDetails gitHubDetails) {
        return String.join(
            System.getProperty("line.separator"), 
            "listRepos($url:URI!){",
                "resource(url:$url){",
                  "... on Repository{",
                    "refs(refPrefix:\"refs/heads/\",first:100,orderBy:{field:TAG_COMMIT_DATE,direction:DESC}){",
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
                "}"
            );
    }

}