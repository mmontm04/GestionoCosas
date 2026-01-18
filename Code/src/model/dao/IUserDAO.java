package model.dao;
import java.util.List;

import model.entities.User;

public interface IUserDAO {
    public List<User> list();
    public boolean create(User u);
    public boolean delete(int id);
    User findByUsername(String username);
    boolean update(User u);
    User findById(int id);
}