package com.barberia.service.impl;

import com.barberia.dto.ConfiguracionSistemaRequest;
import com.barberia.dto.ConfiguracionSistemaResponse;
import com.barberia.entity.ConfiguracionSistema;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.ConfiguracionSistemaRepository;
import com.barberia.service.ConfiguracionSistemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfiguracionSistemaServiceImpl implements ConfiguracionSistemaService {

    private final ConfiguracionSistemaRepository configuracionSistemaRepository;
    private final JavaMailSender mailSender;

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSistemaResponse> listarTodas() {
        return configuracionSistemaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSistemaResponse> listarEditables() {
        return configuracionSistemaRepository.findByEditableTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSistemaResponse> listarPorCategoria(String categoria) {
        return configuracionSistemaRepository.findByCategoria(categoria).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionSistemaResponse obtenerPorId(Long id) {
        ConfiguracionSistema config = configuracionSistemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración", "id", id));
        return mapToResponse(config);
    }

    @Override
    @Transactional
    public ConfiguracionSistemaResponse crear(ConfiguracionSistemaRequest request) {
        ConfiguracionSistema config = new ConfiguracionSistema();
        aplicarDatos(config, request);
        config = configuracionSistemaRepository.save(config);
        return mapToResponse(config);
    }

    @Override
    @Transactional
    public ConfiguracionSistemaResponse actualizar(Long id, ConfiguracionSistemaRequest request) {
        ConfiguracionSistema config = configuracionSistemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración", "id", id));

        if (Boolean.FALSE.equals(config.getEditable())) {
            throw new IllegalStateException("Esta configuración no es editable");
        }

        aplicarDatos(config, request);
        config = configuracionSistemaRepository.save(config);
        return mapToResponse(config);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ConfiguracionSistema config = configuracionSistemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración", "id", id));

        configuracionSistemaRepository.delete(config);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> obtenerConfiguracionPorCategoria(String categoria) {
        log.info("Obteniendo configuración por categoría: {}", categoria);
        List<ConfiguracionSistema> configuraciones = configuracionSistemaRepository.findByCategoria(categoria);
        Map<String, String> resultado = new HashMap<>();
        
        for (ConfiguracionSistema config : configuraciones) {
            resultado.put(config.getClave(), config.getValor());
        }
        
        return resultado;
    }

    @Override
    @Transactional
    public void guardarConfiguracionSMTP(Map<String, String> smtpConfig) {
        log.info("Guardando configuración SMTP");
        
        for (Map.Entry<String, String> entry : smtpConfig.entrySet()) {
            String clave = entry.getKey();
            String valor = entry.getValue();
            
            ConfiguracionSistema config = configuracionSistemaRepository.findByClave(clave)
                    .orElse(new ConfiguracionSistema());
            
            config.setClave(clave);
            config.setValor(valor);
            config.setCategoria("SMTP");
            config.setTipo("STRING");
            config.setEditable(true);
            
            configuracionSistemaRepository.save(config);
        }
        
        log.info("Configuración SMTP guardada exitosamente");
    }

    @Override
    public boolean probarConfiguracionSMTP(String emailDestino) {
        log.info("Probando configuración SMTP enviando correo a: {}", emailDestino);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDestino);
            message.setSubject("Prueba de configuración SMTP - BarberiaApp");
            message.setText("Este es un correo de prueba para verificar la configuración SMTP de BarberiaApp.");
            
            mailSender.send(message);
            log.info("Correo de prueba enviado exitosamente");
            return true;
        } catch (Exception e) {
            log.error("Error al enviar correo de prueba: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String obtenerValorPorClave(String clave) {
        log.info("Obteniendo valor por clave: {}", clave);
        return configuracionSistemaRepository.findByClave(clave)
                .map(ConfiguracionSistema::getValor)
                .orElse(null);
    }

    @Override
    @Transactional
    public void actualizarValorPorClave(String clave, String valor) {
        log.info("Actualizando valor para clave: {}", clave);
        
        ConfiguracionSistema config = configuracionSistemaRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración", "clave", clave));
        
        if (Boolean.FALSE.equals(config.getEditable())) {
            throw new IllegalStateException("Esta configuración no es editable");
        }
        
        config.setValor(valor);
        configuracionSistemaRepository.save(config);
        log.info("Valor actualizado exitosamente");
    }

    private void aplicarDatos(ConfiguracionSistema config, ConfiguracionSistemaRequest request) {
        config.setClave(request.getClave());
        config.setValor(request.getValor());
        config.setTipo(request.getTipo());
        config.setDescripcion(request.getDescripcion());
        config.setCategoria(request.getCategoria());
        config.setEditable(request.getEditable() != null ? request.getEditable() : Boolean.TRUE);
    }

    private ConfiguracionSistemaResponse mapToResponse(ConfiguracionSistema config) {
        return ConfiguracionSistemaResponse.builder()
                .id(config.getId())
                .clave(config.getClave())
                .valor(config.getValor())
                .tipo(config.getTipo())
                .descripcion(config.getDescripcion())
                .categoria(config.getCategoria())
                .editable(config.getEditable())
                .build();
    }
}


