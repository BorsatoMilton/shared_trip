package servlet;

import java.io.IOException;
import java.util.UUID;

import jakarta.mail.MessagingException;
import logic.UserController;
import resources.EnvioCorreo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entidades.Usuario;

@WebServlet("/enviarCorreo")
public class EnviarCorreo extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private final String username = System.getenv("EMAIL_USER");
    private final String password = System.getenv("EMAIL_PASS");
    private final String host = System.getenv("EMAIL_HOST");
    private final int port = Integer.parseInt(System.getenv("EMAIL_PORT")); 

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	String action = request.getParameter("action");
    	
    	try {
    		if("recuperarClave".equals(action)) {
    			recuperarClave(request);	
    	}
    		
    	}catch(Exception e){
    		System.out.println("Hubo un error");
    		e.printStackTrace();
    	}
    	

    }
    
    private void recuperarClave(HttpServletRequest request) {
        String email = request.getParameter("email");
    	UserController userCtrl = new UserController();
        Usuario user = userCtrl.getOneByEmail(email);
        
        if (user == null) {
            request.setAttribute("error", "No existe una cuenta asociada a este email");
            return;
        }
        
        try {
            String token = generarToken();
            guardarTokenEnUsuario(user, token);
            
            String recoveryLink = construirEnlaceRecuperacion(request, token);
            String subject = "Recuperación de Contraseña";
            String mensaje = "Haga clic en el siguiente enlace para restablecer su contraseña:\n" 
                           + recoveryLink 
                           + "\n\nEste enlace expirará en 1 hora.";
            
            if (enviarCorreo(user.getCorreo(), subject, mensaje)) {
                request.setAttribute("success", "Se ha enviado un correo con las instrucciones");
            } else {
                request.setAttribute("error", "Error al enviar el correo. Intente nuevamente.");
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Ocurrió un error inesperado");
            e.printStackTrace();
        }
    }
    
    private boolean enviarCorreo(String to, String subject, String content) {
        EnvioCorreo envioCorreo = new EnvioCorreo(username, password, host, port);
        try {
            envioCorreo.sendEmail(to, subject, content);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    
    }
    
    private String generarToken() {
        return UUID.randomUUID().toString();
    }

    private void guardarTokenEnUsuario(Usuario user, String token) {
       
        //user.setTokenRecuperacion(token);
        //user.setExpiracionToken(LocalDateTime.now().plusHours(1));
        new UserController().updateUser(user);
    }

    private String construirEnlaceRecuperacion(HttpServletRequest request, String token) {
        String baseUrl = request.getRequestURL().toString()
                          .replace(request.getServletPath(), "");
        return baseUrl + "/resetPassword?token=" + token;   //Despues ver como se va a manejar la url
    }
    
    
    
}
