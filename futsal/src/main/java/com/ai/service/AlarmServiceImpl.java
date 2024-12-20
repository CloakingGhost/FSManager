package com.ai.service;

import com.ai.domain.AlarmDTO;
import com.ai.repository.AlarmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Slf4j
@Component
@Repository
public class AlarmServiceImpl implements AlarmService{
    @Autowired
    AlarmRepository aService;

    @Override
    public void save(AlarmDTO alarmDTO) {
        aService.save(alarmDTO);
    }

    @Override
    public void insert(AlarmDTO alarmDTO) {
        AlarmDTO alarm = aService.insert(alarmDTO);
    }

    @Override
    public AlarmDTO findAlarmDTOById(String nickName) {
        AlarmDTO alarm = aService.findAlarmDTOById(nickName);
        return alarm;
    }


}
