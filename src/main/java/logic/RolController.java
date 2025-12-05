package logic;

import java.util.LinkedList;

import data.RolDAO;
import entities.Rol;

public class RolController {

    private final RolDAO rolDAO;

    public Rol getOne(int id_rol) {
        return rolDAO.getById(id_rol);
    }

    public LinkedList<Rol> getAll() {
        return rolDAO.getAll();
    }

    public RolController() {
        super();
        this.rolDAO = new RolDAO();
    }

}