package com.bd2r.harrypotter;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Vector3;

public class Main implements ApplicationListener {

    Texture worldTexture;
    Texture characterTexture;
    FitViewport viewport;
    SpriteBatch spriteBatch;
    Sprite characterSprite;
    TextureRegion[][] characterFrames;
    TextureRegion currentFrame;

    int direction = 0;
    float animationTimer = 0f;
    float frameDuration = 0.2f;
    int[][] Map; // só declarado aqui
    float destinationX = -1;
    float destinationY = -1;
    boolean movingOnClick = false;
    float speed = 4f;
    final int TILE_SIZE = 1;

    int[][] Map = {

        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0},
        {0,0,0,1,1,1,1,1,1,0,0,0,1,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
    };

    @Override
    public void create () {
        worldTexture = new Texture("mundo.png");
        characterTexture = new Texture("1.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(19, 25);
        characterSprite = new Sprite(characterTexture);
        characterSprite.setSize(1, 1);
        characterSprite.setPosition(8 * TILE_SIZE, TILE_SIZE);

        characterTexture = new Texture("1.png");
        characterFrames = TextureRegion.split(characterTexture, 32, 32);
        currentFrame = characterFrames[0][1];

        loadMap(); // <--- chamar aqui
    }

    private void loadMap() {
        Array<String> lines = new Array<>();
        FileHandle file = Gdx.files.internal("mapa.txt");

        String text = file.readString();
        String[] rawLines = text.split("\n");

        for (String line : rawLines) {
            lines.add(line.trim());
        }

        int rows = lines.size;
        int cols = lines.get(0).length();
        Map = new int[rows][cols];

        for (int y = 0; y < rows; y++) {
            String line = lines.get(y);
            for (int x = 0; x < cols; x++) {
                char c = line.charAt(x);
                Map[rows - 1 - y][x] = (c == '1') ? 1 : 0;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render () {
        input();
        logic();
        draw();
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(worldTexture, 0, 0, worldWidth, worldHeight);
        spriteBatch.draw(currentFrame,
            characterSprite.getX(),
            characterSprite.getY(),
            characterSprite.getWidth(),
            characterSprite.getHeight());
        //characterSprite.draw(spriteBatch);
        spriteBatch.draw(currentFrame, characterSprite.getX(), characterSprite.getY(), 1, 1);

        spriteBatch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float characterWidth = characterSprite.getWidth();
        float characterHeight = characterSprite.getHeight();

        characterSprite.setX(MathUtils.clamp(characterSprite.getX(), 0, worldWidth - characterWidth));
        characterSprite.setY(MathUtils.clamp(characterSprite.getY(), 0, worldHeight - characterHeight));

        if (movingOnClick) {
            float dx = destinationX - characterSprite.getX();
            float dy = destinationY - characterSprite.getY();

            float distance = (float)Math.sqrt(dx * dx + dy * dy);
            if (distance > 0.1f) {
                float moveX = (dx / distance) * speed * Gdx.graphics.getDeltaTime();
                float moveY = (dy / distance) * speed * Gdx.graphics.getDeltaTime();

                if (Move(characterSprite.getX() + moveX, characterSprite.getY() + moveY)) {
                    characterSprite.setPosition(characterSprite.getX() + moveX, characterSprite.getY() + moveY);

                    animationTimer += Gdx.graphics.getDeltaTime();
                    int frameIndex = (int)(animationTimer / frameDuration) % 3;

                    if (Math.abs(dx) > Math.abs(dy)) {
                        direction= dx > 0 ? 2 : 1;
                    } else {
                        direction= dy > 0 ? 3 : 0;
                    }

                    currentFrame = characterFrames[direction][frameIndex];

                } else {
                    movingOnClick = false; //Para ao encontrar uma colisão
                    animationTimer = 0f;
                    currentFrame = characterFrames[direction][1];
                }
            } else {
                movingOnClick = false;
                animationTimer = 0f;
                currentFrame = characterFrames[direction][1];
            }
        }
    }

    private void input() {
        float speed = 200f;
        float delta = Gdx.graphics.getDeltaTime();
        boolean moving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            Vector3 tempVec = new Vector3();
            viewport.getCamera().unproject(tempVec.set(x, y, 0));
            destinationX = tempVec.x;
            destinationY = tempVec.y;
            movingOnClick = true;
        }
        /*
        if (Gdx.input.isTouched()) {
            character(speed * delta, 0);
            direction = 2;
            moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            character(-speed * delta, 0);
            direction = 1;
            moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            character(0,speed * delta);
            direction = 3;
            moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            character(0,-speed * delta);
            direction = 0;
            moving = true;
        }

        if (moving) {
            animationTimer += delta;
            int frameIndex = (int) (animationTimer / frameDuration) %3;
            currentFrame = characterFrames[direction][frameIndex];
        } else {
            animationTimer = 0f;
            currentFrame = characterFrames[direction][1];
        }
        */
    }

    private boolean Move(float x, float y) {
        int column = (int)((x + 0.5f)/ TILE_SIZE);
        int row = (int)((y + 0.5f) / TILE_SIZE);
        if (row < 0 || row >= Map.length || column < 0 || column >= Map[0].length) {
            return false;
        }
        return Map[row][column] ==1;
    }

    private void character(float dx, float dy) {
        float newX = characterSprite.getX() + dx;
        float newY = characterSprite.getY() + dy;

        if (Move(newX, newY)) {
            characterSprite.setPosition(newX, newY);
        }
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void dispose () {
        characterTexture.dispose();
        worldTexture.dispose();
        spriteBatch.dispose();
    }
}
