package edu.spacemission.view;

import edu.spacemission.model.Question;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.IntConsumer;

public final class SpaceKnowledgeAssessmentView extends JPanel {
    private final List<Question> questions;
    private final Runnable back;
    private final IntConsumer finished;
    private int index;
    private int correct;
    private final JLabel progress = Theme.subtitle("");
    private final JLabel questionLabel = new JLabel();
    private final JLabel feedback = new JLabel(" ");
    private final JButton[] answers = new JButton[4];
    private final JButton next = Theme.button("Next question");

    public SpaceKnowledgeAssessmentView(List<Question> questions, Runnable back, IntConsumer finished) {
        this.questions = List.copyOf(questions);
        this.back = back;
        this.finished = finished;
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(27, 38, 27, 38));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel headings = new JPanel(new GridLayout(2, 1, 0, 5));
        headings.setOpaque(false);
        headings.add(Theme.title("Space Knowledge Assessment"));
        headings.add(progress);
        top.add(headings, BorderLayout.CENTER);
        JButton exit = Theme.button("Leave mission");
        exit.addActionListener(event -> back.run());
        top.add(exit, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JPanel content = Theme.card();
        content.setLayout(new BorderLayout(18, 18));
        questionLabel.setForeground(Theme.TEXT);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        content.add(questionLabel, BorderLayout.NORTH);
        JPanel options = new JPanel(new GridLayout(2, 2, 12, 12));
        options.setOpaque(false);
        for (int i = 0; i < answers.length; i++) {
            final int choice = i;
            answers[i] = Theme.button("");
            answers[i].setHorizontalAlignment(SwingConstants.LEFT);
            answers[i].addActionListener(event -> answer(choice));
            options.add(answers[i]);
        }
        content.add(options, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setOpaque(false);
        feedback.setForeground(Theme.MUTED);
        feedback.setFont(new Font("SansSerif", Font.PLAIN, 15));
        bottom.add(feedback, BorderLayout.CENTER);
        next.setVisible(false);
        next.addActionListener(event -> next());
        bottom.add(next, BorderLayout.EAST);
        content.add(bottom, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
        showQuestion();
    }

    private void showQuestion() {
        Question question = questions.get(index);
        progress.setText("QUESTION " + (index + 1) + " OF " + questions.size());
        questionLabel.setText("<html><div style='width:680px'>" + question.text() + "</div></html>");
        for (int i = 0; i < 4; i++) {
            answers[i].setText((char) ('A' + i) + "    " + question.options().get(i));
            answers[i].setEnabled(true);
        }
        feedback.setText("Choose the best answer.");
        feedback.setForeground(Theme.MUTED);
        next.setVisible(false);
    }

    private void answer(int choice) {
        Question question = questions.get(index);
        boolean isCorrect = question.correctAnswer().equals(String.valueOf((char) ('A' + choice)));
        if (isCorrect) correct++;
        for (JButton button : answers) button.setEnabled(false);
        feedback.setForeground(isCorrect ? Theme.GREEN : Theme.ORANGE);
        feedback.setText("<html>" + (isCorrect ? "Correct! " : "Not quite. ") + question.explanation() + "</html>");
        next.setText(index == questions.size() - 1 ? "See results" : "Next question");
        next.setVisible(true);
    }

    private void next() {
        if (index + 1 < questions.size()) {
            index++;
            showQuestion();
        } else {
            finished.accept(questions.isEmpty() ? 0 : Math.round(correct * 100f / questions.size()));
        }
    }
}
