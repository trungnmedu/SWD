package com.swd.backend.myrepository;

import com.swd.backend.entity.YardEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class YardCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<?> findYardByFilter(Integer provinceId, Integer districtId, int ofSet, int page) {
        Query query = null;

        if (districtId == null && provinceId == null) {
            String nativeQuery = "SELECT * FROM yards WHERE is_active = true AND is_deleted = false";
            query = entityManager.createNativeQuery(nativeQuery, YardEntity.class);
        }

        if (query == null && districtId != null) {
            String nativeQuery = "SELECT * FROM yards WHERE is_deleted = false AND is_active = true AND district_id = ?1";
            query = entityManager.createNativeQuery(nativeQuery, YardEntity.class);
            query.setParameter(1, districtId);
        }

        if (query == null && provinceId != null) {
            String nativeQuery = "SELECT yards.* FROM yards" +
                    " INNER JOIN districts district ON district.id = yards.district_id" +
                    " WHERE yards.is_active = true" +
                    " AND yards.is_deleted = false" +
                    " AND district.province_id = ?1";
            query = entityManager.createNativeQuery(nativeQuery, YardEntity.class);
            query.setParameter(1, provinceId);
        }
        if (query != null) {
            query.setFirstResult((page - 1) * ofSet);
            query.setMaxResults(ofSet);
            return query.getResultList();
        } else {
            return null;
        }
    }

    public int getMaxResultFindYardByFilter(Integer provinceId, Integer districtId) {
        Query query = null;

        if (districtId == null && provinceId == null) {
            String nativeQuery = "SELECT COUNT(*) FROM yards WHERE is_active = true AND is_deleted = false";
            query = entityManager.createNativeQuery(nativeQuery);
        }

        if (query == null && districtId != null) {
            String nativeQuery = "SELECT COUNT(*) FROM yards WHERE is_deleted = false AND is_active = true AND district_id = ?1";
            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, districtId);
        }

        if (query == null && provinceId != null) {
            String nativeQuery = "SELECT COUNT(yards.*) FROM yards" +
                    " INNER JOIN districts district ON district.id = yards.district_id" +
                    " WHERE yards.is_active = true" +
                    " AND yards.is_deleted = false" +
                    " AND district.province_id = ?1";
            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, provinceId);
        }

        if (query != null) {
            Object result = query.getSingleResult();
            return result instanceof BigInteger ? ((BigInteger) result).intValue() : 0;
        } else {
            return 0;
        }
    }

    public int inactivateAllYardsOfOwner(String ownerId) {
        Query query = null;

        try {
            String nativeQuery = "UPDATE yards SET is_active = false WHERE owner_id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, ownerId);
            int rowAffected = query.executeUpdate();

            return rowAffected;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public int reactivateAllYardsOfOwner(String ownerId) {
        Query query = null;

        try {
            String nativeQuery = "UPDATE yards SET is_active = true WHERE owner_id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, ownerId);
            int rowAffected = query.executeUpdate();

            return rowAffected;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
