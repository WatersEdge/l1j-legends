package l1j.server.server.model.skill;


import static l1j.server.server.model.skill.L1SkillName.*;
import l1j.server.server.model.Instance.*;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.random.RandomGenerator;
import l1j.server.server.random.RandomGeneratorFactory;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;

import java.util.logging.Level;
import java.util.logging.Logger;

public class L1Hold {
    private static Logger _log = Logger.getLogger("L1Hold");

    public static int Hold(L1Character attacker, L1Character target, int skillId) {
        try {
            int targetLevel;
            int diffLevel;
            int stunTime = 0;

            //Prevent hold stacking
            if(target.hasSkillEffect(skillId))
            {
                return 0;
            }

            if (target instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) target;
                targetLevel = pc.getLevel();
            }
            else if (target instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) target;
                targetLevel = npc.getLevel();
            }
            //If it isnt a Player or an NPC return 0;
            else {
                return 0;
            }

            diffLevel = attacker.getLevel() - targetLevel;
            RandomGenerator random = RandomGeneratorFactory.getSharedRandom();

            int basechance = random.nextInt(99) + 1;

            int chance = basechance+(diffLevel*5);

            if(target instanceof  L1PcInstance)
            {
                chance -= target.getRegistHold();
            }
            else if (attacker instanceof L1NpcInstance) {
                int mobStunResist = targetLevel/2;
                chance -= mobStunResist;
            }

            if(skillId == Skill_ShockStun) {
                if (chance>90) {
                    stunTime = 6000;
                } else if (chance > 85) {
                    stunTime = 5500;
                } else if (chance > 80) {
                    stunTime = 5000;
                } else if (chance > 75) {
                    stunTime = 4500;
                } else if (chance > 70) {
                    stunTime = 4000;
                } else if (chance > 65) {
                    stunTime = 3500;
                } else if (chance > 60) {
                    stunTime = 3000;
                } else if (chance > 55) {
                    stunTime = 2500;
                } else if (chance > 50) {
                    stunTime = 2000;
                } else if (chance > 40) {
                    stunTime = 1500;
                } else if (chance > 30) {
                    stunTime = 1000;
                } else {
                    stunTime = 500;
                }
            }
            else if (skillId == Skill_BoneBreak) {
                if (chance>90) {
                    stunTime = 4000;
                } else if (chance > 70) {
                    stunTime = 3500;
                } else if (chance > 50) {
                    stunTime = 3000;
                } else if (chance > 30) {
                    stunTime = 2500;
                } else if (chance > 10) {
                    stunTime = 2000;
                } else {
                    stunTime = 1000;
                }
            }
            else if (skillId == Skill_EarthBind) {
                if (chance>90) {
                    stunTime = 16000;
                } else if (chance > 85) {
                    stunTime = 15000;
                } else if (chance > 80) {
                    stunTime =14000;
                } else if (chance > 75) {
                    stunTime = 13000;
                } else if (chance > 70) {
                    stunTime = 12000;
                } else if (chance > 65) {
                    stunTime = 10000;
                } else if (chance > 60) {
                    stunTime = 9000;
                } else if (chance > 55) {
                    stunTime = 8000;
                } else if (chance > 50) {
                    stunTime = 7000;
                } else {
                    stunTime = 6000;
                }
            }
            else if (skillId == Skill_Paralyze) {
                if (chance>90) {
                    stunTime = 8000;
                } else if (chance > 70) {
                    stunTime = 700;
                } else if (chance > 50) {
                    stunTime = 6000;
                } else if (chance > 30) {
                    stunTime = 5000;
                } else if (chance > 10) {
                    stunTime = 4000;
                } else {
                    stunTime = 3000;
                }
            }

            //This ensures that pvp stuns are never longer than 5 seconds
            if (attacker instanceof L1PcInstance && target instanceof  L1PcInstance) {
                stunTime = Math.min(5000, stunTime);
            }

            //This makes it so mobs/bosses that use Hold Abilities can only hold a player for 3s max.
            if (attacker instanceof L1NpcInstance && target instanceof L1PcInstance) {
                stunTime = Math.min(3000, stunTime);
            }

            //If this is a player hitting a monster/boss
            if(attacker instanceof L1PcInstance && target instanceof L1NpcInstance) {
                int maxPveStun= random.nextInt(12) + 1;
                stunTime = Math.min(maxPveStun*500, stunTime);
            }

            //Call function to draw the stun animation icon
            Animate(target,skillId,stunTime);
            return stunTime;
        }
        catch(Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            System.out.println(e.getMessage());
        }
        //If we get here, something went wrong with the stun duration calc so we return 0;
        return 0;
    }

    private static void Animate(L1Character target, int skillId, int stunTime)
    {
        if(skillId == Skill_ShockStun || skillId == Skill_BoneBreak)
        {
            L1EffectSpawn.getInstance().spawnEffect(81162, stunTime, target.getX(), target.getY(), target.getMapId());
            if (target instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) target;
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN,true));
            } else if (target instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) target;
                npc.setParalyzed(true);
                npc.setParalysisTime(stunTime);
            }
        }
        if(skillId == Skill_EarthBind)
        {
            L1EffectSpawn.getInstance().spawnEffect(97076, stunTime, target.getX(), target.getY(), target.getMapId());
            if (target instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) target;
                pc.sendPackets(new S_Poison(pc.getId(), 2));
                pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
            }
            else if (target instanceof  L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) target;
                npc.broadcastPacket(new S_Poison(npc.getId(), 2));
                npc.setParalyzed(true);
                npc.setParalysisTime(stunTime);
            }
        }
    }
}