package com.ai.service;

import com.ai.domain.AlarmDTO;
import org.springframework.stereotype.Service;

@Service
public interface AlarmService{
    public void save(AlarmDTO alarmDTO);
    public void insert(AlarmDTO alarmDTO);
    public AlarmDTO findAlarmDTOById(String nickName);
}