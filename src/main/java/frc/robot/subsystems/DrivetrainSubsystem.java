// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.swervedrivespecialties.swervelib.SwerveModule;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import static frc.robot.Constants.*;

/**
 * Note: These swerve modules are "SDS MK4i w/ a L2 Gear Ratio using Neos":
 * https://www.swervedrivespecialties.com/products/mk4i-swerve-module
 * 
 * Note: This code is based upon the SDS templates found on GitHub:
 * https://github.com/SwerveDriveSpecialties/swerve-template
 */
public class DrivetrainSubsystem extends SubsystemBase {

  /**
   * The maximum voltage that will be delivered to the drive motors.
   * This can be reduced to cap the robot's maximum speed.
   */
  public static final double MAX_VOLTAGE = 12.0;

  /**
   * The robot's maximum velocity in a straight line in meters per second.
   * <Motor's max RPM> / 60 * <Drive reduction> * <Wheel diameter meters> * pi
   * 
   * Falcon max RPM = 6380
   * Neo max RPM = 5880
   */
  public static final double MAX_VELOCITY_METERS_PER_SECOND = 5880.0 / 60.0 *
      SdsModuleConfigurations.MK4I_L2.getDriveReduction() *
      SdsModuleConfigurations.MK4I_L2.getWheelDiameter() * Math.PI;

  /**
   * The robot's maximum angular velocity in radians per second.
   * This is a measure of how fast the robot can rotate in place.
   */
  public static final double MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND = MAX_VELOCITY_METERS_PER_SECOND /
      Math.hypot(DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0);

  /**
   * Constructs a swerve drive kinematics object.
   * Note: Order of class instantiation matters.
   */
  private final SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
      new Translation2d(DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0), // Front Left
      new Translation2d(DRIVETRAIN_TRACKWIDTH_METERS / 2.0, -DRIVETRAIN_WHEELBASE_METERS / 2.0), // Front Right
      new Translation2d(-DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0), // Back Left
      new Translation2d(-DRIVETRAIN_TRACKWIDTH_METERS / 2.0, -DRIVETRAIN_WHEELBASE_METERS / 2.0)); // Back Right

  /**
   * Instantiate a NavX connected using the RoboRio's MXP port.
   * Note: Rotating robot counter-clockwise should increase the angle reading.
   */
  private final AHRS m_navx = new AHRS(SPI.Port.kMXP, (byte) 200);

  // These swerve modules are initialized in the constructor.
  private final SwerveModule m_frontLeftModule;
  private final SwerveModule m_frontRightModule;
  private final SwerveModule m_backLeftModule;
  private final SwerveModule m_backRightModule;

  // Initial chassis speed should be set to zero so that robot is stationary.
  private ChassisSpeeds m_chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);

  public DrivetrainSubsystem() {

    // Shuffleboard object to print values for calibration
    ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");

    // Front Left Swerve Module
    m_frontLeftModule = Mk4iSwerveModuleHelper.createNeo(
        tab.getLayout("Front Left Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(0, 0),
        Mk4iSwerveModuleHelper.GearRatio.L2,
        FRONT_LEFT_MODULE_DRIVE_MOTOR,
        FRONT_LEFT_MODULE_STEER_MOTOR,
        FRONT_LEFT_MODULE_STEER_ENCODER,
        FRONT_LEFT_MODULE_STEER_OFFSET);

    // Front Right Swerve Module
    m_frontRightModule = Mk4iSwerveModuleHelper.createNeo(
        tab.getLayout("Front Right Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(2, 0),
        Mk4iSwerveModuleHelper.GearRatio.L2,
        FRONT_RIGHT_MODULE_DRIVE_MOTOR,
        FRONT_RIGHT_MODULE_STEER_MOTOR,
        FRONT_RIGHT_MODULE_STEER_ENCODER,
        FRONT_RIGHT_MODULE_STEER_OFFSET);

    // Back Left Swerve Module
    m_backLeftModule = Mk4iSwerveModuleHelper.createNeo(
        tab.getLayout("Back Left Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(4, 0),
        Mk4iSwerveModuleHelper.GearRatio.L2,
        BACK_LEFT_MODULE_DRIVE_MOTOR,
        BACK_LEFT_MODULE_STEER_MOTOR,
        BACK_LEFT_MODULE_STEER_ENCODER,
        BACK_LEFT_MODULE_STEER_OFFSET);

    // Back Right Swerve Module
    m_backRightModule = Mk4iSwerveModuleHelper.createNeo(
        tab.getLayout("Back Right Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(6, 0),
        Mk4iSwerveModuleHelper.GearRatio.L2,
        BACK_RIGHT_MODULE_DRIVE_MOTOR,
        BACK_RIGHT_MODULE_STEER_MOTOR,
        BACK_RIGHT_MODULE_STEER_ENCODER,
        BACK_RIGHT_MODULE_STEER_OFFSET);
  }

  /**
   * Set the gyroscope angle to zero.
   * This resets the 'forward direction' of the robot to the current heading.
   */
  public void zeroGyroscope() {
    m_navx.zeroYaw();
  }

  public Rotation2d getGyroscopeRotation() {

    // Return fused headings if the magnetometer is calibrated.
    if (m_navx.isMagnetometerCalibrated()) {
      return Rotation2d.fromDegrees(m_navx.getFusedHeading());
    }

    // Invert the angle of the NavX so that rotating the robot
    // counter-clockwise icreases the angle.
    return Rotation2d.fromDegrees(360.0 - m_navx.getYaw());
  }

  public void displayNavXAngle() {
    System.out.println(360.0 - m_navx.getYaw());
  }

  public void drive(ChassisSpeeds chassisSpeeds) {
    m_chassisSpeeds = chassisSpeeds;
  }

  @Override
  public void periodic() {

    SwerveModuleState[] states = m_kinematics.toSwerveModuleStates(m_chassisSpeeds);
    SwerveDriveKinematics.desaturateWheelSpeeds(states, MAX_VELOCITY_METERS_PER_SECOND);

    m_frontLeftModule.set(states[0].speedMetersPerSecond /
        MAX_VELOCITY_METERS_PER_SECOND * MAX_VOLTAGE,
        states[0].angle.getRadians());
    m_frontRightModule.set(states[1].speedMetersPerSecond /
        MAX_VELOCITY_METERS_PER_SECOND * MAX_VOLTAGE,
        states[1].angle.getRadians());
    m_backLeftModule.set(states[2].speedMetersPerSecond /
        MAX_VELOCITY_METERS_PER_SECOND * MAX_VOLTAGE,
        states[2].angle.getRadians());
    m_backRightModule.set(states[3].speedMetersPerSecond /
        MAX_VELOCITY_METERS_PER_SECOND * MAX_VOLTAGE,
        states[3].angle.getRadians());
  }
}
