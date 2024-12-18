package s24.backend.exerciselog.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import s24.backend.exerciselog.domain.*;
import s24.backend.exerciselog.dto.UserRegistrationDto;
import s24.backend.exerciselog.exception.ResourceNotFoundException;
import s24.backend.exerciselog.mapper.UserMapper;
import s24.backend.exerciselog.repository.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;



@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto userDto, BindingResult result, Model model) {
        if(!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Passwords do not match");
        }
        if(userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username is already taken");
        }
        if(userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "An account with this email is already registered");
        }
        if(result.hasErrors()) {
            return "register";
        }

        Role roleUser = roleRepository.findByName("USER").orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        User newUser = userMapper.toEntity(userDto);
        newUser.setPassword(encodedPassword);
        newUser.setRole(roleUser);

        userRepository.save(newUser);
        return "redirect:/login";
    }
    
}
