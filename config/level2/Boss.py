import numpy as np
import sys
import random

class Unit:

    def __init__(self, id_, faction, unit_type, health, x, y, vx, vy):
        self.id = id_
        self.faction = faction
        self.unit_type = unit_type
        self.health = health
        self.pos = np.array([x,y], dtype=float)
        self.vel = np.array([vx,vy], dtype=float)

def length(x):
    return np.sqrt(x[0]**2 + x[1]**2)

def sign(x):
    if x > 0:
        return 1
    if x < 0:
        return -1
    return 0

turn = 0

while True:

    turn += 1

    count = int(input())
    our_units = []
    enemy_units = []

    pos = np.array([0,0], dtype=float)
    vel = np.array([0,0], dtype=float)

    for i in range(count):

        unit_id, faction, unit_type, health, posX, posY, velX, velY, _ = input().split()
        unit_id, faction, unit_type, health, posX, posY, velX, velY =\
            int(unit_id), int(faction), unit_type, float(health), float(posX), float(posY), float(velX), float(velY)
        u = Unit(unit_id, faction, unit_type, health, posX, posY, velX, velY)
        if faction == 1:
            our_units.append(u)
        else:
            enemy_units.append(u)

        if u.unit_type == "S" and u.faction == 1:
            pos = u.pos
            vel = u.vel

    for u in our_units:
        target_pos = np.array([10000,10000], dtype=float)
        target_vel = np.array([0,0], dtype=float)

        if u.unit_type == "S":
            for s in enemy_units:
                if s.unit_type == "S" or s.unit_type == "M":
                    t_pos = s.pos - u.pos
                    if length(target_pos) > length(t_pos):
                        target_pos = t_pos
                        target_vel = s.vel - u.vel

            target = target_pos
            print("%d | F %.2f %.2f | A %.2f %.2f | M %.2f %.2f" % (u.id, target[0], target[1],0, 0, 2*random.random()-1,2*random.random()-1))

        if u.unit_type == "M":
            for s in enemy_units:
                if s.unit_type == "S":
                    t_pos = s.pos - u.pos
                    if length(target_pos) > length(t_pos):
                        target_pos = t_pos
                        target_vel = s.vel - u.vel
            target = target_pos
            if length(target_pos) < 100:
                print("%d | D" % (u.id,))
            else:
                print("%d | A %.2f %.2f" % (u.id, u.vel[0]*1000, u.vel[1]*1000))
