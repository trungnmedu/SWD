package com.swd.backend.service;

import com.swd.backend.entity.BookingEntity;
import com.swd.backend.entity.SlotEntity;
import com.swd.backend.entity.SubYardEntity;
import com.swd.backend.model.MatchModel;
import com.swd.backend.model.YardModel;
import com.swd.backend.myrepository.SubYardCustomRepository;
import com.swd.backend.repository.SlotRepository;
import com.swd.backend.repository.SubYardRepository;
import com.swd.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {
    private SlotRepository slotRepository;
    private YardService yardService;
    private SubYardCustomRepository subYardCustomRepository;
    private SubYardRepository subYardRepository;

    public MatchModel transformMatchModelFromBookingEntity(BookingEntity bookingEntity) {
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Timestamp bookingDate = bookingEntity.getDate();
        LocalDate date = LocalDate.ofInstant(bookingDate.toInstant(), ZoneId.of(DateHelper.VIETNAM_ZONE));

        int slotId = bookingEntity.getSlotId();
        String subYardId = bookingEntity.getSubYardId();
        String typeYard = subYardCustomRepository.findTypeYardFromSubYardId(subYardId);
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        String yardId = bookingEntity.getBigYardId();
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        YardModel yardModel = yardService.getYardModelFromYardId(yardId);
        return MatchModel.builder().bigYardAddress(yardModel.getAddress())
                .bigYardName(yardModel.getName())
                .date(date.format(dateFormatter))
                .district(yardModel.getDistrictName())
                .startTime(slotEntity.getStartTime().format(hourFormatter))
                .endTime(slotEntity.getEndTime().format(hourFormatter))
                .type(typeYard)
                .subYardName(subYardEntity.getName())
                .province(yardModel.getProvince())
                .bookAt((new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(bookingEntity.getBookAt()))
                .bookingReference(bookingEntity.getReference())
                .bookingId(bookingEntity.getId())
                .bigYardId(yardId)
                .subYardId(subYardId)
                .slotId(slotId)
                .bookingStatus(bookingEntity.getStatus())
                .price(bookingEntity.getPrice())
                .build();
    }

    public List<MatchModel> getListMatchModelFromListBookingEntity(List<BookingEntity> bookingEntities) {
        List<MatchModel> result = new ArrayList<>();

        for (BookingEntity bookingEntity : bookingEntities) {
            MatchModel model = transformMatchModelFromBookingEntity(bookingEntity);
            result.add(model);
        }

        return result;
    }
}
