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

def gun_solver(relative_pos, relative_vel, bullet_speed):
    a = 2 - bullet_speed**2
    b = (2 * relative_vel * relative_pos).sum()
    c = (relative_pos ** 2).sum()
    delta = b**2 - 4*a*c
    if delta < 0:
        print ("no gun solution", file=sys.stderr)
        return None

    t1 = (-b - np.sqrt(delta))/(2*a)
    t2 = (-b + np.sqrt(delta))/(2*a)

    if (t1 > 0):
        print ("gun solution found, intersect in ", t1, file=sys.stderr)
        return relative_pos + relative_vel*t1
    else:
        print ("gun solution found, intersect in ", t2, file=sys.stderr)
        return relative_pos + relative_vel*t2


def missile_solver(relative_pos, relative_vel, missile_accel):
    a = 2
    b = (2 * relative_vel * relative_pos).sum()
    c = (relative_pos ** 2).sum()
    e = - 1/4 *  missile_accel**2
    def f(t):
        return e*t**4 + a*t**2 + b*t + c
    def fprim(t):
        return 4*e*t**3 + 2*a*t + b
    t = 0
    for i in range(20):
        t -= f(t) / fprim(t)

    print ("intersect in ", t,"f(t)=", f(t), file=sys.stderr)
    return relative_pos + relative_vel*t

print("uuu", file=sys.stderr)

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
    print(count, file=sys.stderr)
    our_units = []
    enemy_units = []

    pos = np.array([0,0], dtype=float)
    vel = np.array([0,0], dtype=float)

    for i in range(count):
        # int unit_id
        # int faction
        # String type
        # double health
        # double posX
        # double posY
        # double velX
        # double velY

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

    closest_pass = 10000
    dodge_dir = np.array([2*random.random()-1,2*random.random()-1], dtype=float)
    for u in enemy_units:
        if u.unit_type != "S":
            en_pos = pos - u.pos
            en_vel = u.vel - vel

            cos = (en_pos * en_vel).sum() / length(en_pos) / length(en_vel)
            print("cos: ",cos, file=sys.stderr)
            closest_point = cos * en_vel / length(en_vel) * length(en_pos) - en_pos
            print("pos of the closest point: ",closest_point, file=sys.stderr)
            print("dist from ship: ",length(closest_point), file=sys.stderr)
            if length(closest_point) < closest_pass:
                closest_pass = length(closest_point)
                if length(closest_point) > 0:
                    dodge_dir = -closest_point
                else:
                    dodge_dir = np.array([-en_vel[1],en_vel[0]])

    print("closest pass: ",closest_pass, file=sys.stderr)

    full_stop_x = abs(vel[0]/10)
    full_stop_y = abs(vel[1]/10)

    pos_full_stop_x = -sign(vel[0])*10/2*full_stop_x**2 + vel[0]*full_stop_x + pos[0]
    pos_full_stop_y = -sign(vel[1])*10/2*full_stop_y**2 + vel[1]*full_stop_y + pos[1]
    if pos_full_stop_x > 1700 or pos_full_stop_x < 200:
        print("braking x", file=sys.stderr)
        dodge_dir[0] = -sign(vel[0])*10
        dodge_dir[1] = 0

    if pos_full_stop_y > 800 or pos_full_stop_y < 200:
        print("braking y", file=sys.stderr)
        dodge_dir[1] = -sign(vel[1])*10
        dodge_dir[0] = 0

    if pos[0] < 100:
        dodge_dir[0] = 1000
    if pos[1] < 100:
        dodge_dir[1] = 1000

    if pos[0] > 1800:
        dodge_dir[0] = -1000
    if pos[1] > 900:
        dodge_dir[1] = -1000

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
            gun_solution = gun_solver(target_pos, target_vel, 100)
            if gun_solution is not None:
                target = gun_solution
            print(target, file=sys.stderr)
            if turn % 3 == 0:
                print("%d | F %.2f %.2f | A %.2f %.2f | M %.2f %.2f" % (u.id, target[0], target[1], dodge_dir[0], dodge_dir[1], 2*random.random()-1,2*random.random()-1))
            else:
                print("%d | F %.2f %.2f | A %.2f %.2f " % (u.id, target[0], target[1], dodge_dir[0], dodge_dir[1]))

        if u.unit_type == "M":
            for s in enemy_units:
                if s.unit_type == "S":
                    t_pos = s.pos - u.pos
                    if length(target_pos) > length(t_pos):
                        target_pos = t_pos
                        target_vel = s.vel - u.vel
            target = target_pos
            missile_solution = missile_solver(target_pos, target_vel, 30)
            print(missile_solution, file=sys.stderr)
            if length(target_pos) < 100:
                print("%d | D" % (u.id,))
            else:
                print("%d | A %.2f %.2f" % (u.id, missile_solution[0], missile_solution[1]))




