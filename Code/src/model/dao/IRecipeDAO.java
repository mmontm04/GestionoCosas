package model.dao;

import model.entities.Recipe;
import java.util.List;

public interface IRecipeDAO {
    List<Recipe> listarTodas();
    boolean save(Recipe r);
    Recipe getRecetaCompleta(int id);
    boolean update(Recipe r);
    boolean delete(int id);
}
