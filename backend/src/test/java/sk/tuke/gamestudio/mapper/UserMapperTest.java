package sk.tuke.gamestudio.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import sk.tuke.gamestudio.dto.UserDTO;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.Role;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;
    private User userEntity;
    private UserDTO userDTO;
    private final LocalDateTime fixedTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);


    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        userEntity = new User();
        userEntity.setId(1L);
        userEntity.setUsername("testEntityUser");
        userEntity.setEmail("entity@example.com");
        userEntity.setPassword("entityPassword");
        userEntity.setRole(Role.USER);
        userEntity.setCreatedAt(fixedTime);

        userDTO = new UserDTO();
        userDTO.setId(2L);
        userDTO.setUsername("testDtoUser");
        userDTO.setEmail("dto@example.com");

        userDTO.setCreatedAt(fixedTime.plusHours(1));
    }

    @Test
    void shouldMapUserEntityToUserDto() {
        // When
        UserDTO mappedDto = userMapper.toUserDTO(userEntity);

        // Then
        assertThat(mappedDto).isNotNull();
        assertThat(mappedDto.getId()).isEqualTo(userEntity.getId());
        assertThat(mappedDto.getUsername()).isEqualTo(userEntity.getUsername());
        assertThat(mappedDto.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(mappedDto.getCreatedAt()).isEqualTo(userEntity.getCreatedAt());
    }

    @Test
    void shouldMapUserDtoToUserEntity() {
        // When
        User mappedEntity = userMapper.toUserEntity(userDTO);

        // Then
        assertThat(mappedEntity).isNotNull();
        assertThat(mappedEntity.getId()).isEqualTo(userDTO.getId());
        assertThat(mappedEntity.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(mappedEntity.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(mappedEntity.getCreatedAt()).isEqualTo(userDTO.getCreatedAt());

        assertThat(mappedEntity.getPassword()).isNull();

    }

    @Test
    void shouldReturnNullDtoWhenEntityIsNull() {
        // When
        UserDTO mappedDto = userMapper.toUserDTO(null);
        // Then
        assertThat(mappedDto).isNull();
    }

    @Test
    void shouldReturnNullEntityWhenDtoIsNull() {
        // When
        User mappedEntity = userMapper.toUserEntity(null);
        // Then
        assertThat(mappedEntity).isNull();
    }

}