package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;


import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.ejb.Stateless;

@Stateless
public class PasswordHashService {

    private static final int BCRYPT_COST_FACTOR = 12;

    public  String hashPassword(String password) {
        return new String(BCrypt.withDefaults().hash(BCRYPT_COST_FACTOR, password.toCharArray()));
    }

    public  boolean checkPassword(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray()).verified;
    }
}
