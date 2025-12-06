package logic;

import java.util.LinkedList;

import data.RolDAO;
import entities.Rol;

public class RolController {

    private final RolDAO rolDAO;


    public LinkedList<Rol> getAll() {
        return rolDAO.getAll();
    }

    public RolController() {
        super();
        this.rolDAO = new RolDAO();
    }

}