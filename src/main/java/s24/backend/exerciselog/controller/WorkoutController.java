package s24.backend.exerciselog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import s24.backend.exerciselog.domain.*;
import s24.backend.exerciselog.dto.*;
import s24.backend.exerciselog.service.WorkoutService;
import s24.backend.exerciselog.util.SecurityUtils;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.*;

@Controller
public class WorkoutController {
    @Autowired
    private WorkoutService workoutService;

    @GetMapping("/workouts")
    public String getWorkoutsPage(Model model) {
        User user = SecurityUtils.getCurrentUser();

        List<WorkoutDto> workoutDtos = workoutService.getUserWorkouts(user);
        List<CompletedWorkoutDto> completedWorkoutDtos = workoutService.getUserCompletedWorkouts(user);
        List<PlannedExerciseLogDto> plannedExerciseLogDtos = workoutService.getUserPlannedExercises(user);

        model.addAttribute("workoutDtos", workoutDtos);
        model.addAttribute("completedWorkoutDtos", completedWorkoutDtos);
        model.addAttribute("plannedExerciseLogDtos", plannedExerciseLogDtos);

        WorkoutDto workoutDto = new WorkoutDto();
        model.addAttribute("workoutDto", workoutDto);
        return "workouts";
    }
    
    @PostMapping("/add-workout")
    public String addWorkout(@Valid @ModelAttribute WorkoutDto workoutDto, BindingResult result, Model model) {

        User user = SecurityUtils.getCurrentUser();

        // Check for validation errors
        if(result.hasErrors()) {
            List<WorkoutDto> workoutDtos = workoutService.getUserWorkouts(user);
            List<CompletedWorkoutDto> completedWorkoutDtos = workoutService.getUserCompletedWorkouts(user);
            List<PlannedExerciseLogDto> plannedExerciseLogDtos = workoutService.getUserPlannedExercises(user);

            model.addAttribute("workoutDtos", workoutDtos);
            model.addAttribute("completedWorkoutDtos", completedWorkoutDtos);
            model.addAttribute("plannedExerciseLogDtos", plannedExerciseLogDtos);
            return "workouts";
        }
        
        workoutService.addWorkout(workoutDto, user, result);

        // Check for new validation errors
        if(result.hasErrors()) {
            List<WorkoutDto> workoutDtos = workoutService.getUserWorkouts(user);
            List<CompletedWorkoutDto> completedWorkoutDtos = workoutService.getUserCompletedWorkouts(user);
            List<PlannedExerciseLogDto> plannedExerciseLogDtos = workoutService.getUserPlannedExercises(user);

            model.addAttribute("workoutDtos", workoutDtos);
            model.addAttribute("completedWorkoutDtos", completedWorkoutDtos);
            model.addAttribute("plannedExerciseLogDtos", plannedExerciseLogDtos);
            return "workouts";
        }
        return "redirect:/workouts";
    }

    @GetMapping("/workouts/start/{id}")
    public String startWorkout(@PathVariable Long id, Model model) {
        CompletedWorkoutDto completedWorkoutDto = workoutService.startWorkout(id);
        model.addAttribute("completedWorkoutDto", completedWorkoutDto);
        return "start-workout";
    }

    @PostMapping("/workouts/complete/{id}")
    public String completeWorkout(@PathVariable Long id, 
        @Valid @ModelAttribute CompletedWorkoutDto completedWorkoutDto, BindingResult result, Model model) {

        // Check for validation errors
        if(result.hasErrors()) {
            CompletedWorkoutDto freshDto = workoutService.startWorkout(id);
            model.addAttribute("completedWorkoutDto", freshDto);
            return "start-workout";
        }

        workoutService.completeWorkout(id, completedWorkoutDto, result);

        // Check for validation errors again
        if(result.hasErrors()) {
            CompletedWorkoutDto freshDto = workoutService.startWorkout(id);
            model.addAttribute("completedWorkoutDto", freshDto);
            return "start-workout";
        }
        return "redirect:/workouts";
    }

    @PostMapping("/workouts/delete-planned-workout/{id}")
    public String deletePlannedWorkout(@PathVariable Long id) {
        workoutService.deletePlannedWorkout(id);
        return "redirect:/workouts";
    }

    @PostMapping("/workouts/delete-completed-workout/{id}")
    public String deleteCompletedWorkout(@PathVariable Long id) {
        workoutService.deleteCompletedWorkout(id);
        return "redirect:/workouts";
    }
}
