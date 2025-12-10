package services;

import data.ReservaDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservasService {
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private static final Logger logger = LoggerFactory.getLogger(ReservasService.class);


    public void procesarReservasVencidas() {

        int cantidad = reservaDAO.marcarReservasVencidas();
        logger.info("Reservas marcadas como VENCIDAS: {}", cantidad);
    }


}
