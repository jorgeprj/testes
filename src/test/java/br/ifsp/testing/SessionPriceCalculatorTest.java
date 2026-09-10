package br.ifsp.testing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class SessionPriceCalculatorTest {

    @ParameterizedTest
    @CsvSource({
            "4,0,8,0",       // R1
            "23,10,23,15",  // R2
            "22,0,8,0",     // R10
            "22,0,23,30"    // R4
    })
    @DisplayName("Should throw if hour is invalid")
    void shouldThrowIfHourIsInvalid(
            int beginHour,
            int beginMinute,
            int endHour,
            int endMinute) {

        LocalDateTime begin = LocalDateTime.of(
                LocalDate.now(),
                LocalTime.of(beginHour, beginMinute)
        );

        LocalDateTime end = LocalDateTime.of(
                LocalDate.now(),
                LocalTime.of(endHour, endMinute)
        );

        assertThatIllegalStateException().isThrownBy(() -> new SessionPriceCalculator(1.0, begin, end));
    }

    @ParameterizedTest
    @CsvSource({
            "5,0,5,1,0.8",       // R5, R13, R20
            "7,59,8,1,1.8",      // R6
            "8,0,8,1,1.0",       // R7, R14
            "20,59,21,1,1.0",    // R8
            "21,0,21,1,0.8",     // R9, R15
            "7,0,22,0,876.8"     // R10
    })
    @DisplayName("Should calculate workout cost")
    void shouldCalculateWorkoutCostBy(
            int beginHour,
            int beginMinute,
            int endHour,
            int endMinute,
            double expected) {

        LocalDateTime begin = LocalDateTime.of(
                LocalDate.now(),
                LocalTime.of(beginHour, beginMinute)
        );

        LocalDateTime end = LocalDateTime.of(
                LocalDate.now(),
                LocalTime.of(endHour, endMinute)
        );

        var sut = new SessionPriceCalculator(1.0, begin, end);

        double resultado = sut.checkout();

        assertThat(resultado).isEqualTo(expected);
    }

    @Test // R11, R12
    @DisplayName("Should not allow training overnight")
    void shouldNotAllowTrainingOvernight() {

        LocalDateTime begin = LocalDateTime.of(
                LocalDate.now(),
                LocalTime.of(14, 0)
        );

        LocalDateTime end = LocalDateTime.of(
                LocalDate.now().plusDays(1),
                LocalTime.of(16, 0)
        );

        var sut = new SessionPriceCalculator(1.0, begin, end);

        assertThatIllegalStateException()
                .isThrownBy(sut::checkout);
    }
}