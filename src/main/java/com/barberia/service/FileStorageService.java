package com.barberia.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {

    /**
     * Almacena un archivo y retorna la ruta relativa
     */
    String almacenarArchivo(MultipartFile archivo, String categoria, Long sucursalId) throws IOException;

    /**
     * Almacena un archivo con compresión (solo para imágenes)
     */
    String almacenarArchivoConCompresion(MultipartFile archivo, String categoria, Long sucursalId, int calidadCompresion) throws IOException;

    /**
     * Obtiene la ruta completa de un archivo
     */
    Path obtenerRutaCompleta(String rutaRelativa);

    /**
     * Elimina un archivo del sistema
     */
    void eliminarArchivo(String rutaRelativa) throws IOException;

    /**
     * Verifica si un archivo existe
     */
    boolean existeArchivo(String rutaRelativa);

    /**
     * Genera un nombre de archivo único
     */
    String generarNombreArchivoUnico(String nombreOriginal);

    /**
     * Obtiene el tipo MIME de un archivo
     */
    String obtenerTipoMime(MultipartFile archivo);

    /**
     * Valida que el archivo sea una imagen
     */
    boolean esImagen(MultipartFile archivo);

    /**
     * Valida el tamaño del archivo (en bytes)
     */
    boolean validarTamanoArchivo(MultipartFile archivo, long tamanoMaximo);
}

