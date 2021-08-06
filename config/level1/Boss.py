import numpy as np
import random

class Unit:

    def __init__(self, id_, faction, unit_type, health, x, y, vx, vy):
        self.id = id_
        self.faction = faction
        self.unit_type = unit_type
        self.health = health
        self.pos = np.array([x,y], dtype=float)
        self.vel = np.array([vx,vy], dtype=float)

while True:
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
        if u.unit_type == "S":
            print("%d | F %.2f %.2f | A %.2f %.2f" % (u.id, (2*random.random()-1)*1000,(2*random.random()-1)*1000, 0, 0))