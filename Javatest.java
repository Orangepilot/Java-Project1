package javatest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

class Character {
    String name;
    int health;
    int attackPower;
    int magicPower;
    int defense;
    String skill;
    boolean skillUsed;
    int healCount;

    public Character(String name, int health, int attackPower, int magicPower, int defense, String skill) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.magicPower = magicPower;
        this.defense = defense;
        this.skill = skill;
        this.skillUsed = false;
        this.healCount = 0;
    }

    public void useSkill(Character target) {
        target.health -= this.magicPower; // 스킬은 무조건 맞음
        this.skillUsed = true;
    }

    public boolean attack(Character target) {
        Random rand = new Random();
        if (rand.nextInt(100) < 80) { // 공격은 80% 확률로 명중
            target.health -= this.attackPower;
            return true;
        } else {
            return false; // 공격 빗나감
        }
    }

    public boolean heal() {
        if (this.healCount >= 2) { // 회복은 최대 2번 가능
            return false;
        }
        Random rand = new Random();
        if (rand.nextInt(100) < 70) { // 회복은 70% 확률로 성공
            this.health += 20;
            this.healCount++;
            return true;
        } else {
            this.healCount++;
            return false; // 회복 실패
        }
    }

    public boolean evade() {
        Random rand = new Random();
        if (rand.nextInt(100) < 50) { // 회피는 50% 확률로 성공
            return true;
        } else {
            this.health -= 10; // 회피 실패 시 소량 피해
            return false;
        }
    }
}

public class Javatest {
    private static JFrame frame;
    private static JPanel mainPanel, endPanel;
    private static Character player, opponent;
    private static final Character[] characters = {
        new Character("Warrior", 100, 20, 10, 15, "Power Strike"),
        new Character("Mage", 80, 10, 30, 10, "Fireball"),
        new Character("Rogue", 90, 15, 20, 12, "Shadow Strike")
    };
    private static final Random rand = new Random();

    public static void main(String[] args) {
        frame = new JFrame("Random Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new CardLayout());

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.green);
        mainPanel.setPreferredSize(new Dimension(400, 300));

        JLabel instruction = new JLabel("Choose your character:");
        JButton btnChar1 = new JButton(characters[0].name);
        JButton btnChar2 = new JButton(characters[1].name);
        JButton btnChar3 = new JButton(characters[2].name);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(instruction, gbc);

        gbc.gridy = 1;
        mainPanel.add(btnChar1, gbc);
        gbc.gridy = 2;
        mainPanel.add(btnChar2, gbc);
        gbc.gridy = 3;
        mainPanel.add(btnChar3, gbc);

        endPanel = new JPanel();
        endPanel.setBackground(Color.red);
        endPanel.setPreferredSize(new Dimension(400, 300));
        JLabel endMessage = new JLabel();
        endPanel.add(endMessage);

        ActionListener chooseCharacterListener = e -> {
            JButton source = (JButton) e.getSource();
            if (source.getText().equals(characters[0].name)) {
                player = characters[0];
            } else if (source.getText().equals(characters[1].name)) {
                player = characters[1];
            } else {
                player = characters[2];
            }

            do {
                opponent = characters[rand.nextInt(characters.length)];
            } while (opponent == player);

            gameLoop();
        };

        btnChar1.addActionListener(chooseCharacterListener);
        btnChar2.addActionListener(chooseCharacterListener);
        btnChar3.addActionListener(chooseCharacterListener);

        frame.add(mainPanel, "Main Panel");
        frame.add(endPanel, "End Panel");

        frame.pack();
        frame.setVisible(true);
    }

    private static void gameLoop() {
        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(Color.blue);
        gamePanel.setPreferredSize(new Dimension(400, 300));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel gameStatus = new JLabel("Game in progress...");
        gamePanel.add(gameStatus, gbc);

        frame.add(gamePanel, "Game Panel");
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "Game Panel");

        while (player.health > 0 && opponent.health > 0) {
            // 플레이어 행동 선택
            String[] options = {"Attack", "Heal", "Evade", "Use Skill", "Check Health", "End Game"};
            int choice = JOptionPane.showOptionDialog(frame, "Choose your action", "Player Turn",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 5) { // End Game
                endPanel.removeAll();
                endPanel.add(new JLabel("Game ended by player."));
                cl.show(frame.getContentPane(), "End Panel");
                System.out.println("Current working directory: " + System.getProperty("user.dir"));
                try (FileWriter writer = new FileWriter("sexyyy.txt", true)) {
                    writer.write("Ended game by sex" + "\n"); // 입력된 이름을 파일에 저장
                    JOptionPane.showMessageDialog(frame, "데이터가 저장되었습니다!");
                    
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "파일 저장 중 오류 발생!");
                }
                
                return;
            } else if (choice == 4) { // Check Health
                String healthStatus = "Player Health: " + player.health + "\nOpponent Health: " + opponent.health;
                JOptionPane.showMessageDialog(frame, healthStatus, "Health Status", JOptionPane.INFORMATION_MESSAGE);
                continue;
            }

            switch (choice) {
                case 0: // Attack
                    if (player.attack(opponent)) {
                        JOptionPane.showMessageDialog(frame, "Attack hits!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Attack missed!");
                    }
                    break;
                case 1: // Heal
                    if (player.heal()) {
                        JOptionPane.showMessageDialog(frame, "Heal successful!");
                    } else {
                        JOptionPane.showMessageDialog(frame, player.healCount < 2 ? "Heal failed!" : "Heal limit reached!");
                    }
                    break;
                case 2: // Evade
                    if (player.evade()) {
                        JOptionPane.showMessageDialog(frame, "Evade successful!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Evade failed!");
                    }
                    break;
                case 3: // Use Skill
                    if (!player.skillUsed) {
                        player.useSkill(opponent);
                        JOptionPane.showMessageDialog(frame, "Skill used successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Skill already used!");
                    }
                    break;
            }

            if (opponent.health <= 0) break;

            // 상대방 행동
            int action = rand.nextInt(4);
            switch (action) {
                case 0:
                    if (opponent.attack(player)) {
                        JOptionPane.showMessageDialog(frame, "Opponent's attack hits!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Opponent's attack missed!");
                    }
                    break;
                case 1:
                    if (opponent.heal()) {
                        JOptionPane.showMessageDialog(frame, "Opponent's heal successful!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Opponent's heal failed!");
                    }
                    break;
                case 2:
                    if (opponent.evade()) {
                        JOptionPane.showMessageDialog(frame, "Opponent's evade successful!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Opponent's evade failed!");
                    }
                    break;
                case 3:
                    if (!opponent.skillUsed) {
                        opponent.useSkill(player);
                        JOptionPane.showMessageDialog(frame, "Opponent used skill!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Opponent's skill already used!");
                    }
                    break;
            }
        }

        // 게임 종료 메시지
        String endMessageText = player.health > 0 ? "Player wins!" : "Opponent wins!";
        endPanel.removeAll();
        endPanel.add(new JLabel(endMessageText));
        cl.show(frame.getContentPane(), "End Panel");
    }
}
