package utils;

import java.util.UUID;

public class Generators {

    public String generarToken() {
        String token = UUID.randomUUID().toString();
        return token;
    }
}
