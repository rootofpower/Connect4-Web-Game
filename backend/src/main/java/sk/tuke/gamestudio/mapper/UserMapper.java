package sk.tuke.gamestudio.mapper;

import org.mapstruct.Mapper;
import sk.tuke.gamestudio.dto.UserDTO;
import sk.tuke.gamestudio.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(User user);

    User toUserEntity(UserDTO userDTO);

}
