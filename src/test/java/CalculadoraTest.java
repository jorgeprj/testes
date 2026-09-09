import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraTest {

    @Test
    void deveSomarDoisNumeros() {

        // Arrange
        Calculadora calculadora = new Calculadora();

        // Act
        int resultado = calculadora.somar(2, 3);

        // Assert
        assertThat(resultado).isEqualTo(5);
    }
}