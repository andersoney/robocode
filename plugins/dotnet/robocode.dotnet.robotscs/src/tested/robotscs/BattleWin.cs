/**
 * Copyright (c) 2001-2018 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using Robocode;

namespace tested.robotscs
{
    public class BattleWin : Robot
    {
        public override void Run()
        {
            while (true)
            {
                Ahead(100); // Move ahead 100
                TurnGunRight(360); // Spin gun around
                Back(100); // Move back 100
                TurnGunRight(360); // Spin gun around
            }
        }


        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Fire(1);
        }


        public override void OnHitByBullet(HitByBulletEvent e)
        {
            TurnLeft(90 - e.Bearing);
        }


        public override void OnWin(WinEvent e)
        {
            Out.WriteLine("Win!");
        }


        public override void OnDeath(DeathEvent e)
        {
            Out.WriteLine("Death!");
        }


        public override void OnBattleEnded(BattleEndedEvent evnt)
        {
            Out.WriteLine("BattleEnded!");
        }

        public override void OnRoundEnded(RoundEndedEvent evnt)
        {
            Out.WriteLine("RoundEnded!");
        }
    }
}