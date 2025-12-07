package logic;

import data.RolDAO;
import entities.Rol;

import java.util.LinkedList;

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