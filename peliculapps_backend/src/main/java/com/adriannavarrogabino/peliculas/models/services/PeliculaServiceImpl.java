package com.adriannavarrogabino.peliculas.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adriannavarrogabino.peliculas.models.dao.IPeliculaDao;
import com.adriannavarrogabino.peliculas.models.entity.Pelicula;

@Service
public class PeliculaServiceImpl implements IPeliculaService {
	
	@Autowired
	private IPeliculaDao peliculaDao;

	@Override
	@Transactional(readOnly = true)
	public List<Pelicula> findAllOrderByFechaVista() {
		
		return peliculaDao.findAllOrderByFechaVista();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Pelicula> findAll() {
		
		return (List<Pelicula>) peliculaDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Pelicula findById(Long id) {
		
		return peliculaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Pelicula save(Pelicula pelicula) {
		
		return peliculaDao.save(pelicula);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		
		peliculaDao.deleteById(id);
	}

}
