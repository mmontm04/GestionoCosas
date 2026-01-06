package model.dao;
import model.entities.User;

public interface IUserDAO {
    User findByUsername(String username);
}