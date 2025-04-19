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

    final int TILE_SIZE = 9;
    int[][] Map; // só declarado aqui

    @Override
    public void create () {
        worldTexture = new Texture("mundo.png");
        characterTexture = new Texture("1.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(936, 1408);
        characterSprite = new Sprite(characterTexture);
        characterSprite.setSize(32, 32);
        characterSprite.setPosition(0 * TILE_SIZE, 0 * TILE_SIZE);

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

        spriteBatch.end();
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float characterWidth = characterSprite.getWidth();
        float characterHeight = characterSprite.getHeight();

        characterSprite.setX(MathUtils.clamp(characterSprite.getX(), 0, worldWidth - characterWidth));
        characterSprite.setY(MathUtils.clamp(characterSprite.getY(), 0, worldHeight - characterHeight));
    }

    private void input() {
        float speed = 200f;
        float delta = Gdx.graphics.getDeltaTime();
        boolean moving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            character(speed * delta, 0);
            direction = 2;
            moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            character(-speed * delta, 0);
            direction = 1;
            moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            character(0, speed * delta);
            direction = 3;
            moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            character(0, -speed * delta);
            direction = 0;
            moving = true;
        }

        if (moving) {
            animationTimer += delta;
            int frameIndex = (int) (animationTimer / frameDuration) % 3;
            currentFrame = characterFrames[direction][frameIndex];
        } else {
            animationTimer = 0f;
            currentFrame = characterFrames[direction][1];
        }
    }

    private boolean Move(float x, float y) {
        int column = (int)((x + 0.5f) / TILE_SIZE);
        int row = (int)((y + 0.5f) / TILE_SIZE);
        if (Map == null || row < 0 || row >= Map.length || column < 0 || column >= Map[0].length) {
            return false;
        }
        return Map[row][column] == 1;
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
