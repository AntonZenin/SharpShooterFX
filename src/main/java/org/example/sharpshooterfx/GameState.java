package org.example.sharpshooterfx;

// Класс хранит текущее состояние всех игровых объектов
// Это тот самый "Point" как у преподавателя, только для нашей игры
public class GameState {
    // Ближняя мишень
    public double nearX;
    public double nearY;

    // Дальняя мишень
    public double farX;
    public double farY;

    // Стрела
    public double arrowX;
    public double arrowY;
    public boolean arrowActive;

    public GameState(double nearX, double nearY,
                     double farX, double farY,
                     double arrowX, double arrowY,
                     boolean arrowActive) {
        this.nearX = nearX;
        this.nearY = nearY;
        this.farX = farX;
        this.farY = farY;
        this.arrowX = arrowX;
        this.arrowY = arrowY;
        this.arrowActive = arrowActive;
    }
}