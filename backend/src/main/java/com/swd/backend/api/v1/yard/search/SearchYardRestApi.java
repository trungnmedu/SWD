package com.swd.backend.api.v1.yard.search;


import com.google.gson.Gson;
import com.swd.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/yards")
public class SearchYardRestApi {

    private YardService yardService;
    private Gson gson;

    @PostMapping(value = "search")
    public ResponseEntity<String> searchYardByLocation(@RequestBody(required = false) SearchYardRequest searchYardRequest) {
        if (searchYardRequest == null) {
            YardResponse yardResponse = yardService.findYardByFilter(null, null, null, null);
            return ResponseEntity.ok().body(gson.toJson(yardResponse));
        }

        YardResponse yardResponse = yardService.findYardByFilter(searchYardRequest.getProvinceId(), searchYardRequest.getDistrictId(), searchYardRequest.getItemsPerPage(), searchYardRequest.getPage());
        return ResponseEntity.ok().body(gson.toJson(yardResponse));
    }
}
