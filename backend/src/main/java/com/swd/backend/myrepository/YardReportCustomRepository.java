package com.swd.backend.myrepository;

import com.swd.backend.model.YardReportModel;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class YardReportCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<YardReportModel> getYardReportModelByPage(int startIndex, int endIndex) {
        Query query = null;

        try {
            String nativeQuery = "SELECT yr.id, yr.user_id, yr.yard_id, y.owner_id, a.full_name as userFullname, y.name, y.address, account_owner.email, yr.status, yr.created_at, yr.updated_at, yr.reason, yr.reference, a.email as userEmail, account_owner.full_name" +
                    " FROM yard_report yr INNER JOIN yards y ON yr.yard_id = y.id" +
                    "                     INNER JOIN accounts a ON yr.user_id = a.id" +
                    "                     INNER JOIN accounts account_owner ON y.owner_id = account_owner.id" +
                    " ORDER BY yr.status DESC, yr.updated_at DESC";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setFirstResult(startIndex);
            query.setMaxResults(endIndex - startIndex + 1);
            List<?> queriedList = query.getResultList();

            if (queriedList == null) {
                return null;
            }

            List<YardReportModel> yardReportModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardReportModel.builder().reportId((String) objects[0])
                        .userId((String) objects[1])
                        .yardId((String) objects[2])
                        .ownerId((String) objects[3])
                        .userName((String) objects[4])
                        .yardName((String) objects[5])
                        .yardAddress((String) objects[6])
                        .ownerEmail((String) objects[7])
                        .status((String) objects[8])
                        .createdAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((Timestamp) objects[9]))
                        .updatedAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((Timestamp) objects[10]))
                        .reason((String) objects[11])
                        .reference((Integer) objects[12])
                        .userEmail((String) objects[13])
                        .ownerName((String) objects[14])
                        .build();
            }).collect(Collectors.toList());

            return yardReportModels;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<YardReportModel> getAllYardReportModels() {
        Query query = null;

        try {
            String nativeQuery = "SELECT yr.id, yr.user_id, yr.yard_id, y.owner_id, a.full_name as userFullname, y.name, y.address, account_owner.email, yr.status, yr.created_at, yr.updated_at, yr.reason, yr.reference, a.email as userEmail, account_owner.full_name" +
                    " FROM yard_report yr INNER JOIN yards y ON yr.yard_id = y.id" +
                    "                     INNER JOIN accounts a ON yr.user_id = a.id" +
                    "                     INNER JOIN accounts account_owner ON y.owner_id = account_owner.id" +
                    " ORDER BY yr.status DESC, yr.updated_at DESC";

            query = entityManager.createNativeQuery(nativeQuery);
            List<?> queriedList = query.getResultList();

            if (queriedList == null) {
                return null;
            }

            List<YardReportModel> yardReportModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardReportModel.builder().reportId((String) objects[0])
                        .userId((String) objects[1])
                        .yardId((String) objects[2])
                        .ownerId((String) objects[3])
                        .userName((String) objects[4])
                        .yardName((String) objects[5])
                        .yardAddress((String) objects[6])
                        .ownerEmail((String) objects[7])
                        .status((String) objects[8])
                        .createdAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((Timestamp) objects[9]))
                        .updatedAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((Timestamp) objects[10]))
                        .reason((String) objects[11])
                        .reference((Integer) objects[12])
                        .userEmail((String) objects[13])
                        .ownerName((String) objects[14])
                        .build();
            }).collect(Collectors.toList());

            return yardReportModels;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public int countAllYardReports() {
        Query query = null;

        try {
            String nativeQuery = "SELECT count(*)" +
                    " FROM yard_report";

            query = entityManager.createNativeQuery(nativeQuery);

            Object queriedObject = query.getSingleResult();

            if (queriedObject == null) {
                return 0;
            }

            return ((BigInteger) queriedObject).intValue();
        } catch (Exception ex) {
            return 0;
        }
    }
}
