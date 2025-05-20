/*
Player skills and abilities:
    Offensive:
        JINGU MASTERY - guaranteed 3 Crits after 4 consecutive attacks.
        STUN - Stuns enemy and skips their turn (1/3 chances to land) for balancing purposes.

    Defensive:
        PACK - Access inventory
            -> VIEW - view next item on Stack.
            -> CONSUME - consume item on top of Stack.
            -> USE SHIELD - Disable's the enemy's crit chance and returns 25% of the damage to the opponent.
                - Can only be used when health is at 50% below.


Opponent skills and abilities:
    Offensive:
        Basic Attack

    Defensive:
        BACKTRACK - Regains previous health lost.
        STEAL - Steals top item from player's stack.
*/

import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class turnBased {

    // Initialize objects to be used
    private static Stack<String> items = new Stack<>();
    private static Stack<Integer> playerHpHistory = new Stack<>();
    private static Stack<Integer> enemyHpHistory = new Stack<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int[] shields = { 1, 1, 1, 1, 1 };
    private static Random r = new Random();
    private static boolean shieldActive = false, enemyStunned = false;
    private static int shieldNum = 5, enemyDamage = 0, playerHp, enemyHp;
    private static String username = null, enemyName = null;
    private static Stack<Integer> jinguMastery = new Stack<>();
    private static int jinguCount = 0;
    private static int dmg, damage, playerAtk = 5;

    public static void main(String[] args) {
        // Add items to stack
        items.add("Cake");
        items.add("Candy");
        items.add("Poison");
        items.add("Cheese");
        items.add("Gatorade");

        // Trigger Replayability
        while (true) {
            playerHp = 100;
            enemyHp = 100;

            System.out.print("Enter your username: ");
            username = scanner.nextLine();

            System.out.print("Enter your enemy's name: ");
            enemyName = scanner.nextLine();

            System.out.println("Welcome to MAN VS. MACHINE THE GAME!");

            while (playerHp > 0 && enemyHp > 0) {
                System.out.println("\n" + username + " HP: " + playerHp + " " + getHealthBar(playerHp));
                System.out.println(enemyName + " HP: " + enemyHp + " " + getHealthBar(enemyHp));
                System.out.println("Choose your action: attack / pack / stun / undo (type exit to exit)");
                String action = scanner.nextLine();
                System.out.println("");

                if (action.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting game. Goodbye!");
                    break;
                }

                // ATTACK
                if (action.equalsIgnoreCase("attack")) {
                    playerHpHistory.push(playerHp);
                    enemyHpHistory.push(enemyHp);
                    heroJinguMastery();
                    damage = (int) (Math.random() * 20) + 1; // randomized 1 - 20

                    enemyHp -= (dmg + damage);
                    System.out.println("You attacked the enemy for " + damage + " damage!");
                } else if (action.equalsIgnoreCase("pack")) { // Access inventory
                    inventory();

                } else if (action.equalsIgnoreCase("stun")) { // Stun enemy
                    if (Math.random() < 0.33) {
                        enemyStunned = true;
                        System.out.println(enemyName + " has been successfully stunned!");
                    } else {
                        System.out.println("Stun failed! " + enemyName + " is not stunned.");
                    }

                } else if (action.equalsIgnoreCase("undo")) { // Undo previous attack
                    if (!playerHpHistory.isEmpty() && !enemyHpHistory.isEmpty()) {
                        playerHp = playerHpHistory.pop();
                        enemyHp = enemyHpHistory.pop();
                        System.out.println("Undo successful!");
                    } else {
                        System.out.println("Nothing to undo!");
                    }
                    continue;
                } else {
                    System.out.println("Invalid action. Try again.");
                    continue;
                }

                // ENEMY'S TURN
                // Random number for steal
                int rando = r.nextInt(6 - 1 + 1) + 1;

                if (enemyHp > 0) {
                    if (enemyStunned) { // Enemy is stunned
                        System.out.println(enemyName + " is stunned and skips their turn!");
                        enemyStunned = false; // Reset stun

                    } else if (rando == 4) { // Random numer equals 4
                        checkFood(2);

                    } else {
                        if (shieldActive) { // shield is activated
                            enemyDamage = r.nextInt(10 - 1 + 1) + 1;
                            System.out.println("Shield is activated, " + enemyName + " cannot land a critical hit! ");

                        } else { // shield not activated
                            enemyDamage = (int) (Math.random() * 20) + 1;
                        }

                        // Damage to player
                        playerHp -= enemyDamage;
                        System.out.println(enemyName + " attacks you for " + enemyDamage + " damage!");

                        // Backtrack logic
                        if (enemyHp < 50) {
                            if (Math.random() < 0.5) {
                                enemyHp += enemyDamage;
                                System.out
                                        .println("Backtrack activated! " + enemyName + " restored " + enemyHp + " HP.");
                            }

                        }

                        // Deactivate shield after turn
                        if (shieldActive) {
                            shieldActive = false;
                            System.out.println("Shield deactivated! You are now vulnerable.");
                        }

                        // Player is dead
                        if (playerHp <= 0) {
                            System.out.println(enemyName + " wins!");
                            System.out.println();
                            break;
                        }

                        // Enemy is dead
                        if (enemyHp <= 0) {
                            System.out.println(username + " wins!");
                            System.out.println();
                            break;
                        }

                    }
                }

            }

            System.out.print("Do you want to restart the game? (yes/no): ");
            String restart = scanner.next();
            if (!restart.equalsIgnoreCase("yes")) {
                System.out.println("Thanks for playing!");
                break;
            }

        }
    }

    

    // Jingu Mastery
    public static void heroJinguMastery() {
        if (jinguMastery.isEmpty()) {
            dmg = playerAtk; // playerAtk is 5
            jinguCount++;
            System.out.println("Normal attack (" + jinguCount + "/4)");

            if (jinguCount >= 4) {
                for (int i = 0; i < 3; i++) {
                    jinguMastery.push(playerAtk + (playerAtk / 2)); // +7atk from jinguMastery
                }
                System.out.println("Jingu Mastery READY! Next 3 attacks deal more damage with life steal!");
                jinguCount = 0;
            }
        } else {
            dmg = jinguMastery.pop();
            int lifestealAmount = (damage / 5); // +20% Hp lifesteal 
            playerHp += lifestealAmount;
            System.out.println("Jingu Mastery is ACTIVE!");
            System.out.println(lifestealAmount + " HP was added because of lifesteal! (Current HP: " + playerHp + ")");
        }
    }

    // Health bar representation
    static String getHealthBar(int hp) {
        int totalBars = 20;
        int bars = Math.max(0, (int) Math.round(hp / 5.0));
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < bars; i++) {
            bar.append("|");
        }
        for (int i = bars; i < totalBars; i++) {
            bar.append(" ");
        }
        bar.append("]");
        return bar.toString();
    }

    // Access inventory
    static void inventory() {
        System.out.println("You opened your backpack. You have the following options.");
        System.out.println(">>View   >>Consume   >>Use Shield [" + shieldNum + "]");
        System.out.print("Which will you choose? ");

        String choice = scanner.nextLine();
        System.out.println("");

        if (choice.equalsIgnoreCase("use shield")) {
            useShield();
        } else if (choice.equalsIgnoreCase("view") || choice.equalsIgnoreCase("view inventory")) {
            if (items.isEmpty()) {
                System.out.println("Your inventory is empty.");
            } else {
                System.out.println("Your inventory contains (top item will be used first):");
                System.out.println("- " + items.peek() + " (next to use)");
                System.out.println("");
                inventory();
            }
        } else if (choice.equalsIgnoreCase("consume")) {
            checkFood(1);
        } else {
            System.out.println("Not a valid input, try again.");
            System.out.println("");
            inventory();
        }
    }

    // Access food items
    static void checkFood(int target) {
        if (items.empty() && target == 1) {
            System.out.println("There are no more items in your pack.");
            return;
        } else if (items.empty() && target == 2) {
            System.out.println("The enemy tried to steal from your pack, but it was empty!");
            return;
        }

        int heal;
        String food = items.pop();

        if (food.equalsIgnoreCase("cake")) {
            heal = 50;
        } else if (food.equalsIgnoreCase("candy")) {
            heal = 5;
        } else if (food.equalsIgnoreCase("cheese")) {
            heal = 25;
        } else if (food.equalsIgnoreCase("gatorade")) {
            heal = 30;
        } else {
            heal = -20;
        }

        if (target == 1) {
            playerHp += heal;
            System.out.println(
                    "You consumed " + food + " that added " + heal + " to your health. Your health is now " + playerHp);
        } else {
            enemyHp += heal;
            System.out.println("The enemy stole from your pack! He consumed " + food + " that added " + heal
                    + " to his health. His health is now " + enemyHp);
        }
    }

    // Access shields
    static void useShield() {
        if (playerHp > 50) {
            System.out.println("Shield cannot be used right now.");
            System.out.println("");
            return;
        }

        for (int i = 0; i < shields.length; i++) {
            if (shields[i] == 1) {
                shields[i] = 0;
                shieldActive = true;
                shieldNum -= 1;
                break;
            }
        }

        if (!shieldActive) {
            System.out.println("You have no more shields to spare.");
            return;
        }
    }
}
