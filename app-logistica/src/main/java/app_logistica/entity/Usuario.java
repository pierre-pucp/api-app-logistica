package app_logistica.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
@Entity 
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Integer idusuario;

    @Column(name = "nombre", nullable = false, length = 20)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 20, message = "El nombre no debe exceder los 20 caracteres")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "El nombre contiene caracteres no válidos")
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 20)
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 20, message = "El apellido no debe exceder los 20 caracteres")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "El apellido contiene caracteres no válidos")
    private String apellido;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    @Size(max = 150, message = "El correo electrónico no debe exceder los 150 caracteres")
    private String email;

    @Column(name = "pwd", nullable = false, length = 255)
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "La contraseña no debe exceder los 255 caracteres")
    private String pwd;

    @Column(name = "activo")
    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    @ManyToOne 
    @JoinColumn(name = "idrol", nullable = false)
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
    
}
