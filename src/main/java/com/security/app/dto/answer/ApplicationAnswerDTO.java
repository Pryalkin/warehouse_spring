package com.security.app.dto.answer;

import lombok.Data;

@Data
public class ApplicationAnswerDTO {

    private Long id;
    private String number;
    private String status;
    private String message;
    private String warehouseName;
    private String file;
    private SubjectAnswerDTO subjectAnswerDTO;

}
