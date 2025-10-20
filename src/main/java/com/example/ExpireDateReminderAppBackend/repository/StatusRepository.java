package com.example.ExpireDateReminderAppBackend.repository;

import com.example.ExpireDateReminderAppBackend.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

}
