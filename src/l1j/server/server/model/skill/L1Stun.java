package l1j.server.server.model.skill;

import l1j.server.server.model.Instance.*;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Magic;
import l1j.server.server.random.RandomGenerator;
import l1j.server.server.random.RandomGeneratorFactory;
import l1j.server.server.serverpackets.S_Paralysis;

// return a value so it can prevent stun from stacking - [Hank]
public class L1Stun {
    public static int Stun(L1Character attacker, L1Character target, int skillId) {
        int _stunDuration = 0;
        try {

            int targetLevel = 0;
            int diffLevel = 0;
            int stunTime = 0;
            // change attacker to target - [Hank]
            if (target instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) target;
                targetLevel = pc.getLevel();
            }
            else if (target instanceof L1MonsterInstance || target instanceof L1SummonInstance || target instanceof L1PetInstance) {
                L1NpcInstance npc = (L1NpcInstance) attacker;
                targetLevel = npc.getLevel();
            }
            else {
                targetLevel = 0;
            }

            diffLevel = attacker.getLevel() - targetLevel;
            RandomGenerator random = RandomGeneratorFactory.getSharedRandom();

            int basechance = random.nextInt(99) + 1;

            int chance = basechance+(diffLevel*5);

            if(target instanceof  L1PcInstance)
            {
                chance -= target.getRegistStun();
            }
            else if (attacker instanceof L1MonsterInstance || attacker instanceof L1SummonInstance || attacker instanceof L1PetInstance) {
                int mobStunResist = targetLevel/2;
                chance -= mobStunResist;
            }

            if(skillId == L1SkillId.SHOCK_STUN) {
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
            else if (skillId == L1SkillId.BONE_BREAK) {
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
            else
            {
                //This will be for other skills. This is temporary
                stunTime = 1000;
            }

            //This ensures that pvp stuns are never longer than 5 seconds
            if (attacker instanceof  L1PcInstance && target instanceof  L1PcInstance) {
                stunTime = Math.min(5000, stunTime);
            }
            //This makes it so mobs/bosses that use ShockStun can only stun for 3s max.
            if (attacker instanceof L1MonsterInstance && target instanceof  L1PcInstance) {
                stunTime = Math.min(3000, stunTime);
            }

            //If this is a player hitting a monster/boss
            if(attacker instanceof  L1PcInstance && target instanceof L1MonsterInstance) {
                int maxPveStun= random.nextInt(12) + 1;
                stunTime = Math.min(maxPveStun*500, stunTime);
            }

            _stunDuration = stunTime;
            L1Magic _magic = new L1Magic(attacker, target);
            //L1EffectSpawn.getInstance().spawnEffect(81162, _stunDuration, target.getX(), target.getY(), target.getMapId());
            if (attacker instanceof L1PcInstance) {
                //change attacker to target - [Hank]
                L1PcInstance pc = (L1PcInstance) target;
                // adding this so it reads the actual probability for bone break - [Hank]
                if(skillId == L1SkillId.BONE_BREAK && _magic.calcProbabilityMagic(skillId))
                {
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN,true));
                }
                else
                {
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN,true));
                }

            } else if (attacker instanceof L1MonsterInstance || attacker instanceof L1SummonInstance || attacker instanceof L1PetInstance) {
                //change attacker to target - [Hank]
                L1NpcInstance npc = (L1NpcInstance) target;
                // adding this so it reads the actual probability for bone break - [Hank]
                if(skillId == L1SkillId.BONE_BREAK && _magic.calcProbabilityMagic(skillId))
                {
                    npc.setParalyzed(true);
                    npc.setParalysisTime(_stunDuration);
                }
                else
                {
                    npc.setParalyzed(true);
                    npc.setParalysisTime(_stunDuration);
                }

            }
        }
        catch(Exception e) {
            System.out.println("Error Stunning");
        }
        return _stunDuration;
    }
}