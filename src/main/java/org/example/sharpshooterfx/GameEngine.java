package org.example.sharpshooterfx;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class GameEngine implements Runnable {

    // Игровое поле
    private final Pane gameField;

    // Мишени
    private final Target nearTarget;   // ближняя (1 очко)
    private final Target farTarget;    // дальняя (2 очка)

    // Стрела
    private final Arrow arrow;

    // Визуальные объекты мишеней и стрелы
    private Circle nearCircle;
    private Circle farCircle;
    private Polygon arrowShape;

    // Состояние игры
    private volatile boolean running = false;
    private volatile boolean paused  = false;

    // Размеры поля
    private final double fieldWidth  = 650;
    private final double fieldHeight = 460;

    // Колбэк — когда очки меняются, сообщаем контроллеру
    private ScoreCallback scoreCallback;

    // Интерфейс для передачи очков в контроллер
    public interface ScoreCallback {
        void onScore(int points);
    }

    public GameEngine(Pane gameField, ScoreCallback scoreCallback) {
        this.gameField      = gameField;
        this.scoreCallback  = scoreCallback;

        // Ближняя мишень
        nearTarget = new Target(fieldWidth * 0.6, fieldHeight / 2, 7, 25);

        // Дальняя мишень
        farTarget  = new Target(fieldWidth * 0.85, fieldHeight / 2, 10, 12);

        arrow = new Arrow();
    }

    // Вызывается один раз при старте — создаём визуальные объекты и добавляем на поле
    private void initVisuals() {
        // Ближняя мишень — красный круг
        nearCircle = new Circle(nearTarget.getX(), nearTarget.getY(),
                nearTarget.getRadius(), Color.RED);
        nearCircle.setStroke(Color.DARKRED);

        // Дальняя мишень — меньше, другой оттенок
        farCircle  = new Circle(farTarget.getX(), farTarget.getY(),
                farTarget.getRadius(), Color.SALMON);
        farCircle.setStroke(Color.DARKRED);

        // Стрела — треугольник (стрелка вправо)
        arrowShape = new Polygon(
                0.0, -6.0,   // верх
                20.0, 0.0,   // острие
                0.0,  6.0    // низ
        );
        arrowShape.setFill(Color.BLUE);
        arrowShape.setVisible(false); // пока не видна

        // Platform.runLater — все изменения UI делаем в главном потоке!
        // (как PostMessage в WinAPI — нельзя трогать UI из другого потока)
        Platform.runLater(() -> {
            gameField.getChildren().addAll(nearCircle, farCircle, arrowShape);
        });
    }

    // Главный игровой цикл — запускается в отдельном потоке
    @Override
    public void run() { //TO DO оптимизировать потоки
        initVisuals();

        while (running) {
            if (!paused) {
                // Пересчитываем позиции (метод next)
                nearTarget.next(fieldHeight);
                farTarget.next(fieldHeight);
                arrow.next(fieldWidth);

                // Проверяем попадание
                checkHit();

                // Обновляем визуал в главном потоке
                Platform.runLater(this::updateVisuals);
            }

            // Задержка — скорость игрового цикла
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Обновляем позиции кружков и стрелы на экране
    private void updateVisuals() {
        nearCircle.setCenterX(nearTarget.getX());
        nearCircle.setCenterY(nearTarget.getY());

        farCircle.setCenterX(farTarget.getX());
        farCircle.setCenterY(farTarget.getY());

        if (arrow.isActive()) {
            arrowShape.setVisible(true);
            arrowShape.setLayoutX(arrow.getX());
            arrowShape.setLayoutY(arrow.getY());
        } else {
            arrowShape.setVisible(false);
        }
    }

    // Проверка попадания стрелы в мишень
    private void checkHit() {
        if (!arrow.isActive()) return;

        // Расстояние от стрелы до центра мишени
        double dNear = Math.sqrt(
                Math.pow(arrow.getX() - nearTarget.getX(), 2) +
                        Math.pow(arrow.getY() - nearTarget.getY(), 2)
        );
        double dFar = Math.sqrt(
                Math.pow(arrow.getX() - farTarget.getX(), 2) +
                        Math.pow(arrow.getY() - farTarget.getY(), 2)
        );

        if (dNear < nearTarget.getRadius()) {
            arrow.setActive(false);
            scoreCallback.onScore(1); // 1 очко за ближнюю
        } else if (dFar < farTarget.getRadius()) {
            arrow.setActive(false);
            scoreCallback.onScore(2); // 2 очка за дальнюю
        }
    }

    // Выстрел — стрела стартует из правого края жёлтой зоны, середина по высоте
    public void shoot() {
        if (!running || paused) return;
        if (!arrow.isActive()) { // нельзя стрелять пока летит предыдущая стрела
            arrow.shoot(65, fieldHeight / 2);
        }
    }

    public void start() {
        running = true;
        paused  = false;
        Thread thread = new Thread(this);
        thread.setDaemon(true); // поток завершится вместе с приложением
        thread.start();
    }

    public void stop() {
        running = false;
        // Убираем объекты с поля
        Platform.runLater(() -> gameField.getChildren()
                .removeAll(nearCircle, farCircle, arrowShape));
    }

    public void pause() {
        paused = !paused; // переключаем паузу
    }

    public boolean isRunning() { return running; }
    public boolean isPaused()  { return paused; }

    public boolean isArrowActive() {
        return arrow.isActive();
    }
}
