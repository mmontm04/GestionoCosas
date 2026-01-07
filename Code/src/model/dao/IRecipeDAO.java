package model.dao;

import model.entities.Recipe;
import java.util.List;

public interface IRecipeDAO {
    List<Recipe> listarTodas();
    boolean save(Recipe r);
    boolean delate(int id);
}