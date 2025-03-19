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

	public Usuario getOneByUserOrEmail(String user, String correo) {
		return usuarioDAO.getOneByUserOrEmail(user, correo);
	}

	public Usuario getOneById(int id) {
		return usuarioDAO.getById(id);
	}

	public LinkedList<Usuario> getAll() {
		return usuarioDAO.getAll();
	}

	public void updateUser(Usuario u, int id) {
		usuarioDAO.update(u, id);
	}

	public void addUser(Usuario u) {
		usuarioDAO.add(u);
	}

	public boolean deleteUser(int idUsuario) {
		return this.usuarioDAO.eliminarUsuario(idUsuario);
	}

}