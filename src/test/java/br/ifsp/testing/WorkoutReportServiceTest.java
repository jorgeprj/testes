package br.ifsp.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutReportServiceTest {

    @Mock
    private WorkoutRepository repository;

    @Mock
    private WorkoutSession session;

    @Mock
    private WorkoutSession session2;

    @Mock
    private WorkoutSession otherMemberSession;

    @Mock
    private Member member;

    @Mock
    private Member otherMember;

    private WorkoutReportService sut;

    private UUID memberId;
    private UUID otherMemberId;

    @BeforeEach
    void setUp() {
        sut = new WorkoutReportService(repository);

        memberId = UUID.randomUUID();
        otherMemberId = UUID.randomUUID();
    }

    @Test // R1
    @DisplayName("R1 - Should throw NullPointerException when member id is null")
    void shouldThrowWhenMemberIdIsNull() {

        assertThatNullPointerException()
                .isThrownBy(() -> sut.averageWorkoutPaidValue(null));
    }

    @Test // R2
    @DisplayName("R2 - Should return zero when there are no workout sessions")
    void shouldReturnZeroWhenThereAreNoSessions() {

        when(repository.findAll())
                .thenReturn(List.of());

        double result = sut.averageWorkoutPaidValue(memberId);

        assertThat(result).isEqualTo(0.0);
    }

    @Test // R3
    @DisplayName("R3 - Should return the workout value when there is one session")
    void shouldReturnValueWhenThereIsOneSession() {

        when(repository.findAll())
                .thenReturn(List.of(session));

        when(session.member())
                .thenReturn(member);

        when(member.uuid())
                .thenReturn(memberId);

        when(session.totalCost())
                .thenReturn(50.0);

        double result = sut.averageWorkoutPaidValue(memberId);

        assertThat(result).isEqualTo(50.0);
    }

    @Test // R4
    @DisplayName("R4 - Should calculate the average of multiple workout sessions")
    void shouldCalculateAverageOfMultipleSessions() {

        when(repository.findAll())
                .thenReturn(List.of(session, session2));

        when(session.member())
                .thenReturn(member);

        when(session2.member())
                .thenReturn(member);

        when(member.uuid())
                .thenReturn(memberId);

        when(session.totalCost())
                .thenReturn(50.0);

        when(session2.totalCost())
                .thenReturn(100.0);

        double result = sut.averageWorkoutPaidValue(memberId);

        assertThat(result).isEqualTo(75.0);
    }

    @Test // R5, R7
    @DisplayName("R5, R7 - Should consider only sessions belonging to the requested member")
    void shouldIgnoreSessionsFromOtherMembers() {

        when(repository.findAll())
                .thenReturn(List.of(session, otherMemberSession));

        when(session.member())
                .thenReturn(member);

        when(member.uuid())
                .thenReturn(memberId);

        when(session.totalCost())
                .thenReturn(100.0);

        when(otherMemberSession.member())
                .thenReturn(otherMember);

        when(otherMember.uuid())
                .thenReturn(otherMemberId);

        when(otherMemberSession.totalCost())
                .thenReturn(500.0);

        double result = sut.averageWorkoutPaidValue(memberId);

        assertThat(result).isEqualTo(100.0);
    }

    @Test // R6
    @DisplayName("R6 - Should accept zero as workout cost")
    void shouldAcceptZeroWorkoutCost() {

        when(repository.findAll())
                .thenReturn(List.of(session));

        when(session.member())
                .thenReturn(member);

        when(member.uuid())
                .thenReturn(memberId);

        when(session.totalCost())
                .thenReturn(0.0);

        double result = sut.averageWorkoutPaidValue(memberId);

        assertThat(result).isEqualTo(0.0);
    }
}
