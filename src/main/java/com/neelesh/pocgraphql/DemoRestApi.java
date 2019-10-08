package com.neelesh.pocgraphql;

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

    @Autowired
    private DemoBusinessApi demoBusinessApi;

    @GetMapping(path="/getBranchesAndTags",produces = "application/json")
    public ResponseEntity<Map<String,List<String>>> getBranchesAndTags(@RequestBody GitHubDetails githubDetails){
        Map<String,List<String>> mapOFBranchAndTagList = demoBusinessApi.getGitHubBranchesAndTags(githubDetails);
        if(!mapOFBranchAndTagList.isEmpty()){
            return (ResponseEntity<Map<String,List<String>>>) ResponseEntity.ok().body(mapOFBranchAndTagList);
        }else{
            return (ResponseEntity<Map<String,List<String>>>) ResponseEntity.noContent();
        }
    }

}