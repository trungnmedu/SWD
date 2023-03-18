package com.swd.backend.service;

import com.swd.backend.entity.YardEntity;
import com.swd.backend.model.SlotStatistic;
import com.swd.backend.model.YardStatisticModel;
import com.swd.backend.myrepository.DashboardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@AllArgsConstructor
public class DashboardService {
    public static final String[] TIME_SET = new String[]{
            "00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00",
            "06:30", "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
            "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00",
            "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"
    };

    private YardService yardService;
    private DashboardRepository dashboardRepository;

    public List<YardStatisticModel> processGetAllInformationOfYardStatisticModel(String ownerId, Timestamp startDate, Timestamp endDate) {
        List<YardStatisticModel> resultList = initializeYardStatisticModelsOfOwner(ownerId);
        resultList = updateNumberOfBookingStatisticForModels(resultList, ownerId, startDate, endDate);
        resultList = updateNumberOfBookingCanceledStatisticForModels(resultList, ownerId, startDate, endDate);
        resultList = updateNumberOfBookingPlayedStatisticForModels(resultList, ownerId, startDate, endDate);
        resultList = updateBusinessContributionStatisticForModels(resultList, ownerId, startDate, endDate);

        return resultList;
    }

    public List<YardStatisticModel> initializeYardStatisticModelsOfOwner(String ownerId) {
        List<YardEntity> yardEntities = yardService.getAllYardEntityOfOwner(ownerId);
        List<YardStatisticModel> yardStatisticModelsInitialization = new ArrayList<>();
        for (YardEntity yard : yardEntities) {
            YardStatisticModel yardStatisticModel = YardStatisticModel.builder().yardId(yard.getId())
                    .yardName(yard.getName())
                    .build();
            yardStatisticModelsInitialization.add(yardStatisticModel);
        }
        return yardStatisticModelsInitialization;
    }

    public List<YardStatisticModel> updateNumberOfBookingStatisticForModels(List<YardStatisticModel> preList,
                                                                            String ownerId,
                                                                            Timestamp startDate,
                                                                            Timestamp endDate) {
        List<YardStatisticModel> yardsContainNumberOfBookingStatistic = dashboardRepository.getNumberOfYardBookingsForOwner(ownerId, startDate, endDate);

        //Với mỗi thông số trong list mới, tìm trong list cũ để update thông số
        for (YardStatisticModel newModel : yardsContainNumberOfBookingStatistic) {
            for (YardStatisticModel preModel : preList) {
                if (newModel.getYardId().equals(preModel.getYardId())) {
                    preModel.setNumberOfBookings(newModel.getNumberOfBookings());
                    break;
                }
            }
        }

        return preList;
    }

    public List<YardStatisticModel> updateNumberOfBookingCanceledStatisticForModels(List<YardStatisticModel> preList,
                                                                                    String ownerId,
                                                                                    Timestamp startDate,
                                                                                    Timestamp endDate) {
        List<YardStatisticModel> yardsContainNumberOfBookingCanceledStatistic = dashboardRepository.getNumberOfYardBookingCanceledForOwner(ownerId, startDate, endDate);

        //Với mỗi thông số trong list mới, tìm trong list cũ để update thông số
        for (YardStatisticModel newModel : yardsContainNumberOfBookingCanceledStatistic) {
            for (YardStatisticModel preModel : preList) {
                if (newModel.getYardId().equals(preModel.getYardId())) {
                    preModel.setNumberOfBookingCanceled(newModel.getNumberOfBookingCanceled());
                    break;
                }
            }
        }

        return preList;
    }

    public List<YardStatisticModel> updateNumberOfBookingPlayedStatisticForModels(List<YardStatisticModel> preList,
                                                                                  String ownerId,
                                                                                  Timestamp startDate,
                                                                                  Timestamp endDate) {
        List<YardStatisticModel> yardsContainNumberOfBookingPlayedStatistic = dashboardRepository.getNumberOfYardBookingPlayedForOwner(ownerId, startDate, endDate);

        //Với mỗi thông số trong list mới, tìm trong list cũ để update thông số
        for (YardStatisticModel newModel : yardsContainNumberOfBookingPlayedStatistic) {
            for (YardStatisticModel preModel : preList) {
                if (newModel.getYardId().equals(preModel.getYardId())) {
                    preModel.setNumberOfBookingPlayed(newModel.getNumberOfBookingPlayed());
                    break;
                }
            }
        }

        return preList;
    }

    public List<YardStatisticModel> updateBusinessContributionStatisticForModels(List<YardStatisticModel> preList,
                                                                                 String ownerId,
                                                                                 Timestamp startDate,
                                                                                 Timestamp endDate) {
        List<YardStatisticModel> yardsContainBusinessContributionStatistic = dashboardRepository.getYardBookingTotalIncomeForOwner(ownerId, startDate, endDate);

        long totalIncome = 0;
        for (YardStatisticModel newModel : yardsContainBusinessContributionStatistic) {
            totalIncome += newModel.getTotalIncome();
        }

        //Với mỗi thông số trong list mới, tìm trong list cũ để update thông số
        for (YardStatisticModel newModel : yardsContainBusinessContributionStatistic) {
            for (YardStatisticModel preModel : preList) {
                if (newModel.getYardId().equals(preModel.getYardId())) {
                    double percentage = ((double) newModel.getTotalIncome()) / totalIncome * 100;
                    preModel.setBusinessContributionPercentage((int) Math.round(percentage));
                    preModel.setTotalIncome(newModel.getTotalIncome());
                    break;
                }
            }
        }

        return preList;
    }


    public Map getBookingByTimeStatistic(String ownerId, Timestamp startDate, Timestamp endDate) {
        List<SlotStatistic> slots = dashboardRepository.getNumberOfBookingInSlotForOwner(ownerId, startDate, endDate);
        Map<String, Long> bookingInSlotStatisticsByTimeSet = new TreeMap<>();
        for (String time : TIME_SET) {
            bookingInSlotStatisticsByTimeSet.put(time, Long.valueOf(0));
        }

        for (SlotStatistic slot : slots) {
            for (String time : TIME_SET) {
                if (time.compareTo(slot.getStartTime()) >= 0 && time.compareTo(slot.getEndTime()) <= 0) {
                    Long currentStatistic = bookingInSlotStatisticsByTimeSet.get(time);
                    currentStatistic += slot.getNumberOfBooking();
                    bookingInSlotStatisticsByTimeSet.put(time, currentStatistic);
                }
            }
        }

        return bookingInSlotStatisticsByTimeSet;
    }
}
