package com.monteBravo.be.Services.Impl;

import com.monteBravo.be.Repository.AdministradorRepository;
import com.monteBravo.be.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    AdministradorRepository administradorRepository;
    @Autowired
    ClienteRepository clienteRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return administradorRepository.findByNombreUsuarioActivo(username)
                .map(UserDetailsImpl::build) // Adaptación para Administrador
                .or(() -> clienteRepository.findByNombreUsuarioActivo(username)
                        .map(UserDetailsImpl::build)) // Adaptación para Cliente
                .orElseThrow(() -> new UsernameNotFoundException("Usuario: " + username + " no existe"));}




}
