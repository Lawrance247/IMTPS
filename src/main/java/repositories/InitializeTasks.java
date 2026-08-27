package repositories;

import java.time.LocalDateTime;
import java.util.List;

import app.App;
import models.Task;
import utils.Category;
import utils.Weights;



/**
 * Seeds {@code App.taskManager} with a fixed set of demo tasks.
 *
 * <p><b>Collaborators:</b> called once by {@code controllers.MainController.initialize()} on first run (when there are no existing tasks);
 * writes through {@code App.taskManager}.</p>
 */
public class InitializeTasks {


  /**
   * Temporary method for testing otherwise user adds tasks via addtask.
   *
   * <p>Populates {@code App.taskManager} with a fixed set of demo tasks spanning all four {@link Category} values, used only when the task
   * manager is empty on startup (see {@code MainController#initialize()}). This is seed data for development/demo purposes, not part of the app's
   * normal runtime behaviour.</p>
   *
   * @return the full list of tasks after seeding (via {@code App.taskManager.getAllTasks()})
   */
    public static List<Task> createTasks(){
          App.taskManager.createTask(
                  "Complete Database Queries",
                  "Finish the remaining SQL queries for the database assignment.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 1, 11, 0),
                  90,
                  Weights.HIGH,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Review Lecture Notes",
                  "Review notes from the latest computer science lecture.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 1, 15, 0),
                  60,
                  Weights.MEDIUM,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Study Algorithms",
                  "Revise sorting, searching, and graph algorithms.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 2, 10, 0),
                  120,
                  Weights.HIGH,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Complete Finance Exercises",
                  "Work through the assigned finance calculation exercises.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 2, 14, 0),
                  90,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Debug Java Project",
                  "Find and fix errors in the current Java project.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 3, 16, 0),
                  150,
                  Weights.HIGH,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Prepare Tutorial",
                  "Complete questions before the upcoming tutorial.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 3, 12, 0),
                  75,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Read Research Paper",
                  "Read and summarise the assigned research paper.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 4, 13, 0),
                  90,
                  Weights.LOW,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Update Project Documentation",
                  "Update documentation for the latest project changes.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 4, 18, 0),
                  60,
                  Weights.MEDIUM,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Practice Coding Questions",
                  "Solve programming questions under timed conditions.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 5, 10, 0),
                  120,
                  Weights.HIGH,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Organise Study Material",
                  "Organise lecture notes, assignments, and study resources.",
                  Category.ACADEMIC,
                  LocalDateTime.of(2026, 9, 6, 11, 0),
                  60,
                  Weights.LOW,
                  Weights.LOW
          );


          App.taskManager.createTask(
                  "Clean Bedroom",
                  "Clean and organise the bedroom.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 1, 18, 0),
                  60,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Buy Groceries",
                  "Purchase groceries and household food supplies.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 2, 17, 0),
                  75,
                  Weights.HIGH,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Call Family",
                  "Call family members and catch up with them.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 2, 20, 0),
                  30,
                  Weights.MEDIUM,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Prepare Dinner",
                  "Prepare dinner for the household.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 3, 18, 0),
                  90,
                  Weights.HIGH,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Wash Clothes",
                  "Do the weekly laundry and put clothes away.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 4, 9, 0),
                  120,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Clean Kitchen",
                  "Clean the kitchen and organise the cupboards.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 4, 16, 0),
                  75,
                  Weights.MEDIUM,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Help With Shopping",
                  "Help a family member complete the weekly shopping.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 5, 13, 0),
                  90,
                  Weights.LOW,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Visit Relative",
                  "Visit a relative and spend some time together.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 6, 14, 0),
                  180,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Organise Household Documents",
                  "Sort and organise important household documents.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 7, 11, 0),
                  90,
                  Weights.LOW,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Prepare Family Lunch",
                  "Prepare lunch for the family.",
                  Category.FAMILY,
                  LocalDateTime.of(2026, 9, 8, 12, 0),
                  90,
                  Weights.MEDIUM,
                  Weights.HIGH
          );


          App.taskManager.createTask(
                  "Upper Body Workout",
                  "Complete an upper body strength training session.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 1, 16, 30),
                  75,
                  Weights.HIGH,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Morning Run",
                  "Go for a short morning run.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 2, 7, 30),
                  45,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Stretching Session",
                  "Complete a full-body stretching and mobility routine.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 2, 19, 0),
                  30,
                  Weights.LOW,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Football Training",
                  "Attend football training and complete drills.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 3, 15, 0),
                  120,
                  Weights.HIGH,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Meal Preparation",
                  "Prepare healthy meals for the next few days.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 4, 17, 0),
                  120,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Lower Body Workout",
                  "Complete a lower body strength workout.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 5, 10, 0),
                  75,
                  Weights.HIGH,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Cycling Session",
                  "Go cycling for cardiovascular exercise.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 6, 9, 0),
                  90,
                  Weights.MEDIUM,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Recovery Session",
                  "Complete light stretching and recovery exercises.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 6, 19, 0),
                  45,
                  Weights.LOW,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Core Workout",
                  "Complete a focused abdominal and core workout.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 7, 17, 0),
                  45,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Long Walk",
                  "Take a long walk to stay active and relax.",
                  Category.HEALTH,
                  LocalDateTime.of(2026, 9, 8, 18, 0),
                  75,
                  Weights.LOW,
                  Weights.LOW
          );


          App.taskManager.createTask(
                  "Watch Movie",
                  "Relax and watch a movie.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 1, 20, 0),
                  150,
                  Weights.LOW,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Play Video Games",
                  "Spend some time playing video games.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 2, 21, 0),
                  120,
                  Weights.LOW,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Meet Friends",
                  "Meet friends and spend some time socialising.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 3, 19, 0),
                  180,
                  Weights.MEDIUM,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Read a Book",
                  "Read several chapters of a book.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 4, 20, 0),
                  90,
                  Weights.LOW,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Watch Football",
                  "Watch a football match in the evening.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 5, 20, 0),
                  120,
                  Weights.LOW,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Visit Coffee Shop",
                  "Spend some time relaxing at a coffee shop.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 6, 14, 0),
                  90,
                  Weights.LOW,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Go Hiking",
                  "Spend the afternoon hiking and exploring outdoors.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 7, 10, 0),
                  180,
                  Weights.MEDIUM,
                  Weights.HIGH
          );

          App.taskManager.createTask(
                  "Play Pool",
                  "Play pool with friends.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 7, 19, 0),
                  120,
                  Weights.LOW,
                  Weights.LOW
          );

          App.taskManager.createTask(
                  "Photography Walk",
                  "Walk around the city and practise photography.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 8, 15, 0),
                  120,
                  Weights.LOW,
                  Weights.MEDIUM
          );

          App.taskManager.createTask(
                  "Try New Restaurant",
                  "Have dinner at a restaurant you have never visited.",
                  Category.LEISURE,
                  LocalDateTime.of(2026, 9, 8, 19, 0),
                  120,
                  Weights.LOW,
                  Weights.LOW
          );

          return App.taskManager.getAllTasks();
    }
}
