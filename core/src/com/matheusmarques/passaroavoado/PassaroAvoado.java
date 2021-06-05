package com.matheusmarques.passaroavoado;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import javax.swing.ViewportLayout;

public class PassaroAvoado extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture passaro; // feito dessa forma porque celulares merda não rodam texture como array.
	private Texture passaro1;
	private Texture passaro2;
	private Texture passaro3;
	private Texture fundo;
	private Random numeroRandom;
	private BitmapFont fonte;
	private Texture canoCima;
	private Texture canoBaixo;
	private Circle passaroCirculo;
	private Rectangle CanoCimaRet;
	private Rectangle CanoBaixoRet;
	private ShapeRenderer shape;
	private Texture GameOver;
	private String frase;

	private float larguraDispostivo;
	private float alturaDispostivo;
	private int pontuacao = 0;
	private int estadoJogo = 0; // jogo não iniciado;

	private boolean marcouPonto = false;

	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoInicial;
	private float posicaoMovimentoCano;
	private float espacoEntreCanos;
	private float espacoEntreCanosRandom;
	private float deltaTime;

	private OrthographicCamera camera;
	private Viewport viewport;
private final float VIRTUAL_WIDTH = 768;
private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
		batch = new SpriteBatch();
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(4);
		passaroCirculo = new Circle();
		CanoCimaRet = new Rectangle();
		CanoBaixoRet = new Rectangle();
		shape = new ShapeRenderer();
		passaro1 = new Texture("passaro1.png");
		passaro2 = new Texture("passaro2.png");
		passaro3 = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");
		canoCima = new Texture("cano_topo.png");
		canoBaixo = new Texture("cano_baixo.png");
		GameOver = new Texture("game_over.png");
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,camera);
		frase= new String();
		frase = "Toque para Iniciar";
		numeroRandom = new Random();
		larguraDispostivo = VIRTUAL_WIDTH;
		alturaDispostivo = VIRTUAL_HEIGHT;
		posicaoInicial = alturaDispostivo/2;
		posicaoMovimentoCano =larguraDispostivo;
		espacoEntreCanos = 300;
	}

	@Override
	public void render () {
		camera.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime =Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;
		if((int) variacao==0) passaro = passaro1;
		if((int) variacao==1) passaro = passaro2;
		if((int) variacao==2) passaro = passaro3;

		if(variacao>2) variacao = 0;

		if(estadoJogo == 0){
			if(Gdx.input.justTouched()){
				estadoJogo = 1;
			}
		}else{
			velocidadeQueda++;

			if(posicaoInicial>0 || velocidadeQueda < 0)
				posicaoInicial = posicaoInicial - velocidadeQueda;


			if(estadoJogo==1){
				posicaoMovimentoCano = posicaoMovimentoCano - deltaTime*150;


				if(posicaoMovimentoCano< -canoBaixo.getWidth()){
					posicaoMovimentoCano =larguraDispostivo;
					espacoEntreCanosRandom = numeroRandom.nextInt(400) - 200;
					marcouPonto = false;
				}


				if(posicaoMovimentoCano<120){
					if(!marcouPonto){
						pontuacao++;
						marcouPonto =true;
					}
				}


				if(Gdx.input.justTouched()){
					velocidadeQueda = - 20;
				}


				if(posicaoInicial >= alturaDispostivo)
					posicaoInicial = alturaDispostivo;

			}

		}

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(fundo,0,0, larguraDispostivo,alturaDispostivo);
		batch.draw(canoCima,posicaoMovimentoCano,alturaDispostivo/2 + espacoEntreCanos / 2 + espacoEntreCanosRandom);
		batch.draw(canoBaixo,posicaoMovimentoCano,alturaDispostivo/2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + espacoEntreCanosRandom);
		batch.draw(passaro, 120,posicaoInicial);
		if(estadoJogo==1){
			fonte.draw(batch, String.valueOf(pontuacao) , larguraDispostivo/2, alturaDispostivo-40);
		}
		if(estadoJogo==0){

			fonte.draw(batch, String.valueOf(frase) , larguraDispostivo/2-200, alturaDispostivo-200);
		}

		if(estadoJogo==2){
			fonte.getData().setScale(3);
			frase = "Toque para Recomeçar";
			batch.draw(GameOver,larguraDispostivo/2 - (GameOver.getWidth()/2),alturaDispostivo/2 - (GameOver.getHeight()/2));
			fonte.draw(batch, String.valueOf(frase) , larguraDispostivo/2-200, alturaDispostivo-200);
		}

		batch.end();

		passaroCirculo.set(120 + (passaro.getWidth()/2), posicaoInicial + (passaro.getHeight()/2),passaro.getWidth()/2);
		CanoBaixoRet = new Rectangle(
				posicaoMovimentoCano,alturaDispostivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2+espacoEntreCanosRandom,
				canoBaixo.getWidth(),canoBaixo.getHeight()
		);
		CanoCimaRet = new Rectangle(
				posicaoMovimentoCano,alturaDispostivo/2 + espacoEntreCanos/2 + espacoEntreCanosRandom,
				canoCima.getWidth(),canoCima.getHeight()
		);
		/*shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(passaroCirculo.x,passaroCirculo.y,passaroCirculo.radius);
		shape.rect(CanoBaixoRet.x,CanoBaixoRet.y,CanoBaixoRet.width,CanoBaixoRet.height);
		shape.rect(CanoCimaRet.x,CanoCimaRet.y,CanoCimaRet.width,CanoCimaRet.height);
		shape.setColor(Color.RED);
		shape.end();*/

		if((Intersector.overlaps(passaroCirculo,CanoBaixoRet) || Intersector.overlaps(passaroCirculo,CanoCimaRet)) || posicaoInicial<=0 || posicaoInicial>=alturaDispostivo) {
	estadoJogo = 2;
			if(Gdx.input.justTouched()){
				estadoJogo = 0;
				pontuacao = 0;
				velocidadeQueda = 0;
				posicaoInicial = alturaDispostivo/2;
				posicaoMovimentoCano = larguraDispostivo;
			}
		}
	}

@Override
	public void resize(int width, int height){
viewport.update(width,height);

}
}
