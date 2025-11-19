package logic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import data.ReservaDAO;
import entidades.Reserva;
import entidades.Usuario;
import entidades.Viaje;

public class ReservaController {
	private ReservaDAO reservaDAO;
	
	public ReservaController() {
		this.reservaDAO = new ReservaDAO();
	}
	
	public void nuevaReserva(Viaje viaje, int cantPasajeros, int idUsuario) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = new Date();
		String fechaString = sdf.format(fecha);
		
		Reserva r = new Reserva(fechaString, cantPasajeros, false, viaje, idUsuario, "EN PROCESO", 3);
		
		this.reservaDAO.add(r);
	}
	
	public LinkedList<Reserva> getReservasUsuario(Usuario u){
		LinkedList<Reserva> reservas = this.reservaDAO.getByUser(u);
		return reservas;
	}
	
	
	public Reserva getOne(int id) {
		return this.reservaDAO.getByReserva(id);
	}
	
	public void actualizarEstado(int idReserva, String nuevoEstado){
		this.reservaDAO.actualizarEstado(idReserva, nuevoEstado);
		
	}
	
	public void updateEntity(Reserva reserva, int idReserva) {
		this.reservaDAO.update(reserva, idReserva);
	}
	
	public boolean cancelar(int idReserva) {
		return this.reservaDAO.cancelarReserva(idReserva);
	}
	
	public int obtenerCantidad(int idReserva) {
		return this.reservaDAO.obtenerCantidad(idReserva);
	}
}