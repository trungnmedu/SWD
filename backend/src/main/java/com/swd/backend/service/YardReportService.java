package com.swd.backend.service;

import com.swd.backend.constance.YardReportStatus;
import com.swd.backend.entity.YardReportEntity;
import com.swd.backend.model.FilterModel;
import com.swd.backend.model.SearchModel;
import com.swd.backend.model.YardReportModel;
import com.swd.backend.myrepository.YardReportCustomRepository;
import com.swd.backend.repository.YardReportRepository;
import com.swd.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
public class YardReportService {
    private YardReportRepository yardReportRepository;
    private YardReportCustomRepository yardReportCustomRepository;

    public static final String[] SEARCH_KEYWORDS = new String[]{"REFERENCE", "YARDNAME", "CREATEDBY"};
    public static final String[] SORT_KEYWORDS = new String[]{"REFERENCE", "YARDNAME", "USERNAME", "CREATEDAT"};
    public static final String[] FILTER_FIELDS = new String[]{"STATUS"};

    public void reportYard(String userId, String yardId, String reason) {
        YardReportEntity yardReportEntity = YardReportEntity.builder().yardId(yardId)
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .reason(reason)
                .createdAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .updatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .status(YardReportStatus.REPORT_PENDING)
                .build();
        yardReportRepository.save(yardReportEntity);
    }

    public void maskAsResolvedReport(String reportId) {
        YardReportEntity yardReportEntity = yardReportRepository.findYardReportEntityById(reportId);
        yardReportEntity.setStatus(YardReportStatus.REPORT_HANDLED);
        yardReportEntity.setUpdatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
        yardReportRepository.save(yardReportEntity);
    }

    public void rejectReport(String reportId) {
        YardReportEntity yardReportEntity = yardReportRepository.findYardReportEntityById(reportId);
        yardReportEntity.setStatus(YardReportStatus.REPORT_REJECTED);
        yardReportEntity.setUpdatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
        yardReportRepository.save(yardReportEntity);
    }

    public List<YardReportModel> getAllReports(SearchModel searchModel) {
        validateSearchModel(searchModel);
        List<YardReportModel> yardReports = yardReportCustomRepository.getAllYardReportModels();

        if (searchModel != null && searchModel.getFilter() != null) {
            yardReports = applyFilter(yardReports, searchModel.getFilter());
        }
        if (searchModel != null && searchModel.getKeyword() != null) {
            yardReports = applySearch(yardReports, searchModel.getKeyword());
        }
        if (searchModel != null && searchModel.getSort() != null) {
            applySort(yardReports, searchModel.getSort());
        }

        return yardReports;
    }

    private void validateSearchModel(SearchModel searchModel) {
        if (searchModel.getFilter() != null && !isValidFilterRequest(searchModel.getFilter())) {
            throw new RuntimeException("Filter request is not valid");
        }
        if (searchModel.getSort() != null && !isValidSortRequest(searchModel.getSort())) {
            throw new RuntimeException("Sort request is not valid");
        }
    }

    private boolean isValidSortRequest(String sortRequest) {
        for (String sortKeyword : SORT_KEYWORDS) {
            if ((sortRequest.charAt(0) + sortRequest.substring(1).toUpperCase()).equals("+" + sortKeyword) || sortRequest.toUpperCase().equals("-" + sortKeyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidFilterRequest(FilterModel filterRequest) {
        for (String filterKeyword : FILTER_FIELDS) {
            if (filterRequest.getField().toUpperCase().equals(filterKeyword)) {
                switch (filterKeyword) {
                    case "STATUS":
                        for (String status : YardReportStatus.getAllStatus()) {
                            if (filterRequest.getValue().toUpperCase().equals(status)) {
                                return true;
                            }
                        }
                        break;
                }
            }
        }
        return false;
    }

    private List<YardReportModel> applySearch(List<YardReportModel> originalData, String keyword) {
        List<YardReportModel> result = new ArrayList<>();
        for (YardReportModel yardReport : originalData) {
            if (yardReport.getReference().toString().contains(keyword) || yardReport.getYardName().toUpperCase().contains(keyword.toUpperCase()) || yardReport.getUserName().toUpperCase().contains(keyword.toUpperCase())) {
                result.add(yardReport);
            }
        }
        return result;
    }

    private List<YardReportModel> applyFilter(List<YardReportModel> originalData, FilterModel filterModel) {
        List<YardReportModel> result = new ArrayList<>();
        switch (filterModel.getField().toUpperCase()) {
            case "STATUS":
                return filterByStatus(originalData, filterModel.getValue());
            default:
                throw new RuntimeException("Field not supported");
        }
    }

    private List<YardReportModel> filterByStatus(List<YardReportModel> originalData, String keyword) {
        List<YardReportModel> result = new ArrayList<>();
        for (YardReportModel yardReport : originalData) {
            if (yardReport.getStatus().toUpperCase().equals(keyword)) {
                result.add(yardReport);
            }
        }
        return result;
    }

    private void applySort(List<YardReportModel> originalData, String sortKeyword) {
        switch (sortKeyword.toUpperCase()) {
            case "+REFERENCE":
                Collections.sort(originalData, new Comparator<YardReportModel>() {
                    @Override
                    public int compare(YardReportModel y1, YardReportModel y2) {
                        return y1.getReference() - y2.getReference();
                    }
                });
                break;
            case "-REFERENCE":
                Collections.sort(originalData, new Comparator<YardReportModel>() {
                    @Override
                    public int compare(YardReportModel y1, YardReportModel y2) {
                        return y2.getReference() - y1.getReference();
                    }
                });
                break;
            case "+YARDNAME":
                Collections.sort(originalData, new Comparator<YardReportModel>() {
                    @Override
                    public int compare(YardReportModel y1, YardReportModel y2) {
                        return y1.getYardName().toUpperCase().compareTo(y2.getYardName().toUpperCase());
                    }
                });
                break;
            case "-YARDNAME":
                Collections.sort(originalData, new Comparator<YardReportModel>() {
                    @Override
                    public int compare(YardReportModel y1, YardReportModel y2) {
                        return y2.getYardName().toUpperCase().compareTo(y1.getYardName().toUpperCase());
                    }
                });
                break;
            case "+USERNAME":
                Collections.sort(originalData, new Comparator<YardReportModel>() {
                    @Override
                    public int compare(YardReportModel y1, YardReportModel y2) {
                        return y1.getUserName().toUpperCase().compareTo(y2.getUserName().toUpperCase());
                    }
                });
                break;
            case "-USERNAME":
                Collections.sort(originalData, new Comparator<YardReportModel>() {
                    @Override
                    public int compare(YardReportModel y1, YardReportModel y2) {
                        return y2.getUserName().toUpperCase().compareTo(y1.getUserName().toUpperCase());
                    }
                });
                break;
            case "+CREATEDAT":
                Collections.sort(originalData, new Comparator<YardReportModel>() {
                    @Override
                    public int compare(YardReportModel yard, YardReportModel comparedYard) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(yard.getCreatedAt()));
                        Timestamp yardCreatedAt = Timestamp.valueOf(localDateTime);
                        localDateTime = LocalDateTime.from(formatter.parse(comparedYard.getCreatedAt()));
                        Timestamp comparedYardCreatedAt = Timestamp.valueOf(localDateTime);
                        return yardCreatedAt.compareTo(comparedYardCreatedAt);
                    }
                });
                break;
            case "-CREATEDAT":
                Collections.sort(originalData, new Comparator<YardReportModel>() {
                    @Override
                    public int compare(YardReportModel yard, YardReportModel comparedYard) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(yard.getCreatedAt()));
                        Timestamp yardCreatedAt = Timestamp.valueOf(localDateTime);
                        localDateTime = LocalDateTime.from(formatter.parse(comparedYard.getCreatedAt()));
                        Timestamp comparedYardCreatedAt = Timestamp.valueOf(localDateTime);
                        return comparedYardCreatedAt.compareTo(yardCreatedAt);
                    }
                });
                break;
        }
    }
}
