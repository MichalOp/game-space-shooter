import sys
import math
import random

MAP_X = 1700
MAP_Y = 1080

class Vector2d:
    
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def add(self, other):
        return Vector2d(self.x + other.x, self.y + other.y)

    def mul(self, s):
        return Vector2d(self.x*s, self.y*s)

    def length(self):
        return math.sqrt(self.x*self.x + self.y*self.y)

    def distance(self, v2):
        return self.add(v2.mul(-1.0)).length()


class Unit:
    
    def __init__(self, unitId, faction, utype, health, posX, posY, velX, velY):
        self.unitID = unitId
        self.faction = faction
        self.utype = utype
        self.heath = health
        self.pos = Vector2d(posX, posY)
        self.vel = Vector2d(velX, velY)


def get_closest_enemy_ship_position(my_pos, opp_ship, opp_units):
    C = 1000
    m = 100000
    pos = None
    for unitID in opp_units:
        unit = opp_units[unitID]
        # print(unit.utype, file=sys.stderr)
        dist = my_pos.distance(unit.pos)
        if dist < m and unit.utype == 'M':
            pos = unit.pos
            m = dist
    if pos is None or m > C:
        return opp_ship
    return pos

def get_dist_to_wall(my_ship):
    x = my_ship.pos.x - MAP_X
    if abs(x) > my_ship.pos.x:
        x = my_ship.pos.x

    y = my_ship.pos.y - MAP_Y
    if abs(y) > my_ship.pos.y:
        y = my_ship.pos.y

    return Vector2d(x,y)
    
def run_from_wall(my_ship):
    dists = get_dist_to_wall(my_ship)
    if abs(dists.x) < 400 or abs(dists.y) < 400:
        return dists
    else:
        return None

def run_from_bullets(my_ship, opp_units, prev_opp_units):
    for unitID in opp_units:
        unit = opp_units[unitID]
        dist = my_ship.pos.distance(unit.pos)
        if dist < 400:
            if unit.utype == 'B':
                if unitID in prev_opp_units:
                    x = unit.pos.x - prev_opp_units[unitID].pos.x
                    y = unit.pos.x - prev_opp_units[unitID].pos.y
                    return Vector2d(-y, x)
    return None


def newton(f, fprim, x):
    for i in range(20):
        if fprim(x) == 0:
            return x
        x -= f(x) / fprim(x)
    return x
    
# not ready
def time_to_shoot_order4(my_pos, opp_pos, my_vel, opp_vel, opp_acc):
    x = opp_pos.x - my_pos.x
    y = opp_pos.y - my_pos.y
    vx = opp_vel.x - my_vel.x
    vy = opp_vel.y - my_vel.y
    ax = opp_acc.x
    ay = opp_acc.y

    def f(t):
        x**2 + 2*x*vx*t + vx**2 * t**2 + 2*x*ax*t**2 + 2*vx*ax*t**3 + ax**2 * t**4 + \
        y**2 + 2*y*vy*t + vy**2 * t**2 + 2*y*ay*t**2 + 2*vy*ay*t**3 + ay**2 * t**4
    
    def fprim(t):
        2*x*vx + 2*vx**2 * t + 4*x*ax*t + 6*vx*ax*t**2 + 4*ax**2 * t**3 + \
        2*y*vy + 2*vy**2 * t + 4*y*ay*t + 6*vy*ay*t**2 + 4*ay**2 * t**3

    

def time_to_shoot(my_pos, opp_pos, my_vel, opp_vel, opp_acc):
    x = opp_pos.x - my_pos.x
    y = opp_pos.y - my_pos.y
    vx = opp_vel.x - my_vel.x
    vy = opp_vel.y - my_vel.y
    a = -10000 + vx**2  + vy**2
    b = 2*x*vx + 2*y*vy
    c = x**2 + y**2
    delta = b**2 - 4*a*c
    if delta < 0:
        return None
    t_1 = (-b + math.sqrt(delta)) / (2*a)
    t_2 = (-b - math.sqrt(delta)) / (2*a)
    if t_1 < 0:
        return t_2
    if t_2 < 0:
        return t_1
    return min(t_1, t_2)

def pos_to_shoot(my_pos, opp_pos, my_vel, opp_vel, opp_acc, t):
    x = opp_pos.x + opp_vel.x * t #+ opp_acc.x * t**2
    y = opp_pos.y + opp_vel.y * t #+ opp_acc.y * t**2
    dx = x - my_pos.x
    dy = y - my_pos.y
    dx -= my_vel.x
    dy -= my_vel.y
    shoot = Vector2d(dx, dy)
    shoot = shoot.mul(1 / shoot.length()).mul(100)
    return shoot

def main():

    prev_opp_units = {}
    prev_opp_ship = None
    my_units = {}
    opp_units = {}
    opp_ship = None

    while 1:

        for k,v in opp_units.items():
            prev_opp_units[k] = v
        prev_opp_ship = opp_ship

        units = int(input())
        my_units = {}
        opp_units = {}
        my_ship = None
        opp_ship = None

        print(units, file=sys.stderr, flush=True)

        for i in range(units):
            rest = input().split()
            unitId, faction, utype, health, posX, posY, velX, velY, cooldown = rest
            unitId = int(unitId)
            faction = (int(faction == '1'))
            health = float(health)
            posX = float(posX)
            posY = float(posY)
            velX = float(velX)
            velY = float(velY)
            unit = Unit(unitId, faction, utype, health, posX, posY, velX, velY)
            cooldown = float(cooldown)
            if faction == 1:
                if utype == 'S':
                    my_ship = unit
                else:
                    my_units[unitId] = unit
            else:
                if utype == 'S':
                    opp_ship = unit
                else:
                    opp_units[unitId] = unit

        if prev_opp_ship is None:
            prev_opp_ship = opp_ship

        opp_acc = opp_ship.vel.add(prev_opp_ship.vel.mul(-1.0))
        closest = get_closest_enemy_ship_position(my_ship.pos, opp_ship.pos, opp_units)
        print("closest:", closest.x, closest.y, file=sys.stderr)
        t = time_to_shoot(my_ship.pos, closest, my_ship.vel, opp_ship.vel, opp_acc)
        if t is not None:
            shoot = pos_to_shoot(my_ship.pos, closest, my_ship.vel, opp_ship.vel, opp_acc, t)
            print("shooting:", shoot.x, shoot.y, file=sys.stderr)
            move = run_from_wall(my_ship)
            if move is None:
                move = run_from_bullets(my_ship, opp_units, prev_opp_units)
            if move is None:
                move = Vector2d(random.randint(1,20), random.randint(1,20))
            missile = ' | M ' +  str(shoot.x) +' '+ str(shoot.y)
            # shoot = shoot.add(move.mul(-1.0))
            print(my_ship.unitID, '| F', shoot.x, shoot.y, '| A', move.x, move.y, end='')
            if random.random() < 0.4:
                print(missile)
            else:
                print()
        else:
            print(my_ship.unitID, '|')

        for unitID in my_units:
            unit = my_units[unitID]
            if unit.utype == 'M':
                dist = unit.pos.distance(opp_ship.pos)
                if dist < 150:
                    print(unitID, '|', 'D')
                else:
                    if t is not None:
                        shoot = pos_to_shoot(unit.pos, closest, unit.vel, opp_ship.vel, opp_acc, t)
                        # acc = shoot.add(unit.pos.mul(-1.0))
                        print(unitID, '|', 'A', shoot.x, shoot.y)
                    else:
                        print(unitID, '|', 'W')


main()