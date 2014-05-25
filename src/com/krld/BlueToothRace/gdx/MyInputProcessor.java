package com.krld.BlueToothRace.gdx;

import com.krld.BlueToothRace.ProtocolMessages;

/**
 * Created by Andrey on 5/25/2014.
 */
public class MyInputProcessor implements com.badlogic.gdx.InputProcessor {

    public static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private GdxGameView gdxGameView;

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int button, int poiter) {
        if (x < WIDTH / 2) {
            if (y < HEIGHT / 2) {
                gdxGameView.sendMessage(ProtocolMessages.INCREASE_SPEED);
            } else {
                gdxGameView.sendMessage(ProtocolMessages.DECREASE_SPEED);
            }
        } else {
            if (y < HEIGHT / 2) {
                gdxGameView.sendMessage(ProtocolMessages.TURN_LEFT);
            } else {
                gdxGameView.sendMessage(ProtocolMessages.TURN_RIGHT);
            }
        }


        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int button, int poiter) {
        if (x < WIDTH / 2) {
            gdxGameView.sendMessage(ProtocolMessages.STILL_SPEED);
        } else {
            gdxGameView.sendMessage(ProtocolMessages.NO_TURN);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int point) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i2) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }

    public void setGdxGameView(GdxGameView gdxGameView) {
        this.gdxGameView = gdxGameView;
    }

    public GdxGameView getGdxGameView() {
        return gdxGameView;
    }

    /* private void initControlButtons() {
        increaseSpeedButton = (ImageButton) findViewById(R.id.increaseSpeedClientButton);

        increaseSpeedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendMessage(ProtocolMessages.INCREASE_SPEED);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendMessage(ProtocolMessages.STILL_SPEED);
                }
                return false;
            }
        });

        decreaseSpeedButton = (ImageButton) findViewById(R.id.decreaseSpeedClientButton);

        decreaseSpeedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendMessage(ProtocolMessages.DECREASE_SPEED);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendMessage(ProtocolMessages.STILL_SPEED);
                }
                return false;
            }
        });

        turnLeftButton = (ImageButton) findViewById(R.id.turnLeftClientButton);

        turnLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendMessage(ProtocolMessages.TURN_LEFT);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendMessage(ProtocolMessages.NO_TURN);
                }
                return false;
            }
        });

        turnRightButton = (ImageButton) findViewById(R.id.turnRightClientButton);

        turnRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendMessage(ProtocolMessages.TURN_RIGHT);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendMessage(ProtocolMessages.NO_TURN);
                }
                return false;
            }
        });
    }
*/
}
