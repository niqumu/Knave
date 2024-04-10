package dev.niqumu.knave.util;

@SuppressWarnings("unused")
public interface MinecraftPhysics {
	double JUMP_MOTION_BASE = 0.42F;
	double JUMP_MOTION_POTION_MODIFIER = 0.1F;
	double JUMP_MOTION_TICK_MODIFIER = 0.98F;
	double JUMP_003 = 0.003016215090443897;

	double LADDER_UP = 0.1176F;
	double LADDER_DOWN = -0.15F;
	double LADDER_MAX_SPEED = 0.15F;

	double GRAVITY = -0.08D;

	double MOVE_MULTIPLIER_SPRINT = 1.3F;
	double MOVE_MULTIPLIER_SNEAK = 0.41561F;

	double EYE_HEIGHT_STANDING = 1.62F;
	double EYE_HEIGHT_SNEAKING = 1.54F;

	double PLAYER_HITBOX_WIDTH = 0.6D;
	double PLAYER_HITBOX_HEIGHT = 1.8D;
}
