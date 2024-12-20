package com.ai.repository;

import com.ai.domain.AlarmDTO;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AlarmRepository extends MongoRepository<AlarmDTO, String> {
    AlarmDTO save(AlarmDTO alarmDTO);
    AlarmDTO insert(AlarmDTO alarmDTO);
    AlarmDTO findAlarmDTOById(String alarmDTO);
}