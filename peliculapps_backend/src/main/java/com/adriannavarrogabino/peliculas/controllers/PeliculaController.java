package com.adriannavarrogabino.peliculas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.adriannavarrogabino.peliculas.models.entity.Pelicula;
import com.adriannavarrogabino.peliculas.models.services.IPeliculaService;

@RestController
public class PeliculaController {

	@Autowired
	private IPeliculaService peliculaService;
	
	@GetMapping("/peliculas")
	@ResponseStatus(value = HttpStatus.OK)
	public List<Pelicula> verTodas()
	{
		return peliculaService.findAllOrderByFechaVista();
	}
	
	@GetMapping("/peliculas/{id}")
	@ResponseStatus(value = HttpStatus.OK)
	public Pelicula ver(@PathVariable Long id)
	{
		return peliculaService.findById(id);
	}
	
	@GetMapping("/peliculas/length")
	public int getLength()
	{
		return peliculaService.findAll().size();
	}
	
	@PostMapping("/peliculas")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Pelicula create(@RequestBody Pelicula pelicula)
	{
		return peliculaService.save(pelicula);
	}
	
	@PutMapping("/peliculas/{id}")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Pelicula update(@RequestBody Pelicula pelicula, @PathVariable Long id)
	{
		Pelicula peliculaAntigua = peliculaService.findById(id);
		
		peliculaAntigua.setTitulo(pelicula.getTitulo());
		peliculaAntigua.setResumen(pelicula.getResumen());
		peliculaAntigua.setFechaVista(pelicula.getFechaVista());
		
		return peliculaService.save(peliculaAntigua);
	}
	
	@DeleteMapping("/peliculas/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id)
	{
		peliculaService.deleteById(id);
	}
}
