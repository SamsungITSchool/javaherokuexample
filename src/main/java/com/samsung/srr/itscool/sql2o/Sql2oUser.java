package com.samsung.srr.itscool.sql2o;

import com.samsung.srr.itscool.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

/**
 * Created by raiym on 1/13/16.
 */
public class Sql2oUser {
    private Sql2o sql2o;

    public Sql2oUser(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public void createUser(User user) {
        try (Connection conn = sql2o.open()) {
            String hashed = BCrypt.hashpw(user.password, BCrypt.gensalt());
            conn.createQuery("INSERT INTO tbl_User(email, password, token) VALUES (:email, :password, :token)")
                    .addParameter("email", user.email)
                    .addParameter("password", hashed)
                    .addParameter("token", user.token)
                    .executeUpdate();
        }
    }

    public User findUserByEmail(String email) {
        try (Connection conn = sql2o.open()) {
            List<User> users = conn.createQuery("SELECT * FROM tbl_User WHERE email=:email")
                    .addParameter("email", email)
                    .executeAndFetch(User.class);
            if (users.size() == 0) {
                return null;
            }
            return users.get(0);
        }
    }
    public User findByToken(String token) {
        try (Connection conn = sql2o.open()) {
            List<User> users = conn.createQuery("SELECT * FROM tbl_User WHERE token=:token")
                    .addParameter("token", token)
                    .executeAndFetch(User.class);
            if (users.size() == 0) {
                return null;
            }
            return users.get(0);
        }
    }
}
