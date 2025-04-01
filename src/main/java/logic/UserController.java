package logic;

import java.util.LinkedList;

import data.*;
import entidades.*;

public class UserController {
	private UserDAO usuarioDAO;

	public UserController() {
		this.usuarioDAO = new UserDAO();
	}

	public Usuario login(Usuario u) {
		return usuarioDAO.login(u);
	}
	
	public Usuario getOneByEmail(String email) {
		return usuarioDAO.getOneUserByEmail(email);
	}

	public Usuario getOneByUserOrEmail(String user, String correo) {
		return usuarioDAO.getOneByUserOrEmail(user, correo);
	}

	public Usuario getOneById(int id) {
		return usuarioDAO.getById(id);
	}

	public LinkedList<Usuario> getAll() {
		return usuarioDAO.getAll();
	}

	public boolean updateUser(Usuario u) {
		return usuarioDAO.update(u);
	}
	
	public boolean updatePassword(int id, String clave) {
		return usuarioDAO.updatePassword(id, clave);
	}

	public boolean addUser(Usuario u) {
		return usuarioDAO.add(u);
	}

	public boolean deleteUser(int idUsuario) {
		return this.usuarioDAO.eliminarUsuario(idUsuario);
	}

}