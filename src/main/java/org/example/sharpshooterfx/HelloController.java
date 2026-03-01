package org.example.sharpshooterfx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.application.Platform;

public class HelloController {

    // @FXML — связывает поле с элементом из FXML по fx:id
    @FXML private Pane   gameField;
    @FXML private Label  scoreLabel;
    @FXML private Label  shotsLabel;
    @FXML private Button pauseButton;

    private GameEngine engine;
    private int score = 0;
    private int shots = 0;

    // Вызывается автоматически после загрузки FXML
    @FXML
    public void initialize() {
        // Пока ничего не делаем — ждём нажатия "Начало игры"
    }

    @FXML
    public void onStartGame() {
        // Если игра уже запущена — сначала останавливаем
        if (engine != null && engine.isRunning()) {
            engine.stop();
        }

        // Сбрасываем счёт и выстрелы
        score = 0;
        shots = 0;
        updateLabels();

        // Создаём движок и запускаем
        engine = new GameEngine(gameField, this::onScoreUpdate);
        engine.start();

        pauseButton.setText("Пауза");
    }

    @FXML
    public void onStopGame() {
        if (engine != null && engine.isRunning()) {
            engine.stop();
        }
    }

    @FXML
    public void onPauseGame() {
        if (engine == null || !engine.isRunning()) return;
        engine.pause();
        pauseButton.setText(engine.isPaused() ? "Продолжить" : "Пауза");
    }

    @FXML
    public void onShoot() {
        if (engine == null || !engine.isRunning() || engine.isPaused()) return;
        if (engine.isArrowActive()) return; // стрела уже летит — игнорируем нажатие
        shots++;
        updateLabels();
        engine.shoot();
    }

    // Вызывается из GameEngine когда есть попадание
    private void onScoreUpdate(int points) {
        score += points;
        // UI обновляем только в главном потоке!
        Platform.runLater(this::updateLabels);
    }

    private void updateLabels() {
        scoreLabel.setText(String.valueOf(score));
        shotsLabel.setText(String.valueOf(shots));
    }
}