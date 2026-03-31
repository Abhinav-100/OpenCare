package com.ciphertext.opencarebackend.modules.catalog.repository;

import com.ciphertext.opencarebackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer>, JpaSpecificationExecutor<Tag> {

    Optional<Tag> findByName(String name);

    List<Tag> findByCategory(String category);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.displayName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tag> searchByNameOrDisplayName(@Param("query") String query);

    @Query("SELECT t FROM Tag t WHERE t.category = :category AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.displayName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Tag> searchByCategoryAndQuery(@Param("category") String category, @Param("query") String query);

    Set<Tag> findByIdIn(Set<Long> ids);

    @Query("SELECT t FROM Tag t WHERE t.name IN :names")
    Set<Tag> findByNameIn(@Param("names") Set<String> names);

    // PostgreSQL trigram search for better performance
    @Query(value = "SELECT * FROM tag WHERE name % :query OR display_name % :query " +
                   "ORDER BY GREATEST(similarity(name, :query), similarity(display_name, :query)) DESC " +
                   "LIMIT :limit", nativeQuery = true)
    List<Tag> fuzzySearch(@Param("query") String query, @Param("limit") int limit);
}