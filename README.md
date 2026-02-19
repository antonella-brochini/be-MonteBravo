# Monte Bravo – E-Commerce Backend (Java | Spring Boot)

Backend desarrollado en Java 17 + Spring Boot para una plataforma de comercio electrónico creada para un cliente del rubro vitivinícola.

El sistema fue diseñado para soportar operaciones reales de venta online, gestión de stock, procesamiento de pagos y administración interna, aplicando buenas prácticas de arquitectura y seguridad.

## 🔧 Tech Stack

Java 17

Spring Boot

Spring Security

JWT Authentication

OAuth2 (Google / Facebook)

Spring Data JPA

Hibernate

PostgreSQL

RESTful API

Maven

Docker

Deploy en Render

## 🏗 Arquitectura

Arquitectura en tres capas:

Controllers → Endpoints REST

Services → Lógica de negocio

Repositories → Persistencia con JPA

Principios aplicados:

Separación de responsabilidades

Inyección de dependencias

Manejo global de excepciones

DTO pattern

Validaciones con Bean Validation

Configuración por variables de entorno

## 🔐 Seguridad

Autenticación con JWT

Login social con OAuth2

Encriptación de contraseñas (BCrypt)

Control de roles (ADMIN / USER)

Protección de endpoints

Integración segura con MercadoPago

Envío de emails transaccionales con SendGrid

## 📦 Funcionalidades Backend
Usuarios

Registro y login

Login social

Recuperación de contraseña

Perfil de usuario

Historial de pedidos

Productos

CRUD completo

Gestión de stock

Alertas de stock bajo

Deshabilitación automática sin stock

Carrito y Compras

Gestión de carrito

Cálculo de totales

Integración con pasarela de pago

Confirmaciones por email

Administración

Panel administrativo

Gestión de administradores con permisos

Templates de email

Reportes:

Ventas por período

Productos más vendidos

Clientes frecuentes

Stock

Carritos abandonados

## 📊 Enfoque Técnico

Diseño orientado a escalabilidad

Preparado para auto-scaling en la nube

Persistencia optimizada con JPA

Queries eficientes

Manejo de transacciones

Testing manual de endpoints con Postman

Deploy automatizado con Docker

## ⚙️ Variables de Entorno
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
SENDGRID_API_KEY=
CLOUDINARY_URL=
MERCADOPAGO_ACCESS_TOKEN=
FACEBOOK_CLIENT_ID=
FACEBOOK_CLIENT_SECRET=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=


## 💼 Perfil Profesional

Este proyecto demuestra experiencia en:

Desarrollo de APIs REST en Spring Boot

Implementación de seguridad con Spring Security

Integración con servicios externos

Modelado de base de datos relacional

Arquitectura backend lista para producción
