package com.security.app.controller;

import com.security.app.constant.HttpAnswer;
import com.security.app.dto.LoginUserDTO;
import com.security.app.dto.SubjectDTO;
import com.security.app.dto.WarehouseDTO;
import com.security.app.dto.answer.ApplicationAnswerDTO;
import com.security.app.dto.util.HttpResponse;
import com.security.app.exception.ExceptionHandling;
import com.security.app.exception.model.*;
import com.security.app.service.HomeService;
import com.security.app.utility.JWTTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.security.app.constant.FileConstant.FORWARD_SLASH;
import static com.security.app.constant.FileConstant.USER_FOLDER;
import static com.security.app.constant.HttpAnswer.*;
import static com.security.app.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/home")
@AllArgsConstructor
public class HomeController extends ExceptionHandling {

    private final HomeService homeService;
    private final JWTTokenProvider jwtTokenProvider;

    @GetMapping("/warehouse/name")
    public ResponseEntity<List<String>> getWarehouseName() {
        return new ResponseEntity<>(homeService.getWarehouseName(), OK);
    }

    @PostMapping("/warehouse/create")
    public ResponseEntity<HttpResponse> createWarehouse(@RequestBody WarehouseDTO warehouseDTO) throws WarehouseNameExistException {
        homeService.createWarehouse(warehouseDTO);
        return HttpAnswer.response(CREATED, WAREHOUSE_SUCCESSFULLY_REGISTERED);
    }

    @PostMapping("/subject/create")
    public ResponseEntity<List<ApplicationAnswerDTO>> createSubject(@RequestBody SubjectDTO subjectDTO,
                                                      HttpServletRequest request) throws ValidWarehouseException, UsernameExistException {
        String usernameWithToken = getUsernameWithToken(request);
        return new ResponseEntity<>(homeService.createSubject(subjectDTO, usernameWithToken), OK);
    }

    @GetMapping("/application/get")
    public ResponseEntity<List<ApplicationAnswerDTO>> getApplications(HttpServletRequest request) throws UsernameExistException {
        String usernameWithToken = getUsernameWithToken(request);
        return new ResponseEntity<>(homeService.getApplications(usernameWithToken), OK);
    }

    @PostMapping("/application/pick_up")
    public ResponseEntity<List<ApplicationAnswerDTO>> pickUpSubject(@RequestParam String number,
                                                                    HttpServletRequest request) throws UsernameExistException, NoSuchApplicationException {
        String usernameWithToken = getUsernameWithToken(request);
        return new ResponseEntity<>(homeService.pickUpSubject(number, usernameWithToken), OK);
    }

    @PostMapping("/application/id")
    public ResponseEntity<ApplicationAnswerDTO> getApplication(@RequestParam Long id) throws UsernameExistException {
        return new ResponseEntity<>(homeService.getApplication(id), OK);
    }

    @GetMapping(path = "/{username}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@PathVariable("username") String username,
                                               @PathVariable("fileName") String fileName,
                                               HttpServletResponse response) throws IOException {
        // Проверяем, есть ли файл
        Path file = Paths.get(USER_FOLDER + FORWARD_SLASH + username + FORWARD_SLASH + "CHECK" + FORWARD_SLASH + fileName);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        // Устанавливаем заголовки для ответа
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Читаем байты файла и отправляем в браузер
        byte[] data = Files.readAllBytes(file);
        return ResponseEntity.ok().body(data);
    }

    private String getUsernameWithToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return jwtTokenProvider.getSubject(token);
    }

}
