package com.demo.repository;

import com.demo.model.SystemProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemPropertyRepository extends JpaRepository<SystemProperty, Integer> {
}
