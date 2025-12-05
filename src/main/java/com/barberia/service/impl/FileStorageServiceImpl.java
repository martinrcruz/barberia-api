package com.barberia.service.impl;

import com.barberia.exception.BusinessException;
import com.barberia.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB por defecto
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};

    @Override
    public String almacenarArchivo(MultipartFile archivo, String categoria, Long sucursalId) throws IOException {
        validarArchivo(archivo);

        String nombreArchivo = generarNombreArchivoUnico(archivo.getOriginalFilename());
        String rutaRelativa = construirRutaRelativa(categoria, sucursalId, nombreArchivo);
        Path rutaCompleta = obtenerRutaCompleta(rutaRelativa);

        // Crear directorios si no existen
        Files.createDirectories(rutaCompleta.getParent());

        // Copiar archivo
        Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

        log.info("Archivo almacenado exitosamente: {}", rutaRelativa);
        return rutaRelativa;
    }

    @Override
    public String almacenarArchivoConCompresion(MultipartFile archivo, String categoria, Long sucursalId, int calidadCompresion) throws IOException {
        if (!esImagen(archivo)) {
            throw new BusinessException("Solo se pueden comprimir imágenes");
        }

        validarArchivo(archivo);

        // Leer imagen
        BufferedImage imagenOriginal = ImageIO.read(archivo.getInputStream());
        if (imagenOriginal == null) {
            throw new BusinessException("No se pudo leer la imagen");
        }

        // Comprimir imagen
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        comprimirImagen(imagenOriginal, outputStream, calidadCompresion, obtenerFormatoImagen(archivo.getOriginalFilename()));

        // Generar nombre y ruta
        String nombreArchivo = generarNombreArchivoUnico(archivo.getOriginalFilename());
        String rutaRelativa = construirRutaRelativa(categoria, sucursalId, nombreArchivo);
        Path rutaCompleta = obtenerRutaCompleta(rutaRelativa);

        // Crear directorios si no existen
        Files.createDirectories(rutaCompleta.getParent());

        // Guardar imagen comprimida
        Files.write(rutaCompleta, outputStream.toByteArray());

        log.info("Imagen almacenada con compresión exitosamente: {}", rutaRelativa);
        return rutaRelativa;
    }

    @Override
    public Path obtenerRutaCompleta(String rutaRelativa) {
        return Paths.get(uploadDir).resolve(rutaRelativa).normalize();
    }

    @Override
    public void eliminarArchivo(String rutaRelativa) throws IOException {
        Path rutaCompleta = obtenerRutaCompleta(rutaRelativa);
        Files.deleteIfExists(rutaCompleta);
        log.info("Archivo eliminado: {}", rutaRelativa);
    }

    @Override
    public boolean existeArchivo(String rutaRelativa) {
        Path rutaCompleta = obtenerRutaCompleta(rutaRelativa);
        return Files.exists(rutaCompleta);
    }

    @Override
    public String generarNombreArchivoUnico(String nombreOriginal) {
        String extension = StringUtils.getFilenameExtension(nombreOriginal);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return timestamp + "_" + uuid + (extension != null ? "." + extension : "");
    }

    @Override
    public String obtenerTipoMime(MultipartFile archivo) {
        String contentType = archivo.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            return contentType;
        }

        // Intentar determinar por extensión
        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo != null) {
            String extension = StringUtils.getFilenameExtension(nombreArchivo);
            if (extension != null) {
                switch (extension.toLowerCase()) {
                    case "jpg":
                    case "jpeg":
                        return "image/jpeg";
                    case "png":
                        return "image/png";
                    case "gif":
                        return "image/gif";
                    case "webp":
                        return "image/webp";
                    case "pdf":
                        return "application/pdf";
                    case "doc":
                        return "application/msword";
                    case "docx":
                        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    case "xls":
                        return "application/vnd.ms-excel";
                    case "xlsx":
                        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    default:
                        return "application/octet-stream";
                }
            }
        }

        return "application/octet-stream";
    }

    @Override
    public boolean esImagen(MultipartFile archivo) {
        String tipoMime = obtenerTipoMime(archivo);
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(tipoMime)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validarTamanoArchivo(MultipartFile archivo, long tamanoMaximo) {
        return archivo.getSize() <= tamanoMaximo;
    }

    // Métodos auxiliares privados

    private void validarArchivo(MultipartFile archivo) {
        if (archivo.isEmpty()) {
            throw new BusinessException("El archivo está vacío");
        }

        if (!validarTamanoArchivo(archivo, MAX_FILE_SIZE)) {
            throw new BusinessException("El archivo excede el tamaño máximo permitido de " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        String nombreArchivo = StringUtils.cleanPath(archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "");
        if (nombreArchivo.contains("..")) {
            throw new BusinessException("El nombre del archivo contiene una secuencia de ruta inválida: " + nombreArchivo);
        }
    }

    private String construirRutaRelativa(String categoria, Long sucursalId, String nombreArchivo) {
        StringBuilder ruta = new StringBuilder();
        
        if (categoria != null && !categoria.isEmpty()) {
            ruta.append(categoria.toLowerCase().replace(" ", "_")).append(File.separator);
        }
        
        if (sucursalId != null) {
            ruta.append("sucursal_").append(sucursalId).append(File.separator);
        }
        
        String anio = String.valueOf(LocalDateTime.now().getYear());
        String mes = String.format("%02d", LocalDateTime.now().getMonthValue());
        ruta.append(anio).append(File.separator).append(mes).append(File.separator);
        
        ruta.append(nombreArchivo);
        
        return ruta.toString();
    }

    private void comprimirImagen(BufferedImage imagen, ByteArrayOutputStream outputStream, int calidad, String formato) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formato);
        if (!writers.hasNext()) {
            throw new BusinessException("No se encontró un escritor para el formato: " + formato);
        }

        ImageWriter writer = writers.next();
        ImageWriteParam params = writer.getDefaultWriteParam();

        if (params.canWriteCompressed()) {
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(calidad / 100f);
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(imagen, null, null), params);
        } finally {
            writer.dispose();
        }
    }

    private String obtenerFormatoImagen(String nombreArchivo) {
        String extension = StringUtils.getFilenameExtension(nombreArchivo);
        if (extension == null) {
            return "jpg";
        }

        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "jpg";
            case "png":
                return "png";
            case "gif":
                return "gif";
            case "webp":
                return "webp";
            default:
                return "jpg";
        }
    }
}

