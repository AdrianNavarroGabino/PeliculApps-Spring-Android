package com.adriannavarrogabino.peliculas.models.services;

import java.util.List;

import com.adriannavarrogabino.peliculas.models.entity.Pelicula;

public interface IPeliculaService {
	
	public List<Pelicula> findAllOrderByFechaVista();
	
	public List<Pelicula> findAll();
	
	public Pelicula findById(Long id);
	
	public Pelicula save(Pelicula pelicula);
	
	public void deleteById(Long id);

}
