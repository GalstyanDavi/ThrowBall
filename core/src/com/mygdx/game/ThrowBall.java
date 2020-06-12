package com.mygdx.game;
import java.awt.*;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


class Ball{
	public Rectangle rectangle;
	public int speed;      // ball fly speed
	public double delta_x; // x angle
	public double delta_y; // y angle

	// Ball initialization
	public Ball(){
		rectangle=new Rectangle();
		speed=200;
		delta_x=0;
		delta_y=0;
	}
}
class MyInputProcessor implements InputProcessor {
	public boolean keyPressed ;

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			keyPressed = true;

		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			keyPressed=false;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}




public class ThrowBall extends ApplicationAdapter    {
	private SpriteBatch batch;
	private Texture img;
	private OrthographicCamera camera;
	private Array<Ball> balls; //Array for Balls
	private long Time;  //Ball moving time
	MyInputProcessor processor;  //input handeling



	@Override
	public void create () {
		batch = new SpriteBatch();
		// load the images for the ball, 100x100 pixels each
		img = new Texture("soccer-ball.png");
		processor = new MyInputProcessor();
		Gdx.input.setInputProcessor(processor);



		//create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 800);
		batch = new SpriteBatch();
		// create the balls array and throwing
		balls = new Array<Ball>();


	}
	//crating ball for Throwing
	private void Throwing(float x,float y,float delta_x,float delta_y) {
		Ball ball = new Ball();
		ball.rectangle.x = x;
		ball.rectangle.y = y;
		ball.delta_x=delta_x;
		ball.delta_y=delta_y;
		ball.rectangle.width = 100;
		ball.rectangle.height = 100;
		balls.add(ball);
		Time = TimeUtils.nanoTime();
	}
	//the Ball move logic
	private void Ball_Move(Ball ball) {
		if(ball.speed == 0 && ball.rectangle.x > ball.rectangle.width) {
			ball.speed += (int)ball.rectangle.y*20; //TODO with gravity
			if( ball.delta_x > 0) ball.delta_x = 0.01;
			if( ball.delta_x < 0) ball.delta_x =- 0.01;
			ball.delta_y = -ball.delta_y;
		}

			if (ball.rectangle.y != 0) ball.rectangle.x += ball.delta_x * ball.speed * Gdx.graphics.getDeltaTime();
			ball.rectangle.y += ball.delta_y * ball.speed * Gdx.graphics.getDeltaTime();
			//borads checking
			if (ball.rectangle.x < 0 || ball.rectangle.x > Gdx.graphics.getWidth() + ball.rectangle.height/2 )
				ball.delta_x = -ball.delta_x;
			if (ball.rectangle.y < 0  || ball.rectangle.y > Gdx.graphics.getWidth() + ball.rectangle.width/2 )
				ball.delta_y = -ball.delta_y;
			//some magic numbers for gravitation
			if(ball.delta_y > 0) {
				ball.speed -= 1;
				ball.delta_y -= 0.05;
			}

		if (ball.rectangle.y < 0)  ball.rectangle.y = 0 ;
		if (ball.rectangle.x < 0)  ball.rectangle.x = 0 ;
		if ( ball.rectangle.x > Gdx.graphics.getWidth() + ball.rectangle.height/2 )
			ball.rectangle.x = Gdx.graphics.getWidth() + ball.rectangle.height/2 ;
		if ( ball.rectangle.y > Gdx.graphics.getWidth() + ball.rectangle.width/2 )
			ball.rectangle.y = Gdx.graphics.getWidth() + ball.rectangle.width/2 ;


	}



	@Override
	public void render () {
		Gdx.gl.glClearColor(10, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);


		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		Vector3 touchPos1 = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		camera.unproject(touchPos);

		float deltaX = 0;
		float deltaY= 0;

		if (processor.keyPressed) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			deltaX = touchPos.x;
			deltaY = touchPos.y;
			Throwing(touchPos.x, touchPos.y, 10,10);
		}
		//while(processor.keyPressed);
			touchPos1.set(Gdx.input.getX(), Gdx.input.getY(), 0);

			//Throwing(touchPos.x, touchPos.y, touchPos1.x-deltaX, touchPos1.y - deltaY);



		for(Ball ball: balls) {
		//the ball changing time
			if(TimeUtils.nanoTime() - Time > 10000000) Ball_Move(ball);


		}
		batch.begin();
		// balls drawing
		for(Ball ball: balls) {

			// if the ball snapped
			if (ball.speed == 0 && ball.delta_y == 0 && ball.rectangle.y <= 0) {
				ball.rectangle.y = 0;
				ball.delta_y=0;
			}
			batch.draw(img, ball.rectangle.x, ball.rectangle.y);
		}
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

