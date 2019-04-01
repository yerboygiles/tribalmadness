package com.oddlabs.tt.particle;

final strictfp class LinearParticle extends Particle {
	private float velocity_x = 0f;
	private float velocity_y = 0f;
	private float velocity_z = 0f;
	private float acceleration_x = 0f;
	private float acceleration_y = 0f;
	private float acceleration_z = 0f;

	public LinearParticle() {
		this(0f);
	}

	public LinearParticle(float angle) {
		super(angle);
	}

        @Override
	public void update(float t) {
		super.update(t);
		velocity_x += acceleration_x*t;
		velocity_y += acceleration_y*t;
		velocity_z += acceleration_z*t;
		float x = getPosX() + velocity_x*t;
		float y = getPosY() + velocity_y*t;
		float z = getPosZ() + velocity_z*t;
		setPos(x, y, z);
	}

	public void setVelocity(float x, float y, float z) {
		velocity_x = x;
		velocity_y = y;
		velocity_z = z;
	}

	public float getVelocityX() {
		return velocity_x;
	}

	public float getVelocityY() {
		return velocity_y;
	}

	public float getVelocityZ() {
		return velocity_z;
	}

	public void setAcceleration(float x, float y, float z) {
		acceleration_x = x;
		acceleration_y = y;
		acceleration_z = z;
	}

	public float getAccelerationX() {
		return acceleration_x;
	}

	public float getAccelerationY() {
		return acceleration_y;
	}

	public float getAccelerationZ() {
		return acceleration_z;
	}
}
