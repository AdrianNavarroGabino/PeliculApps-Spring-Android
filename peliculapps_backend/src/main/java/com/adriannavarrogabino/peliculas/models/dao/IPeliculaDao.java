package com.adriannavarrogabino.peliculas.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.adriannavarrogabino.peliculas.models.entity.Pelicula;

public interface IPeliculaDao extends CrudRepository<Pelicula, Long> {
	
	@Query(value = "select * from peliculas order by fecha_vista desc", nativeQuery = true)
	public List<Pelicula> findAllOrderByFechaVista();

}
