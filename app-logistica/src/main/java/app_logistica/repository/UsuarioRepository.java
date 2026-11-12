package app_logistica.repository;
import app_logistica.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Usuario findByEmail(String email);

}
