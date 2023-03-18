package com.swd.backend.api.v1.yard.location;

import com.google.gson.Gson;
import com.swd.backend.entity.DistrictEntity;
import com.swd.backend.entity.ProvinceEntity;
import com.swd.backend.service.DistrictService;
import com.swd.backend.service.ProvinceService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1")
public class LocationRestAPI {
    private DistrictService districtService;
    private ProvinceService provinceService;
    private Gson gson;

    @GetMapping(value = "provinces")
    public ResponseEntity<String> getAllProvince() {
        try {
            List<ProvinceEntity> listProvince = provinceService.getAllProvince();
            return ResponseEntity.ok().body(gson.toJson(listProvince));
        } catch (DataAccessException dataAccessException) {
            return ResponseEntity.internalServerError().body(dataAccessException.getMessage());
        }
    }

    @GetMapping(value = "districts")
    public ResponseEntity<String> getAllDistrict() {
        try {
            List<DistrictEntity> listDistrict = districtService.getAllDistrict();
            return ResponseEntity.ok(gson.toJson(listDistrict));
        } catch (DataAccessException accessException) {
            return ResponseEntity.internalServerError().body(accessException.getMessage());
        }
    }

    @GetMapping(value = "provinces/{provinceId}/districts")
    public ResponseEntity<String> getAllDistrictByProvinceId(@PathVariable String provinceId) {
        try {
            int id = Integer.parseInt(provinceId);
            List<DistrictEntity> listDistrict = districtService.getAllDistrictByProvinceId(id);
            return ResponseEntity.ok(gson.toJson(listDistrict));
        } catch (NumberFormatException numberFormatException) {
            return ResponseEntity.badRequest().body("Province Id invalid.");
        } catch (DataAccessException accessException) {
            return ResponseEntity.internalServerError().body(accessException.getMessage());
        }
    }
}
