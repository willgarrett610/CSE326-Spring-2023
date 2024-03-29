package game.world;

public class Vec2f {

    public static final Vec2f ZERO = new Vec2f(0,0);

    public float x;
    public float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f divide(Vec2f vec) {
        return divide(vec.x, vec.y);
    }

    public Vec2f divide(float x, float y) {
        return new Vec2f(this.x / x, this.y / y);
    }

    public Vec2f multiply(Vec2f vec) {
        return multiply(vec.x, vec.y);
    }

    public Vec2f multiply(float x, float y) {
        return new Vec2f(this.x * x, this.y * y);
    }

    public Vec2f add(Vec2f vec) {
        return add(vec.x, vec.y);
    }

    public Vec2f add(float x, float y) {
        return new Vec2f(this.x + x, this.y - y);
    }

    public Vec2f subtract(Vec2f vec) {
        return subtract(vec.x, vec.y);
    }

    public Vec2f subtract(float x, float y) {
        return new Vec2f(this.x - x, this.y + y);
    }

    public float distanceTo(Vec2f b) {
        return (float) Math.sqrt(Math.pow(this.x - b.x, 2) + Math.pow(this.y - b.y, 2));
    }

    public Vec2f clone() {
        return new Vec2f(this.x, this.y);
    }

    public Vec2f normalize() {
        float length = distanceTo(ZERO);
        return new Vec2f(x/length, y/length);
    }

    public Vec2f pointToNormalized(Vec2f b) {
        Vec2f pointing = new Vec2f(x - b.x, y - b.y);

        // Point b is the same as this point
        if (pointing.x == 0 && pointing.y == 0) return ZERO;

        return pointing.normalize().multiply(-1,1);
    }

    @Override
    public String toString() {
        return String.format("Vec2f{x=%f,y=%f};", x, y);
    }

}
