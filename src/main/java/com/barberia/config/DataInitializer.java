package com.barberia.config;

import com.barberia.entity.Permiso;
import com.barberia.entity.Rol;
import com.barberia.entity.Usuario;
import com.barberia.repository.PermisoRepository;
import com.barberia.repository.RolRepository;
import com.barberia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (permisoRepository.count() == 0) {
            log.info("Inicializando permisos del sistema...");
            crearPermisos();
        }

        if (rolRepository.count() == 0) {
            log.info("Inicializando roles del sistema...");
            crearRoles();
        }

        if (usuarioRepository.count() == 0) {
            log.info("Creando usuario administrador por defecto...");
            crearUsuarioAdmin();
        }

        log.info("Inicialización de datos completada");
    }

    private void crearPermisos() {
        List<Permiso> permisos = Arrays.asList(
                // Permisos de Usuario
                crearPermiso("USUARIO_VER", "Ver usuarios", Permiso.TipoPermiso.VISTA),
                crearPermiso("USUARIO_CREAR", "Crear usuarios", Permiso.TipoPermiso.ACCION),
                crearPermiso("USUARIO_EDITAR", "Editar usuarios", Permiso.TipoPermiso.ACCION),
                crearPermiso("USUARIO_ELIMINAR", "Eliminar usuarios", Permiso.TipoPermiso.ACCION),

                // Permisos de Cliente
                crearPermiso("CLIENTE_VER", "Ver clientes", Permiso.TipoPermiso.VISTA),
                crearPermiso("CLIENTE_CREAR", "Crear clientes", Permiso.TipoPermiso.ACCION),
                crearPermiso("CLIENTE_EDITAR", "Editar clientes", Permiso.TipoPermiso.ACCION),
                crearPermiso("CLIENTE_ELIMINAR", "Eliminar clientes", Permiso.TipoPermiso.ACCION),

                // Permisos de Rol
                crearPermiso("ROL_VER", "Ver roles", Permiso.TipoPermiso.VISTA),
                crearPermiso("ROL_CREAR", "Crear roles", Permiso.TipoPermiso.ACCION),
                crearPermiso("ROL_EDITAR", "Editar roles", Permiso.TipoPermiso.ACCION),
                crearPermiso("ROL_ELIMINAR", "Eliminar roles", Permiso.TipoPermiso.ACCION),

                // Permisos de Sucursal
                crearPermiso("SUCURSAL_VER", "Ver sucursales", Permiso.TipoPermiso.VISTA),
                crearPermiso("SUCURSAL_CREAR", "Crear sucursales", Permiso.TipoPermiso.ACCION),
                crearPermiso("SUCURSAL_EDITAR", "Editar sucursales", Permiso.TipoPermiso.ACCION),
                crearPermiso("SUCURSAL_ELIMINAR", "Eliminar sucursales", Permiso.TipoPermiso.ACCION),

                // Permisos de Producto
                crearPermiso("PRODUCTO_VER", "Ver productos", Permiso.TipoPermiso.VISTA),
                crearPermiso("PRODUCTO_CREAR", "Crear productos", Permiso.TipoPermiso.ACCION),
                crearPermiso("PRODUCTO_EDITAR", "Editar productos", Permiso.TipoPermiso.ACCION),
                crearPermiso("PRODUCTO_ELIMINAR", "Eliminar productos", Permiso.TipoPermiso.ACCION),

                // Permisos de Venta
                crearPermiso("VENTA_VER", "Ver ventas", Permiso.TipoPermiso.VISTA),
                crearPermiso("VENTA_CREAR", "Crear ventas", Permiso.TipoPermiso.ACCION),
                crearPermiso("VENTA_EDITAR", "Editar ventas", Permiso.TipoPermiso.ACCION),
                crearPermiso("VENTA_ELIMINAR", "Eliminar ventas", Permiso.TipoPermiso.ACCION),

                // Permisos de Contabilidad
                crearPermiso("CONTABILIDAD_VER", "Ver contabilidad", Permiso.TipoPermiso.VISTA),
                crearPermiso("CONTABILIDAD_REPORTES", "Generar reportes", Permiso.TipoPermiso.ACCION),

                // Permisos de Configuración
                crearPermiso("CONFIG_VER", "Ver configuración", Permiso.TipoPermiso.VISTA),
                crearPermiso("CONFIG_EDITAR", "Editar configuración", Permiso.TipoPermiso.ACCION)
        );

        permisoRepository.saveAll(permisos);
        log.info("Permisos creados: {}", permisos.size());
    }

    private Permiso crearPermiso(String codigo, String nombre, Permiso.TipoPermiso tipo) {
        Permiso permiso = new Permiso();
        permiso.setCodigo(codigo);
        permiso.setNombre(nombre);
        permiso.setDescripcion(nombre);
        permiso.setTipo(tipo);
        return permiso;
    }

    private void crearRoles() {
        // Rol Administrador con todos los permisos
        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("Administrador");
        rolAdmin.setCodigo("ADMIN");
        rolAdmin.setDescripcion("Acceso total al sistema");
        rolAdmin.setPermisos(new HashSet<>(permisoRepository.findAll()));
        rolRepository.save(rolAdmin);

        // Rol Usuario básico
        Rol rolUsuario = new Rol();
        rolUsuario.setNombre("Usuario");
        rolUsuario.setCodigo("USUARIO");
        rolUsuario.setDescripcion("Usuario básico del sistema");
        Set<Permiso> permisosUsuario = new HashSet<>();
        // Usuarios básicos pueden ver clientes y productos
        permisoRepository.findByCodigo("CLIENTE_VER").ifPresent(permisosUsuario::add);
        permisoRepository.findByCodigo("VENTA_VER").ifPresent(permisosUsuario::add);
        permisoRepository.findByCodigo("PRODUCTO_VER").ifPresent(permisosUsuario::add);
        rolUsuario.setPermisos(permisosUsuario);
        rolRepository.save(rolUsuario);

        // Rol Vendedor
        Rol rolVendedor = new Rol();
        rolVendedor.setNombre("Vendedor");
        rolVendedor.setCodigo("VENDEDOR");
        rolVendedor.setDescripcion("Gestión de ventas");
        Set<Permiso> permisosVendedor = new HashSet<>();
        permisoRepository.findByCodigo("VENTA_VER").ifPresent(permisosVendedor::add);
        permisoRepository.findByCodigo("VENTA_CREAR").ifPresent(permisosVendedor::add);
        permisoRepository.findByCodigo("CLIENTE_VER").ifPresent(permisosVendedor::add);
        permisoRepository.findByCodigo("PRODUCTO_VER").ifPresent(permisosVendedor::add);
        rolVendedor.setPermisos(permisosVendedor);
        rolRepository.save(rolVendedor);

        // Rol Trabajador (para colaboradores que atienden clientes)
        Rol rolTrabajador = new Rol();
        rolTrabajador.setNombre("Trabajador");
        rolTrabajador.setCodigo("TRABAJADOR");
        rolTrabajador.setDescripcion("Trabajador que presta servicios y genera comisiones");
        Set<Permiso> permisosTrabajador = new HashSet<>();
        permisoRepository.findByCodigo("VENTA_VER").ifPresent(permisosTrabajador::add);
        permisoRepository.findByCodigo("VENTA_CREAR").ifPresent(permisosTrabajador::add);
        permisoRepository.findByCodigo("PRODUCTO_VER").ifPresent(permisosTrabajador::add);
        rolTrabajador.setPermisos(permisosTrabajador);
        rolRepository.save(rolTrabajador);

        log.info("Roles creados: ADMIN, USUARIO, VENDEDOR, TRABAJADOR");
    }

    private void crearUsuarioAdmin() {
        Usuario admin = new Usuario();
        admin.setEmail("admin@barberiapp.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setRut("11111111-1");
        admin.setActive(true);

        Rol rolAdmin = rolRepository.findByCodigo("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
        admin.setRoles(Set.of(rolAdmin));

        usuarioRepository.save(admin);
        log.info("Usuario administrador creado: admin@barberiapp.com / admin123");

        // Crear también un usuario de ejemplo con rol TRABAJADOR para probar ventas
        if (!usuarioRepository.existsByEmail("trabajador@barberiapp.com")) {
            crearUsuarioTrabajadorDemo();
        }
    }

    private void crearUsuarioTrabajadorDemo() {
        Usuario trabajador = new Usuario();
        trabajador.setEmail("trabajador@barberiapp.com");
        trabajador.setPassword(passwordEncoder.encode("trabajador123"));
        trabajador.setNombre("Trabajador");
        trabajador.setApellido("Demo");
        trabajador.setRut("22222222-2");
        trabajador.setTelefono("+56 9 0000 0000");
        trabajador.setActive(true);
        trabajador.setCuentaBloqueada(false);
        trabajador.setPorcentajeComision(15.0);

        Rol rolTrabajador = rolRepository.findByCodigo("TRABAJADOR")
                .orElseThrow(() -> new RuntimeException("Rol TRABAJADOR no encontrado"));
        trabajador.setRoles(Set.of(rolTrabajador));

        usuarioRepository.save(trabajador);
        log.info("Usuario trabajador demo creado: trabajador@barberiapp.com / trabajador123");
    }
}

