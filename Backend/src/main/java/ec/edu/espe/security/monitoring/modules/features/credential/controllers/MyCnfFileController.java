package ec.edu.espe.security.monitoring.modules.features.credential.controllers;
/*
 * Author: Anyel EC
 * Github: https://github.com/Anyel-ec
 * Creation date: 16/11/2024
 */

import ec.edu.espe.security.monitoring.common.dto.JsonResponseDto;
import ec.edu.espe.security.monitoring.modules.integrations.docker.utils.MyCnfFileGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para probar la generación del archivo .my.cnf
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class MyCnfFileController {

    private final MyCnfFileGenerator myCnfFileGenerator;

    @GetMapping("/generate-my-cnf")
    public ResponseEntity<JsonResponseDto> generateMyCnfFile(@RequestParam(name = "path", required = false) String path) {
        try {
            String targetPath = (path != null && !path.isEmpty()) ? path : "../.container";
            myCnfFileGenerator.generateMyCnfFile();
            String message = "Archivo .my.cnf generado exitosamente en: " + targetPath;

            log.info(message);
            return new ResponseEntity<>(new JsonResponseDto(true, HttpStatus.OK.value(), message, null), HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "Error al generar el archivo .my.cnf: " + e.getMessage();
            log.error(errorMessage, e);
            return new ResponseEntity<>(new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
