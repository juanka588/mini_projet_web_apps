package controllers;

import models.Category;
import play.mvc.With;

@Check("admin")
@With(Secure.class)
@CRUD.For(Category.class)
public class Categories extends CRUD {

}
