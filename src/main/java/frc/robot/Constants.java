// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 */
public final class Constants {

    // The left-to-right distance between the drivetrain wheels
    public static final double DRIVETRAIN_TRACKWIDTH_METERS = 0.4699;

    // The front-to-back distance between the drivetrain wheels.
    public static final double DRIVETRAIN_WHEELBASE_METERS = 0.4699;

    public static final int FRONT_LEFT_MODULE_DRIVE_MOTOR = 5;
    public static final int FRONT_LEFT_MODULE_STEER_MOTOR = 6;
    public static final int FRONT_LEFT_MODULE_STEER_ENCODER = 32;
    public static final double FRONT_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(307.8);
    // CanID 32 encoder readings = 223.5, 178, 307.8

    public static final int FRONT_RIGHT_MODULE_DRIVE_MOTOR = 2;
    public static final int FRONT_RIGHT_MODULE_STEER_MOTOR = 11;
    public static final int FRONT_RIGHT_MODULE_STEER_ENCODER = 34;
    public static final double FRONT_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(301.3);
    // CanID 34 encoder readings = 37.8, 174, 301.3

    public static final int BACK_LEFT_MODULE_DRIVE_MOTOR = 7;
    public static final int BACK_LEFT_MODULE_STEER_MOTOR = 8;
    public static final int BACK_LEFT_MODULE_STEER_ENCODER = 31;
    public static final double BACK_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(13.1);
    // CanID 31 encoder readings = 283.3, 187, 13.1

    public static final int BACK_RIGHT_MODULE_DRIVE_MOTOR = 4;
    public static final int BACK_RIGHT_MODULE_STEER_MOTOR = 3;
    public static final int BACK_RIGHT_MODULE_STEER_ENCODER = 33;
    public static final double BACK_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(49.2);
    // CanID 33 encoder readings = 322.3, 172, 49.2

}
