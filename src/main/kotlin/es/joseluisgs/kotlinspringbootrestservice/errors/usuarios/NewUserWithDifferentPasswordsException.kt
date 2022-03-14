package es.joseluisgs.kotlinspringbootrestservice.errors.usuarios

class NewUserWithDifferentPasswordsException :
    RuntimeException("Las contraseñas no coinciden")