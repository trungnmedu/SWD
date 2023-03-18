package com.swd.backend.api.v1.sub_yard.get;

import com.google.gson.Gson;
import com.swd.backend.model.SubYardModel;
import com.swd.backend.model.YardData;
import com.swd.backend.model.YardModel;
import com.swd.backend.service.SubYardService;
import com.swd.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/yards")
public class GetYardDetailForUserApi {
    private SubYardService subYardService;
    private YardService yardService;
    private Gson gson;

    @GetMapping(value = "{yardId}")
    public ResponseEntity<String> getSubYardByBigYard(@PathVariable String yardId) {
        if (!yardService.isAvailableYard(yardId)) {
            return ResponseEntity.ok().body(gson.toJson(new YardDetailForUserResponse("The yard is not active or deleted.", null)));
        }

        List<SubYardModel> subYards = subYardService.getActiveSubYardsByBigYard(yardId);
        YardModel bigYard = yardService.getYardModelFromYardId(yardId);
        YardData data = new YardData(bigYard, subYards);

        YardDetailForUserResponse response = new YardDetailForUserResponse("Get successful", data);

        return ResponseEntity.ok().body(gson.toJson(response));
    }
}
