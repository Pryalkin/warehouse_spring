package com.security.app.service;

import com.security.app.dto.SubjectDTO;
import com.security.app.dto.WarehouseDTO;
import com.security.app.dto.answer.ApplicationAnswerDTO;
import com.security.app.exception.model.NoSuchApplicationException;
import com.security.app.exception.model.UsernameExistException;
import com.security.app.exception.model.ValidWarehouseException;
import com.security.app.exception.model.WarehouseNameExistException;

import java.util.List;

public interface HomeService {
    List<String> getWarehouseName();

    void createWarehouse(WarehouseDTO warehouseDTO) throws WarehouseNameExistException;

    List<ApplicationAnswerDTO> createSubject(SubjectDTO subjectDTO, String usernameWithToken) throws ValidWarehouseException, UsernameExistException;

    List<ApplicationAnswerDTO> getApplications(String usernameWithToken) throws UsernameExistException;

    List<ApplicationAnswerDTO> pickUpSubject(String number, String usernameWithToken) throws UsernameExistException, NoSuchApplicationException;

    ApplicationAnswerDTO getApplication(Long id);
}
