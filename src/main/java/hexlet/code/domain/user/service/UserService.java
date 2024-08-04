package hexlet.code.domain.user.service;

import hexlet.code.domain.user.dto.UserCreateDTO;
import hexlet.code.domain.user.dto.UserDTO;
import hexlet.code.domain.user.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.domain.user.mapper.UserMapper;
import hexlet.code.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAll() {
        return userRepository.findAll()
                .stream().map(u -> userMapper.map(u)).toList();
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO userData) {
        var user = userMapper.map(userData);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO update(Long id, UserUpdateDTO userData) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.update(userData, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void destroy(Long id) {
        userRepository.deleteById(id);
    }
}
