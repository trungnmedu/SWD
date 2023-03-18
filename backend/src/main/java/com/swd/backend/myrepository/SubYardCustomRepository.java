package com.swd.backend.myrepository;

import com.swd.backend.entity.SubYardEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SubYardCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<SubYardEntity> getAllActiveSubYardByBigYard(String bigYardId) {
        String nativeQuery = "SELECT s.*" +
                " FROM sub_yards s INNER JOIN yards y ON s.parent_yard = y.id" +
                " WHERE s.parent_yard = ?1" +
                " AND s.is_active = true" +
                " AND s.is_deleted = false" +
                " AND y.is_active = true" +
                " AND y.is_deleted = false" +
                " ORDER BY updated_at";
        try {

            Query query = entityManager.createNativeQuery(nativeQuery, SubYardEntity.class);
            query.setParameter(1, bigYardId);
            List<?> result = query.getResultList();
            if (result == null) {
                return null;
            }
            return result.stream().map(subYard -> (subYard instanceof SubYardEntity) ? (SubYardEntity) subYard : null).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<SubYardEntity> getAllSubYardByBigYard(String bigYardId) {
        String nativeQuery = "SELECT s.*" +
                " FROM sub_yards s INNER JOIN yards y ON s.parent_yard = y.id" +
                " WHERE s.parent_yard = ?1" +
                " AND s.is_deleted = false" +
                " AND y.is_active = true" +
                " AND y.is_deleted = false" +
                " ORDER BY updated_at";
        try {

            Query query = entityManager.createNativeQuery(nativeQuery, SubYardEntity.class);
            query.setParameter(1, bigYardId);
            List<?> result = query.getResultList();
            if (result == null) {
                return null;
            }
            return result.stream().map(subYard -> (subYard instanceof SubYardEntity) ? (SubYardEntity) subYard : null).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBigYardIdFromSubYard(String subYardId) {
        try {
            Query query;

            String nativeQuery = "SELECT s.parent_yard" +
                    " FROM sub_yards s" +
                    " WHERE s.Id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, subYardId);
            return (String) query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    public String findTypeYardFromSubYardId(String subYardId) {
        try {
            Query query = null;

            String nativeQuery = "SELECT t.type_name" +
                    " FROM sub_yards s INNER JOIN type_yards t ON s.type_yard = t.id" +
                    " WHERE s.id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, subYardId);
            return (String) query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    public List<SubYardEntity> findAllRelativeSubYardBySubYardId(String subYardId) {
        List<SubYardEntity> subYardEntityList = null;
        try {
            String nativeQuery = "SELECT sub_yards.* FROM sub_yards WHERE sub_yards.parent_yard IN (SELECT parent_yard FROM sub_yards WHERE id LIKE ?)";
            Query query = entityManager.createNativeQuery(nativeQuery, SubYardEntity.class);
            query.setParameter(1, subYardId);
            List<?> result = query.getResultList();
            if (result != null) {
                subYardEntityList = result.stream().map(subYard -> {
                    if (subYard instanceof SubYardEntity) {
                        return (SubYardEntity) subYard;
                    }
                    return null;
                }).collect(Collectors.toList());
            }
            return subYardEntityList;
        } catch (DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            return null;
        }
    }

    public String getOwnerIdOfSubYard(String subYardId) {
        try {
            Query query = null;

            String nativeQuery = "SELECT y.owner_id " +
                    "FROM sub_yards s INNER JOIN yards y ON s.parent_yard = y.id " +
                    "WHERE s.id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, subYardId);
            return (String) query.getSingleResult();
        } catch (Exception ex) {
            return "";
        }
    }
}
