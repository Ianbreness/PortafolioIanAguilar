package Tienda_Ian.service;

import Tienda_Ian.domain.Usuario;
import jakarta.mail.MessagingException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RegistroService {

    private final CorreoService correoService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    @Value("${servidor.http:localhost}")
    private String servidor;

    public RegistroService(CorreoService correoService,
                           UsuarioService usuarioService,
                           MessageSource messageSource) {
        this.correoService = correoService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    // Usado en el enlace del correo para activar
    public Model activar(Model model, String username, String clave) {
        Optional<Usuario> usuario = usuarioService.getUsuarioPorUsernameYPassword(username, clave);
        if (!usuario.isEmpty()) {
            model.addAttribute("usuario", usuario.get());
        } else {
            model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, Locale.getDefault()));
            model.addAttribute("mensaje", messageSource.getMessage("registro.activar.error", null, Locale.getDefault()));
        }
        return model;
    }

    // Activa definitivamente el usuario en el sistema
    public void activar(Usuario usuario, MultipartFile imagenFile) {
        usuario.setActivo(true);
        usuarioService.save(usuario, imagenFile, true);
    }

    // Genera clave temporal para el enlace de activación
    private String demoClave() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // Crea el usuario (inactivo) y envía correo de activación
    public Model crearUsuario(Model model, Usuario usuario) throws MessagingException {
        String mensaje;
        try {
            String clave = demoClave();
            usuario.setPassword(clave);
            usuario.setActivo(false);
            usuarioService.save(usuario, null, false);

            String contenido = String.format(
                    messageSource.getMessage("registro.correo.activar", null, Locale.getDefault()),
                    usuario.getNombre(),
                    usuario.getApellidos(),
                    servidor,
                    usuario.getUsername(),
                    clave
            );

            correoService.enviarCorreoHtml(
                    usuario.getCorreo(),
                    messageSource.getMessage("registro.mensaje.activacion", null, Locale.getDefault()),
                    contenido
            );

            mensaje = String.format(
                    messageSource.getMessage("registro.mensaje.activacion.ok", null, Locale.getDefault()),
                    usuario.getCorreo()
            );

        } catch (Exception e) {
            mensaje = String.format(
                    messageSource.getMessage("registro.mensaje.usuario.o.correo", null, Locale.getDefault()),
                    usuario.getUsername(),
                    usuario.getCorreo()
            );
        }
        model.addAttribute("mensaje", mensaje);
        return model;
    }

    // Permite recordar contraseña
    public Model recordarUsuario(Model model, Usuario usuario) throws MessagingException {
        String mensaje;
        try {
            Optional<Usuario> usuarioOpt = usuarioService.getUsuarioPorUsernameOCorreo(
                    usuario.getUsername(), usuario.getCorreo());

            if (usuarioOpt.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Usuario u = usuarioOpt.get();
            String clave = demoClave();
            u.setPassword(clave);
            u.setActivo(false);
            usuarioService.save(u, null, false);

            String contenido = String.format(
                    messageSource.getMessage("registro.correo.recordar", null, Locale.getDefault()),
                    u.getNombre(),
                    u.getApellidos(),
                    servidor,
                    u.getUsername(),
                    clave
            );

            correoService.enviarCorreoHtml(
                    u.getCorreo(),
                    messageSource.getMessage("registro.mensaje.recordar", null, Locale.getDefault()),
                    contenido
            );

            mensaje = String.format(
                    messageSource.getMessage("registro.mensaje.recordar.ok", null, Locale.getDefault()),
                    u.getCorreo()
            );

        } catch (Exception e) {
            mensaje = String.format(
                    messageSource.getMessage("registro.mensaje.usuario.o.correo", null, Locale.getDefault()),
                    usuario.getUsername(),
                    usuario.getCorreo()
            );
        }
        model.addAttribute("mensaje", mensaje);
        return model;
    }
}