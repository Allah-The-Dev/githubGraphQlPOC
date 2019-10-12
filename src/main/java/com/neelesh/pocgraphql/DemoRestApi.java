package com.neelesh.pocgraphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping(path="/api/v1")
public class DemoRestApi{

    private static Logger logger = LoggerFactory.getLogger(DemoRestApi.class);
    //fdsf

    @Autowired
    private DemoBusinessApi demoBusinessApi;

    @PostMapping(path="/getBranchesAndTags",produces = "application/json")
    public ResponseEntity<Map<String,List<String>>> getBranchesAndTags(@RequestBody GitHubDetails githubDetails){
        Map<String,List<String>> mapOfBranchAndTagList = null;
        try {
            mapOfBranchAndTagList = demoBusinessApi.getGitHubBranchesAndTags(githubDetails);
        } catch (Exception e) {
           logger.info(e.toString());
        }
        if(mapOfBranchAndTagList != null && !mapOfBranchAndTagList.isEmpty()){
            return (ResponseEntity<Map<String,List<String>>>) ResponseEntity
                                                                .ok()
                                                                .body(mapOfBranchAndTagList);
        }else{
            return  (ResponseEntity<Map<String, List<String>>>) ResponseEntity
                                                                    .status(HttpStatus.NO_CONTENT)
                                                                    .body(mapOfBranchAndTagList);
        }
    }

    @PostMapping(path="/getDevDetails",produces = "application/json")
    public ResponseEntity<Map<String,Map<String,String>>> getDevDetails(@RequestBody List<GitHubDetails> githubDetailsList){
        Map<String,Map<String,String>> githubDevDetailsList = null;
        try {
            githubDevDetailsList = demoBusinessApi.getGitHubDevDetails(githubDetailsList);
        } catch (Exception e) {
           logger.info(e.toString());
        }
        if(githubDevDetailsList != null && !githubDevDetailsList.isEmpty()){
            return (ResponseEntity<Map<String,Map<String,String>>>) ResponseEntity
                                                                .ok()
                                                                .body(githubDevDetailsList);
        }else{
            return  (ResponseEntity<Map<String,Map<String,String>>>) ResponseEntity
                                                                    .status(HttpStatus.NO_CONTENT)
                                                                    .body(githubDevDetailsList);
        }
    }

}