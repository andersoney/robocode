/*******************************************************************************
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.battle.peer;


import robocode.BattleResults;
import java.util.Map;
import java.util.HashMap;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Titus Chen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobotStatistics implements ContestantStatistics {

	private final RobotPeer robotPeer;
	private int rank;
	private final int numberOfRobots;
	private boolean isActive;
	private boolean isInRound;

	private double survivalScore;
	private double lastSurvivorBonus;
	private double bulletDamageScore;
	private double bulletKillBonus;
	private double rammingDamageScore;
	private double rammingKillBonus;

	private Map<String, Double> robotDamageMap;

	protected double totalScore;
	private double totalSurvivalScore;
	private double totalLastSurvivorBonus;
	private double totalBulletDamageScore;
	private double totalBulletKillBonus;
	private double totalRammingDamageScore;
	private double totalRammingKillBonus;

	private int totalFirsts;
	private int totalSeconds;
	private int totalThirds;

	RobotStatistics(RobotPeer robotPeer, int numberOfRobots) {
		super();
		this.robotPeer = robotPeer;
		this.numberOfRobots = numberOfRobots;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	void reset() {
		resetScores();

		setActive(true);
		isInRound = true;
	}

	protected void resetScores() {
		robotDamageMap = null;
		survivalScore = 0;
		lastSurvivorBonus = 0;
		bulletDamageScore = 0;
		bulletKillBonus = 0;
		rammingDamageScore = 0;
		rammingKillBonus = 0;
	}

	public void generateTotals() {
		totalSurvivalScore += survivalScore;
		totalLastSurvivorBonus += lastSurvivorBonus;
		totalBulletDamageScore += bulletDamageScore;
		totalBulletKillBonus += bulletKillBonus;
		totalRammingDamageScore += rammingDamageScore;
		totalRammingKillBonus += rammingKillBonus;

		totalScore = getRobotPeer().isSentryRobot()
				? 0
				: totalBulletDamageScore + totalRammingDamageScore + totalSurvivalScore + totalRammingKillBonus
				+ totalBulletKillBonus + totalLastSurvivorBonus;

		isInRound = false;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public double getTotalSurvivalScore() {
		return totalSurvivalScore;
	}

	public double getTotalLastSurvivorBonus() {
		return totalLastSurvivorBonus;
	}

	public double getTotalBulletDamageScore() {
		return totalBulletDamageScore;
	}

	public double getTotalBulletKillBonus() {
		return totalBulletKillBonus;
	}

	public double getTotalRammingDamageScore() {
		return totalRammingDamageScore;
	}

	public double getTotalRammingKillBonus() {
		return totalRammingKillBonus;
	}

	public int getTotalFirsts() {
		return totalFirsts;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public int getTotalThirds() {
		return totalThirds;
	}

	public double getCurrentScore() {
		return getRobotPeer().isSentryRobot()
				? 0
				: (bulletDamageScore + rammingDamageScore + survivalScore + rammingKillBonus + bulletKillBonus
				+ lastSurvivorBonus);
	}

	public double getCurrentSurvivalScore() {
		return survivalScore;
	}

	public double getCurrentSurvivalBonus() {
		return lastSurvivorBonus;
	}

	public double getCurrentBulletDamageScore() {
		return bulletDamageScore;
	}

	public double getCurrentBulletKillBonus() {
		return bulletKillBonus;
	}

	public double getCurrentRammingDamageScore() {
		return rammingDamageScore;
	}

	public double getCurrentRammingKillBonus() {
		return rammingKillBonus;
	}

	public void scoreSurvival() {
		if (isActive() && !getRobotPeer().isSentryRobot()) {
			survivalScore += 50;
		}
	}

	public void scoreLastSurvivor() {
		if (isActive() && !getRobotPeer().isSentryRobot()) {
			int enemyCount = numberOfRobots - 1;

			if (getRobotPeer().getTeamPeer() != null) {
				enemyCount -= (getRobotPeer().getTeamPeer().size() - 1);
			}

			lastSurvivorBonus += 10 * enemyCount;

			if ((getRobotPeer().getTeamPeer() == null || getRobotPeer().isTeamLeader())) {
				totalFirsts++;
			}
		}
	}

	void scoreBulletDamage(String robot, double damage) {
		if (isActive()) {
			incrementRobotDamage(robot, damage);
			bulletDamageScore += damage;
		}
	}

	double scoreBulletKill(String robot) {
		if (isActive()) {
			double bonus;

			if (getRobotPeer().getTeamPeer() == null) {
				bonus = getRobotDamage(robot) * 0.20;
			} else {
				bonus = 0;
				for (RobotPeer teammate : getRobotPeer().getTeamPeer()) {
					bonus += teammate.getRobotStatistics().getRobotDamage(robot) * 0.20;
				}
			}

			bulletKillBonus += bonus;
			return bonus;
		}
		return 0;
	}

	void scoreRammingDamage(String robot) {
		if (isActive()) {
			incrementRobotDamage(robot, robocode.Rules.ROBOT_HIT_DAMAGE);
			rammingDamageScore += robocode.Rules.ROBOT_HIT_BONUS;
		}
	}

	double scoreRammingKill(String robot) {
		if (isActive()) {
			double bonus;

			if (getRobotPeer().getTeamPeer() == null) {
				bonus = getRobotDamage(robot) * 0.30;
			} else {
				bonus = 0;
				for (RobotPeer teammate : getRobotPeer().getTeamPeer()) {
					bonus += teammate.getRobotStatistics().getRobotDamage(robot) * 0.30;
				}
			}
			rammingKillBonus += bonus;
			return bonus;
		}
		return 0;
	}

	public void scoreRobotDeath(int enemiesRemaining) {
		if (!getRobotPeer().isSentryRobot()) {
			switch (enemiesRemaining) {
			case 0:
				if (!getRobotPeer().isWinner()) {
					totalFirsts++;
				}
				break;
	
			case 1:
				totalSeconds++;
				break;
	
			case 2:
				totalThirds++;
				break;
			}
		}
	}

	public void scoreFirsts() {
		if (isActive() && !getRobotPeer().isSentryRobot()) {
			totalFirsts++;
		}
	}

	void setInactive() {
		resetScores();
		setActive(false);
	}

	public BattleResults getFinalResults() {
		return new BattleResults(getRobotPeer().getTeamName(), getRank(), totalScore, totalSurvivalScore, totalLastSurvivorBonus,
				totalBulletDamageScore, totalBulletKillBonus, totalRammingDamageScore, totalRammingKillBonus, totalFirsts,
				totalSeconds, totalThirds);
	}

	protected double getRobotDamage(String robot) {
		if (robotDamageMap == null) {
			robotDamageMap = new HashMap<String, Double>();
		}
		Double damage = robotDamageMap.get(robot);

		return (damage != null) ? damage : 0;
	}

	protected void incrementRobotDamage(String robot, double damage) {
		double newDamage = getRobotDamage(robot) + damage;

		robotDamageMap.put(robot, newDamage);
	}

	void cleanup() {// Do nothing, for now
	}

	public boolean isInRound() {
		return isInRound;
	}

	public int getRank() {
		return rank;
	}

	public RobotPeer getRobotPeer() {
		return robotPeer;
	}

	protected boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}