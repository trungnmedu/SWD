package com.swd.backend.service;

import com.swd.backend.api.v1.owner.yard.request.SubYardRequest;
import com.swd.backend.api.v1.owner.yard.request.YardRequest;
import com.swd.backend.api.v1.owner.yard.response.GetYardDetailResponse;
import com.swd.backend.api.v1.owner.yard.response.GetYardInBookingResponse;
import com.swd.backend.api.v1.owner.yard.response.GetYardResponse;
import com.swd.backend.api.v1.yard.search.YardResponse;
import com.swd.backend.entity.*;
import com.swd.backend.model.*;
import com.swd.backend.myrepository.YardCustomRepository;
import com.swd.backend.repository.*;
import com.swd.backend.utils.DateHelper;
import com.swd.backend.utils.TimeMappingHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class YardService {
    private YardRepository yardRepository;
    private SlotRepository slotRepository;
    private SubYardRepository subYardRepository;
    private DistrictRepository districtRepository;
    private ProvinceRepository provinceRepository;
    private YardPictureRepository yardPictureRepository;
    private YardCustomRepository yardCustomRepository;
    private FirebaseStoreService firebaseStoreService;
    private TypeYardRepository typeYardRepository;
    private TimeMappingHelper timeMappingHelper;
    private SubYardService subYardService;
    public static final int MAX_IMAGE = 3;

    @Transactional(rollbackFor = DataAccessException.class)
    public void createNewYard(String userId, YardRequest createYardModel, MultipartFile[] images) throws DataAccessException {
        final String parentYardId = UUID.randomUUID().toString();
        YardEntity parentYard = YardEntity.builder()
                .id(parentYardId)
                .ownerId(userId)
                .name(createYardModel.getName())
                .address(createYardModel.getAddress())
                .active(true)
                .districtId(createYardModel.getDistrictId())
                .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .openAt(LocalTime.parse(createYardModel.getOpenAt()))
                .closeAt(LocalTime.parse(createYardModel.getCloseAt()))
                .slotDuration(timeMappingHelper.getTimeMapping().get(createYardModel.getSlotDuration()))
                .build();
        //Save parent yard
        yardRepository.save(parentYard);

        //Save sub-yard
        List<SubYardRequest> subYardList = createYardModel.getSubYards();

        addSubYard(subYardList, parentYardId);

        addImages(images, parentYardId);
    }

    public void addSubYard(List<SubYardRequest> subYards, String yardId) {
        if (subYards != null && subYards.size() > 0) {
            List<SubYardEntity> subYardEntities = new ArrayList<>();
            List<SlotEntity> slotEntities = new ArrayList<>();
            List<TypeYard> typeList = typeYardRepository.findAll();
            HashMap<String, Integer> typeMapper = new HashMap<>();
            typeList.forEach(typeYard -> typeMapper.put(typeYard.getTypeName().toUpperCase(), typeYard.getId()));
            subYards.forEach(subYard -> {
                String subYardId = UUID.randomUUID().toString();
                int type = typeMapper.get(subYard.getType() == 3 ? "3 VS 3" : "5 VS 5");
                SubYardEntity subYardEntity = SubYardEntity.builder()
                        .id(subYardId)
                        .name(subYard.getName())
                        .parentYard(yardId)
                        .typeYard(type)
                        .active(true)
                        .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                        .updatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                        .build();
                subYardEntities.add(subYardEntity);

                subYard.getSlots().forEach(slot -> {
                    SlotEntity slotEntity = SlotEntity.builder()
                            .refYard(subYardId)
                            .startTime(LocalTime.parse(slot.getStartTime()))
                            .endTime(LocalTime.parse(slot.getEndTime()))
                            .active(true)
                            .price(slot.getPrice())
                            .build();
                    slotEntities.add(slotEntity);
                });
            });
            subYardRepository.saveAll(subYardEntities);
            slotRepository.saveAll(slotEntities);
        }
    }

    public void addImages(MultipartFile[] images, String yardId) {
        if (images != null && images.length > 0) {
            List<YardPictureEntity> listImage = Arrays.stream(images).parallel().map(image -> {
                String url = null;
                try {
                    url = firebaseStoreService.uploadFile(image);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return YardPictureEntity.builder().image(url).refId(yardId).build();
            }).collect(Collectors.toList());
            if (listImage.size() > 0) {
                new Thread(() -> yardPictureRepository.saveAll(listImage)).start();
            }
        }
    }


    public YardResponse findYardByFilter(Integer provinceId, Integer districtId, Integer ofSet, Integer page) {
        int pageValue = (page == null || page < 1) ? 1 : page;
        int ofSetValue = (ofSet == null || ofSet < 1) ? 6 : ofSet;

        int maxResult = yardCustomRepository.getMaxResultFindYardByFilter(provinceId, districtId);
        if (((pageValue - 1) * ofSetValue) >= maxResult) {
            pageValue = 1;
        }
        List<?> listResult = yardCustomRepository.findYardByFilter(provinceId, districtId, ofSetValue, pageValue);

        List<YardModel> yardModels = listResult.stream().map(item -> {
            if (item instanceof YardEntity) {
                YardEntity yard = (YardEntity) item;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                DistrictEntity districtEntity = districtRepository.findById(yard.getDistrictId());
                String province = provinceRepository.findDistinctById(districtEntity.getProvinceId()).getProvinceName();
                return YardModel.builder()
                        .id(yard.getId())
                        .name(yard.getName())
                        .address(yard.getAddress())
                        .score(yard.getScore())
                        .districtName(districtRepository.findById(yard.getDistrictId()).getDistrictName())
                        .province(province)
                        .openAt(yard.getOpenAt().format(formatter))
                        .closeAt(yard.getCloseAt().format(formatter))
                        .reference(yard.getReference())
                        .ownerId(yard.getOwnerId())
                        .build();
            } else {
                return null;
            }
        }).collect(Collectors.toList());

        yardModels.forEach(yardModel -> {
            List<String> images = new ArrayList<>();
            List<YardPictureEntity> listPicture = yardPictureRepository.getAllByRefIdOrderById(yardModel.getId());
            listPicture.forEach(picture -> images.add(picture.getImage()));
            yardModel.setImages(images);
        });
        yardModels.sort((first, second) -> Integer.compare(second.getScore(), first.getScore()));
        return YardResponse.builder().yards(yardModels).maxResult(maxResult).page(pageValue).build();
    }

    public boolean isAvailableYard(String yardId) {
        YardEntity yard = yardRepository.findYardEntityByIdAndActiveAndDeleted(yardId, true, false);
        return yard != null;
    }

    public YardModel getYardModelFromYardId(String yardId) {
        YardEntity yard = yardRepository.findYardEntityById(yardId);
        if (yard == null) {
            return null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            int provinceId = districtRepository.findById(yard.getDistrictId()).getProvinceId();
            String provinceName = provinceRepository.findDistinctById(provinceId).getProvinceName();
            YardModel yardModel = YardModel.builder()
                    .id(yard.getId())
                    .ownerId(yard.getOwnerId())
                    .name(yard.getName())
                    .address(yard.getAddress())
                    .districtName(districtRepository.findById(yard.getDistrictId()).getDistrictName())
                    .province(provinceName)
                    .score(yard.getScore())
                    .openAt(yard.getOpenAt().format(formatter))
                    .closeAt(yard.getCloseAt().format(formatter))
                    .build();
            List<String> images = new ArrayList<>();
            List<YardPictureEntity> listPicture = yardPictureRepository.getAllByRefIdOrderById(yardModel.getId());
            listPicture.forEach(picture -> images.add(picture.getImage()));
            yardModel.setImages(images);
            return yardModel;
        }
    }

    public YardEntity getYardById(String yardId) {
        return yardRepository.findYardEntitiesById(yardId);
    }

    public YardEntity getYardByIdAndNotDeleted(String yardId) {
        return yardRepository.findYardEntityByIdAndDeleted(yardId, false);
    }

    public void updateYard(YardEntity yard) throws DataAccessException {
        yardRepository.save(yard);
    }


    public String getYardFullAddress(String yardId) {
        YardEntity yard = getYardById(yardId);
        DistrictEntity districtEntity = districtRepository.findById(yard.getDistrictId());
        String district = districtEntity.getDistrictName();
        String province = provinceRepository.findDistinctById(districtEntity.getProvinceId()).getProvinceName();
        return yard.getAddress() + ", " + district + ", " + province;
    }

    public GetYardResponse findAllYardByOwnerId(String ownerId, SearchModel searchModel) {
        List<YardEntity> yards = yardRepository.findAllByOwnerIdAndDeleted(ownerId, false);
        yards.sort((first, second) -> Integer.compare(second.getReference(), first.getReference()));
        int maxResult;
        int pageValue = 1;
        int offSetValue = 10;
        if (searchModel != null) {
            pageValue = searchModel.getPage();
            offSetValue = searchModel.getItemsPerPage();
            yards = searchYardsByKeyword(searchModel.getKeyword(), yards);
            yards = filterYard(searchModel.getFilter(), yards);
            sortYards(searchModel.getSort(), yards);
        }
        List<YardModel> yardModels = transformYardEntityToYardModal(yards);
        if (yardModels == null || yardModels.size() == 0) {
            return GetYardResponse.builder().maxResult(0).listYard(Collections.emptyList()).page(0).build();
        }
        maxResult = yardModels.size();

        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        int startIndex = Math.max((pageValue - 1) * offSetValue, 0);
        int endIndex = Math.min((pageValue * offSetValue), maxResult);
        return GetYardResponse.builder().listYard(yardModels.subList(startIndex, endIndex)).page(pageValue).maxResult(maxResult).build();
    }

    private List<YardModel> transformYardEntityToYardModal(List<YardEntity> yards) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return yards.stream().map(yard -> YardModel.builder()
                .id(yard.getId())
                .address(yard.getAddress())
                .name(yard.getName())
                .openAt(yard.getOpenAt().format(formatter))
                .closeAt(yard.getCloseAt().format(formatter))
                .reference(yard.getReference())
                .createdAt(yard.getCreateAt())
                .address(yard.getAddress())
                .isActive(yard.isActive())
                .build()).collect(Collectors.toList());
    }

    private List<YardEntity> filterYard(FilterModel filter, List<YardEntity> yards) {
        if (filter == null) {
            return yards;
        }
        if (filter.getField().equalsIgnoreCase("isActive")) {
            if (Boolean.parseBoolean(filter.getValue())) {
                return yards.stream().filter(YardEntity::isActive).collect(Collectors.toList());
            } else {
                return yards.stream().filter(yard -> !yard.isActive()).collect(Collectors.toList());
            }
        }
        return yards;
    }

    private void sortYards(String sortFiled, List<YardEntity> yards) {
        String sortColumn = sortFiled != null ? sortFiled.trim() : null;
        if (sortColumn == null) {
            return;
        }
        char sort = sortColumn.charAt(0);
        if (sort == '+' || sort == '-') {
            sortColumn = sortFiled.substring(1);
        } else {
            sort = '+';
        }

        if (sortColumn.equals("reference") && sort == '+') {
            yards.sort(Comparator.comparingInt(YardEntity::getReference));
        }

        if (sortColumn.equals("name")) {
            if (sort == '+') {
                yards.sort((fistYard, secondYard) -> secondYard.getName().compareTo(fistYard.getName()));
            } else {
                yards.sort(Comparator.comparing(YardEntity::getName));
            }
        }

        if (sortColumn.equals("address")) {
            if (sort == '+') {
                yards.sort(Comparator.comparing(YardEntity::getAddress));
            } else {
                yards.sort((fistYard, secondYard) -> secondYard.getAddress().compareTo(fistYard.getAddress()));
            }
        }

        if (sortColumn.equals("createdAt")) {
            if (sort == '+') {
                yards.sort(Comparator.comparing(YardEntity::getCreateAt));
            } else {
                yards.sort((fistYard, secondYard) -> secondYard.getCreateAt().compareTo(fistYard.getCreateAt()));
            }
        }

        if (sortColumn.equals("openAt")) {
            if (sort == '+') {
                yards.sort(Comparator.comparing(YardEntity::getOpenAt));
            } else {
                yards.sort((fistYard, secondYard) -> secondYard.getOpenAt().compareTo(fistYard.getOpenAt()));
            }
        }

        if (sortColumn.equals("closeAt")) {
            if (sort == '+') {
                yards.sort(Comparator.comparing(YardEntity::getCloseAt));
            } else {
                yards.sort((fistYard, secondYard) -> secondYard.getCloseAt().compareTo(fistYard.getCloseAt()));
            }
        }
    }

    private List<YardEntity> searchYardsByKeyword(String keyword, List<YardEntity> yards) {
        String keywordValue = keyword != null && keyword.trim().length() > 0 ? keyword.trim().toLowerCase() : null;
        if (keywordValue == null) {
            return yards;
        }
        return yards.stream().filter(yard -> String.valueOf(yard.getReference()).contains(keywordValue)
                || yard.getAddress().toLowerCase().contains(keywordValue.toLowerCase())
                || yard.getName().toLowerCase().contains(keywordValue.toLowerCase())
        ).collect(Collectors.toList());
    }

    @Transactional
    public void reactiveAllYardsOfOwner(String ownerId) {
        yardCustomRepository.reactivateAllYardsOfOwner(ownerId);
    }

    public GetYardDetailResponse getYardDetailResponseFromYardId(String yardId) {
        YardEntity yardEntity = getYardByIdAndNotDeleted(yardId);
        if (yardEntity == null) {
            throw new RuntimeException("Can not get yard from " + yardId);
        }

        DistrictEntity districtEntity = districtRepository.findById(yardEntity.getDistrictId());

        String districtName = districtEntity.getDistrictName();
        int provinceId = districtEntity.getProvinceId();
        String provinceName = provinceRepository.findDistinctById(provinceId).getProvinceName();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        int hour = yardEntity.getSlotDuration() / 60;
        int minute = yardEntity.getSlotDuration() % 60;
        String duration = LocalTime.of(hour, minute).format(formatter);

        List<String> images = yardPictureRepository.getAllByRefIdOrderById(yardId).stream().map(YardPictureEntity::getImage).collect(Collectors.toList());
        List<SubYardDetailModel> subYardDetailModels = getSubYardDetailModelFromYardId(yardId);
        return GetYardDetailResponse.builder()
                .id(yardEntity.getId())
                .name(yardEntity.getName())
                .address(yardEntity.getAddress())
                .districtId(yardEntity.getDistrictId())
                .districtName(districtName)
                .provinceId(provinceId)
                .provinceName(provinceName)
                .openTime(yardEntity.getOpenAt().format(formatter))
                .closeTime(yardEntity.getCloseAt().format(formatter))
                .duration(duration)
                .score(yardEntity.getScore())
                .images(images)
                .subYards(subYardDetailModels).build();
    }

    public List<SubYardDetailModel> getSubYardDetailModelFromYardId(String yardId) {
        List<SubYardModel> subYards = subYardService.getSubYardsByBigYard(yardId);
        return subYards.stream().map(subYardModel -> {
            List<SlotModel> slots = slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardModel.getId()).stream().map(SlotModel::buildFromSlotEntity).sorted().collect(Collectors.toList());
            return SubYardDetailModel.builder().id(subYardModel.getId())
                    .name(subYardModel.getName())
                    .reference(subYardModel.getReference())
                    .createAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(subYardModel.getCreateAt()))
                    .isActive(subYardModel.isActive())
                    .typeYard(subYardModel.getTypeYard())
                    .slots(slots).build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void setIsActiveFalseForYard(String yardId) {
        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        yardEntity.setActive(false);
        yardRepository.save(yardEntity);
    }

    @Transactional
    public void setIsActiveTrueForYard(String yardId) {
        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        yardEntity.setActive(true);
        yardRepository.save(yardEntity);
    }

    @Transactional
    public void setIsDeletedTrueForYard(String yardId) {
        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        yardEntity.setDeleted(true);
        yardRepository.save(yardEntity);
    }

    public String getOwnerIdOfYard(String yardId) {
        return yardRepository.findYardEntityById(yardId).getOwnerId();
    }

    public List<YardEntity> getAllYardEntityOfOwner(String ownerId) {
        return yardRepository.findYardEntitiesByOwnerIdOrderByReferenceAsc(ownerId);
    }

    public List<GetYardInBookingResponse> getSimpleYardDetailsFromOwner(String ownerId) {
        List<GetYardInBookingResponse> getYardsInBookingResponse = new ArrayList<>();
        List<YardEntity> yards = yardRepository.findYardEntitiesByOwnerIdAndDeleted(ownerId, false);
        for (YardEntity yard : yards) {
            GetYardInBookingResponse getYardInBookingResponse = new GetYardInBookingResponse();
            getYardsInBookingResponse.add(getYardInBookingResponse);
            getYardInBookingResponse.setYardId(yard.getId());
            getYardInBookingResponse.setYardName(yard.getName());
            List<SubYardEntity> subYards = subYardRepository.findSubYardEntitiesByParentYardAndParentActiveAndDeleted(yard.getId(), true, false);
            List<SubYardSimpleModel> subYardsDetail = new ArrayList<>();
            for (SubYardEntity subYard : subYards) {
                String typeYard = typeYardRepository.getTypeYardById(subYard.getTypeYard()).getTypeName();
                subYardsDetail.add(new SubYardSimpleModel(subYard.getId(), subYard.getName(), typeYard));
            }
            getYardInBookingResponse.setSubYards(subYardsDetail);
        }
        return getYardsInBookingResponse;
    }
}