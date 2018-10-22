 package edu.cnm.deepdive.craps.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

 /**
  * This is the main class for the model that sets up the framework for the game.
  */
 public class Game {

   private final Object lock = new Object();

   private State state;
   private int point;
   private Random rng;
   private List<Roll> rolls;
   private int wins;
   private int losses;

   /**
    * our random number generator.
    * @param rng random number generator.
    */
   public Game(Random rng) {
     this.rng = rng;
     rolls = new LinkedList<>();
     wins = 0;
     losses = 0;
   }

   /**
    * Resets the come out for the start of a game.
    */
   public void reset() {
     state = State.COME_OUT;
     point = 0;
     synchronized (lock) {
       rolls.clear();
     }
   }

   private State roll() {
     int[] dice = {
        1 + rng.nextInt(6),
        1 + rng.nextInt(6)
     };
     int total = dice[0] + dice[1];
     State state = this.state.roll(total, point);
     if (this.state == State.COME_OUT && state == State.POINT) {
       point = total;
     }
     this.state = state;
     synchronized (lock) {
       rolls.add(new Roll(dice,state));
     }
     return state;
    }

   /**
    * Rolls the dice and increments wins and losses.
    * @return win or loss at the end of game.
    */
    public State play() {
     reset();
     while (state != State.WIN && state != State.LOSS) {
       roll();
     }
     if (state == State.WIN) {
       wins++;
     } else {
       losses++;
     }
     return state;
    }

   /**
    *Getter for State.
    * @return state of game
    */
   public State getState() {
     return state;
   }

   /**
    *Getter for Rolls.
    * @return the rolls
    */
   public List<Roll> getRolls() {
     synchronized (lock) {
       return new LinkedList<>(rolls);
     }
   }

   /**
    *Getter for wins.
    * @return wins
    */
   public int getWins() {
     return wins;
   }

   /**
    *Getter for losses.
    * @return losses
    */
   public int getLosses() {
     return losses;
   }

   /**
    *rolls dice
    */
   public static class Roll {

     private final int[] dice;
     private final State state;

     private Roll(int[] dice, State state) {
       this.dice = Arrays.copyOf(dice, 2);
       this.state = state;
     }

     /**
      *Getter for dice
      * @return two dice ints
      */
     public int[] getDice() {
       return Arrays.copyOf(dice, 2);
     }

     /**
      *getter for state.
      * @return current state.
      */
     public State getState() {
       return state;
     }

     @Override
     public String toString() {
       return String.format("%s %s%n", Arrays.toString(dice), state);
     }
   }

   /**
    * Current state of the game.
    */
   public enum State {

     COME_OUT {
       @Override
       public State roll(int total, int point) {
         switch (total) {
           case 2:
           case 3:
           case 12:
             return LOSS;
           case 7:
           case 11:
             return WIN;
           default:
             return POINT;
         }
       }
     },
     WIN,
     LOSS,
     POINT {
       @Override
       public State roll(int total, int point) {
         if (total == point) {
           return WIN;
         } else if (total == 7){
           return LOSS;
         } else {
           return POINT;
         }
       }
     };

     /**
      * Come out roll.
      * @param total of the roll
      * @param point of the game.
      * @return next state
      */
     public State roll(int total, int point) {
       return this;
     }
   }

 }
