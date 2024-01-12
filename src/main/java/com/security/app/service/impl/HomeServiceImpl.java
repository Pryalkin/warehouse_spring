package com.security.app.service.impl;

import com.security.app.dto.SubjectDTO;
import com.security.app.dto.WarehouseDTO;
import com.security.app.dto.answer.ApplicationAnswerDTO;
import com.security.app.dto.answer.SubjectAnswerDTO;
import com.security.app.enumeration.Status;
import com.security.app.exception.model.NoSuchApplicationException;
import com.security.app.exception.model.UsernameExistException;
import com.security.app.exception.model.ValidWarehouseException;
import com.security.app.exception.model.WarehouseNameExistException;
import com.security.app.model.Application;
import com.security.app.model.Subject;
import com.security.app.model.User;
import com.security.app.model.Warehouse;
import com.security.app.repository.ApplicationRepository;
import com.security.app.repository.SubjectRepository;
import com.security.app.repository.WarehouseRepository;
import com.security.app.service.AuthService;
import com.security.app.service.HomeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.security.app.constant.FileConstant.*;


@Service
@AllArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final WarehouseRepository warehouseRepository;
    private final AuthService authService;
    private final ApplicationRepository applicationRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public List<String> getWarehouseName() {
        return warehouseRepository.findAll().stream().map(Warehouse::getName).toList();
    }

    @Override
    public void createWarehouse(WarehouseDTO warehouseDTO) throws WarehouseNameExistException {
        if (checkWarehouseName(warehouseDTO.getName())) {
            throw new WarehouseNameExistException("Склад с таким названием уже существует!");
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseDTO.getName());
        warehouse.setLength(warehouseDTO.getLength());
        warehouse.setWidth(warehouseDTO.getWidth());
        warehouse.setHeight(warehouseDTO.getHeight());
        warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public List<ApplicationAnswerDTO> createSubject(SubjectDTO subjectDTO, String usernameWithToken) throws ValidWarehouseException, UsernameExistException {
        Warehouse warehouse = warehouseRepository.findByName(subjectDTO.getWarehouseName()).get();
        Subject subject = new Subject();
        subject.setLength(subjectDTO.getLength());
        subject.setHeight(subjectDTO.getHeight());
        subject.setWidth(subjectDTO.getWidth());
        Application application = new Application();
        String number = generateNumber();
        while (applicationRepository.findByNumber(number).isPresent())
            number = generateNumber();
        application.setNumber(number);
        application = applicationRepository.save(application);
        subject.addApplication(application);
        subjectRepository.save(subject);
        User user = authService.findByUsername(usernameWithToken);
        user.addApplication(application);
        if (warehouse.getLength() - subjectDTO.getLength() < 0) getException(application, "Длина предмета больше!");
        if (warehouse.getWidth() - subjectDTO.getWidth() < 0) getException(application, "Ширина предмета больше!");
        if (warehouse.getHeight() - subjectDTO.getHeight() < 0) getException(application, "Высота предмета больше!");
        warehouse.setLength(warehouse.getLength() - subjectDTO.getLength() - 2);
        warehouse.setWidth(warehouse.getWidth() - subjectDTO.getWidth() - 2);
        application.setStatus(Status.ACCEPTED.getStatus());
        application.setMessage("Предмет добавлен на склад!");
        warehouse.addApplication(application);
        warehouseRepository.save(warehouse);
        application = applicationRepository.findById(application.getId()).get();
        application = createCheck(application);
        applicationRepository.save(application);
        return getApplications(usernameWithToken);
    }

    private Application createCheck(Application app) {
        Path file = Paths.get(USER_FOLDER + FORWARD_SLASH + "CHECK" + DOT + DOCX_EXTENSION);
        try (FileInputStream fileInputStream = new FileInputStream(file.toAbsolutePath().toFile())) {
            XWPFDocument doc = new XWPFDocument(fileInputStream);
            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph para : cell.getParagraphs()) {
                            for (XWPFRun run : para.getRuns()) {
                                CTText ctText = run.getCTR().getTArray(0);
                                if (ctText.getStringValue().equals("username")) {
                                    String replacedText = ctText.getStringValue().replace("username", app.getUser().getUsername());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("Ширина")) {
                                    String replacedText = ctText.getStringValue().replace("Ширина", app.getSubject().getLength().toString());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("Длина")) {
                                    String replacedText = ctText.getStringValue().replace("Длина",  app.getSubject().getWidth().toString());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("Высота")) {
                                    String replacedText = ctText.getStringValue().replace("Высота", app.getSubject().getHeight().toString());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("01.01.2000")) {
                                    String replacedText = ctText.getStringValue().replace("01.01.2000", new Date().toString());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("number")) {
                                    String replacedText = ctText.getStringValue().replace("number", app.getNumber());
                                    ctText.setStringValue(replacedText);
                                }
                                if (ctText.getStringValue().equals("WAREHOUSE")) {
                                    String replacedText = ctText.getStringValue().replace("WAREHOUSE", app.getWarehouse().getName());
                                    ctText.setStringValue(replacedText);
                                }
                            }
                        }
                    }
                }
            }
            Path userFolder = Paths.get(USER_FOLDER + FORWARD_SLASH + app.getUser().getUsername() + FORWARD_SLASH + "CHECK").toAbsolutePath().normalize();
            if (!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(userFolder + FORWARD_SLASH + app.getNumber() + DOT + DOCX_EXTENSION)) {
                doc.write(fileOutputStream);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        app.setFile(setLogoDOCXUrl(app.getUser().getUsername(), app.getNumber()));
        return app;
    }

    @Override
    public List<ApplicationAnswerDTO> getApplications(String usernameWithToken) throws UsernameExistException {
        User user = authService.findByUsername(usernameWithToken);
        return user.getApplications().stream().map(this::createApplicationAnswerDTO).toList();
    }

    private ApplicationAnswerDTO createApplicationAnswerDTO(Application app){
        ApplicationAnswerDTO applicationAnswerDTO = new ApplicationAnswerDTO();
        applicationAnswerDTO.setId(app.getId());
        applicationAnswerDTO.setNumber(app.getNumber());
        applicationAnswerDTO.setStatus(app.getStatus());
        applicationAnswerDTO.setMessage(app.getMessage());
        applicationAnswerDTO.setFile(app.getFile());
        if (app.getWarehouse() == null) applicationAnswerDTO.setWarehouseName("NULL");
        else applicationAnswerDTO.setWarehouseName(app.getWarehouse().getName());
        SubjectAnswerDTO subjectAnswerDTO = new SubjectAnswerDTO();
        subjectAnswerDTO.setLength(app.getSubject().getLength());
        subjectAnswerDTO.setWidth(app.getSubject().getWidth());
        subjectAnswerDTO.setHeight(app.getSubject().getHeight());
        applicationAnswerDTO.setSubjectAnswerDTO(subjectAnswerDTO);
        return applicationAnswerDTO;
    }

    @Override
    @Transactional
    public List<ApplicationAnswerDTO> pickUpSubject(String number, String usernameWithToken) throws UsernameExistException, NoSuchApplicationException {
        Application application = applicationRepository.findByNumber(number)
                .orElseThrow(() -> new NoSuchApplicationException("Такая заявка отсутсвует!"));
        Warehouse warehouse = application.getWarehouse();
        warehouse.setWidth(warehouse.getWidth() + application.getWarehouse().getWidth() + 2);
        warehouse.setLength(warehouse.getLength() + application.getWarehouse().getLength()+ 2);
        warehouse.deleteApplication(application);
        warehouseRepository.save(warehouse);
        application.setStatus(Status.COMPLETED.getStatus());
        application.setMessage("Предмет забран со склада.");
        applicationRepository.save(application);
        return getApplications(usernameWithToken);
    }

    @Override
    public ApplicationAnswerDTO getApplication(Long id) {
        Application app = applicationRepository.findById(id).get();
        return createApplicationAnswerDTO(app);
    }

    private void getException(Application application, String message) throws ValidWarehouseException {
        application.setStatus(Status.DENIED.getStatus());
        application.setMessage(message);
        application.setFile("NULL");
        applicationRepository.save(application);
        throw new ValidWarehouseException(message);
    }

    private Boolean checkWarehouseName(String warehouseName){
        return warehouseRepository.findByName(warehouseName).isPresent();
    }

    private String generateNumber() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String setLogoDOCXUrl(String username, String name) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().
                path(APP_PATH + FORWARD_SLASH + username + FORWARD_SLASH + name + DOT + DOCX_EXTENSION).toUriString();
    }

}
