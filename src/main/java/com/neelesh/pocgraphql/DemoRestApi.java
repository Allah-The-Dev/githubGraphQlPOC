package com.neelesh.pocgraphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping(path="/api/v1")
public class DemoRestApi{

    private static Logger logger = LoggerFactory.getLogger(DemoRestApi.class);

    @Autowired
    private DemoBusinessApi demoBusinessApi;

    @GetMapping(path="/getBranchesAndTags",produces = "application/json")
    public ResponseEntity<Map<String,List<String>>> getBranchesAndTags(@RequestBody GitHubDetails githubDetails){
        Map<String,List<String>> mapOfBranchAndTagList = null;
        try {
            mapOfBranchAndTagList = demoBusinessApi.getGitHubBranchesAndTags(githubDetails);
        } catch (Exception e) {
           logger.info(e.toString());
        }
        if(mapOfBranchAndTagList != null && !mapOfBranchAndTagList.isEmpty()){
            return (ResponseEntity<Map<String,List<String>>>) ResponseEntity.ok().body(mapOfBranchAndTagList);
        }else{
            return (ResponseEntity<Map<String,List<String>>>) ResponseEntity.noContent();
        }
    }

}