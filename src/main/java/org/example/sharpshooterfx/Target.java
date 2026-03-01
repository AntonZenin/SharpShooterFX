package org.example.sharpshooterfx;

// Класс мишени — хранит её позицию и скорость движения
public class Target {
    private double x;      // позиция по горизонтали
    private double y;      // позиция по вертикали
    private double speed;  // скорость движения вниз
    private double radius; // размер мишени
    private boolean active; // видима ли мишень

    // Конструктор — как в C++, называется так же как класс
    public Target(double x, double y, double speed, double radius) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.radius = radius;
        this.active = true;
    }

    // Метод next() — пересчитывает позицию мишени (совет преподавателя!)
    public void next(double fieldHeight) {
        if (!active) return;
        y += speed;
        // Если мишень вышла за нижний край — возвращаем наверх
        if (y > fieldHeight) {
            y = 0;
        }
    }

    // Геттеры — в Java принято получать поля через методы
    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return radius; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
