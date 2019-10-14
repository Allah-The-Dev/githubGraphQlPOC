package com.neelesh.pocgraphql;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    mapOfBranchAndTag.put("branches", getRefs(gitHubDetails, "heads"));
    mapOfBranchAndTag.put("tags", getRefs(gitHubDetails, "tags"));
    return mapOfBranchAndTag;
  }

  private List<String> getRefs(GitHubDetails gitHubDetails, String refName) {

    // return object of list of branches
    List<String> listOfRefs = new ArrayList<>();

    // header
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.set("Authorization", "bearer " + gitHubDetails.getAccessToken());

    // body
    // MultivalueMap didn't work here,
    JSONObject requestBodyJSON = new JSONObject();
    requestBodyJSON.put("query", getQueryForRefs(refName));
    requestBodyJSON.put("variables", "{url:\"" + gitHubDetails.getRepoUrl() + "\"}");
    // requestBodyMap.add("variables", "{url:\""+gitHubDetails.getRepoUrl()+"\"}");

    // httpentity to hold header and body
    HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJSON.toString(), requestHeader);

    // restcall
    ResponseEntity<String> responseForGetRefs = restTemplate.exchange("https://api.github.com/graphql", 
                                                                      HttpMethod.POST,
                                                                      requestEntity, 
                                                                      String.class);

    // checking status
    if (HttpStatus.OK.equals(responseForGetRefs.getStatusCode())) {
      logger.info("voila for refs");
    } else {
      logger.info("try again for refs");
    }

    // extracting result
    return listOfRefs;
  }

  private String getQueryForRefs(String refName) {
    String refPrefix = "\"refs/"+refName+"/\"";
    return String.join(
      System.getProperty("line.separator"), 
      "query($repoURL:URI!) {",
          "resource(url:$repoURL){",
            "... on Repository{",
              "refs(refPrefix:"+refPrefix+",first:100,orderBy:{field:TAG_COMMIT_DATE,direction:DESC}){",
                "totalCount",
                "pageInfo{",
                  "hasNext  Page",
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


  public Map<String, Map<String, String>> getGitHubDevDetails(List<GitHubDetails> githubDetailsList, String since, String until) {
    Map<String, Map<String, String>> devDetailsMap = new HashMap<>();
    String sinceInISO8601 = since+"T00:00:00+05:30";
    String untilInISO8601 = until+"T24:00:00+05:30";
    githubDetailsList.forEach(singleGithubDetail -> {
      getDetailsForSingleRepo(singleGithubDetail,sinceInISO8601,untilInISO8601);
    });
    return devDetailsMap;
  }

  private void getDetailsForSingleRepo(GitHubDetails singleGithubDetail, String since, String until) {
    // request header
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.set("Authorization", "bearer " + singleGithubDetail.getAccessToken());
    // request body
    JSONObject requestBodyJSON = new JSONObject();
    requestBodyJSON.put("query", getQueryForCommitDetails());
    requestBodyJSON.put("variables",getVariablesForCommitQuery(singleGithubDetail,since,until));
    // request entity
    HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJSON.toString(), requestHeader);
    // http post call
    ResponseEntity<JSONObject> responseEntity = restTemplate.exchange("https://api.github.com/graphql", 
                                                                      HttpMethod.POST,
                                                                      requestEntity, 
                                                                      JSONObject.class);
    // checking status
    if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
      logger.info("voila");
    }
  }

  private String getVariablesForCommitQuery(GitHubDetails singleGithubDetail, String since, String until) {
    JSONObject variableJSON = new JSONObject();
    variableJSON.put("url", singleGithubDetail.getRepoUrl());
    variableJSON.put("since", since);
    variableJSON.put("until", until);
    return variableJSON.toString();
  }

  private String getQueryForCommitDetails() {
    List<String> listOfQueryLines = null;
    try {
      listOfQueryLines = (List<String>) Files.readAllLines(Paths.get("src/main/resources/QueryForCommitHistory.txt"),StandardCharsets.UTF_8)
                                              .stream()
                                              .map(line -> line.trim())
                                              .collect(Collectors.toList());
    } catch (IOException e) {
      logger.info(e.toString());
    }
    if(listOfQueryLines !=null && !listOfQueryLines.isEmpty()){
      return String.join(
        System.getProperty("line.separator"),
        listOfQueryLines
      );
    }else{
      return "NA";
    }
  }

}