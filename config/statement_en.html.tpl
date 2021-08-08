<!-- LEAGUES level1 level2 level3 -->
<div id="statement_back" class="statement_back" style="display: none"></div>
<div class="statement-body">
    <!-- LEAGUE ALERT -->
    <!-- BEGIN level1 level2 -->
    <div style="color: #7cc576; 
  background-color: rgba(124, 197, 118,.1);
  padding: 20px;
  margin-right: 15px;
  margin-left: 15px;
  margin-bottom: 10px;
  text-align: left;">
        <div style="text-align: center; margin-bottom: 6px">
            <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png"/>
        </div>

        <!-- BEGIN level1 -->
        <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
            This is a league based challenge.
        </p>
        <span class="statement-league-alert-content">
      For this challenge, three leagues for the same game are available. Once you have proven your skills against the
      Boss, you will access a higher league and extra rules will be available.
    </span>
        <!-- END -->
        <!-- BEGIN level2 -->
        <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
            Summary of new rules
        </p>
        <span class="statement-league-alert-content">
      You can now use missiles, which have their own engines and can be moved around with your commands. Use them wisely!<br>
      <br>See the updated statement for details.</span>
        <!-- END -->
    </div>
    <!-- END -->
    <!-- BEGIN level1 level2 level3 -->
    <!-- GOAL -->
    <div class="statement-section statement-goal">
        <h2>
            <span class="icon icon-goal">&nbsp;</span> <span>The Goal</span>
        </h2>
        <div class="statement-goal-content">
            <p>Win. Don't die.</p>
            <p>Shoot at your enemy with bullets and missiles, watch out for their weapons.
                Be careful - the forcefield, which was used to trap your enemy, seems to also be keeping you inside.
                Keep away from the edge - crash with the forcefield will destroy your ship!

            </p>
        </div>
    </div>

    <!-- RULES -->
    <div class="statement-section statement-rules">
        <h1>
            <span class="icon icon-rules">&nbsp;</span>
            <span>Rules</span>
        </h1>
        <div class="statement-rules-content">
            <p>The game is played on a map
                <const>1700</const>
                units wide and
                <const>1080</const>
                units high.
            </p>
            <br>

            <p>You and your opponent each own one starship, which has
                <const>10</const>
                health points.
            </p>
            <br>


            <p>The ship has an
                <const>infinite number of bullets</const>
                .
            </p>
            <br>

            <!-- BEGIN level2 level3 -->
            <!-- BEGIN level2 -->
            <div class="statement-new-league-rule">
                <!-- END -->
                <p>Now your weaponry includes also
                    <const>8 missiles.</const>
                </p>
                <!-- BEGIN level2 -->
            </div>
            <!-- END -->
            <br>
            <!-- END -->

            <p>The bullet can be shot every two rounds. It detonates automatically in the first tick, when its distance
                to the closest enemy unit, that was in its damage radius, starts to increase. Ending it's lifetime
                (after
                <const>7</const>
                turns), or going out of health points (a bullet has it's own
                <const>10</const>
                health points) also detonates the bullet.
            </p>
            <p>
                Bullet's damage radius is
                <const>120</const>
                , the damage caused at the bullet's position is
                <const>10</const>
                and decreases linearly with the distance to it, reaching
                <const>0</const>
                at damage radius. The bullet is shot with the given velocities along each axis (in relation to the
                ship), with the resultant velocity being clipped to be at most
                <const>100</const>
                .
            </p>
            <br>

            <!-- BEGIN level2 level3 -->
            <!-- BEGIN level2 -->
            <div class="statement-new-league-rule">
                <!-- END -->
                <p>Missiles can be shot each round, as long as you have one. They are detonated with a command or when
                    they lose all their health points (they start having
                    <const>7</const>
                    of them).
                </p>
                <p>Their damage radius is
                    <const>200</const>
                    , and the damage done is
                    <const>15</const>
                    in the center of explosion, decreasing in the same manner as bullet's.
                    They have their own engines, so they can be moved around just as the spaceship.
                </p>
                <!-- BEGIN level2 -->
            </div>
            <!-- END -->
            <br>
            <!-- END -->

            <p>The ships start at symmetrical positions, close to the center of the board.</p>
            <!-- BEGIN level1 -->
            <p> Ships move with the given acceleration along each axis, with the resultant acceleration (interpreted as
                a vector) being clipped to length
                <const>10</const>
                .
            </p>
            <br>
            <!-- END -->
            <!-- BEGIN level2 level3 -->
            <!-- BEGIN level2 -->
            <div class="statement-new-league-rule">
                <!-- END -->
                <p> Both ships and missiles move with the given acceleration along each axis, with the resultant
                    acceleration (interpreted as a vector) being clipped to length
                    <const>10</const>
                    for the ship,
                    <const>30</const>
                    for a missile.
                </p>
                <!-- BEGIN level2 -->
            </div>
            <!-- END -->
            <br>
            <!-- END -->
            <!-- BEGIN level1 -->
            <p> Hitting the edge of the board immediately sets the unit's (ship's or bullet's) health to
                <const>0</const>
                , causing it to die/detonate.
                <!-- END -->
                <!-- BEGIN level2 level3 -->
            <p> Hitting the edge of the board immediately sets the unit's (bullet's, missile's or ship's) health to
                <const>0</const>
                , causing it to die/detonate.
                <!-- END -->
        </div>
        <br>
        <div>Keep in mind that your own weapons are as harmful to you as your opponent's!</div>
        <br>
        <div>Each game turn consists of
            <const>5</const>
            ticks, so that the units' positions are updated 5 times a turn.
        </div>
        <br>
        <div>
            The player whose ship reaches
            <const>0</const>
            health points first - loses.
        </div>
        <br>
        <div>
            The game lasts up to 100 rounds. If both players survive that long, it's a draw.
        </div>
        <br>
        <div>
            Stats summary:
            <br>
            <br>
            <table style="border: solid 1px black; border-collapse:collapse">
                <tr>
                    <th style="border: solid 1px black; padding: 5px 5px 5px 5px"> unit</th>
                    <th style="border: solid 1px black; padding: 5px 5px 5px 5px"> number of</th>
                    <th style="border: solid 1px black; padding: 5px 5px 5px 5px"> damage</th>
                    <th style="border: solid 1px black; padding: 5px 5px 5px 5px"> damage radius</th>
                    <th style="border: solid 1px black; padding: 5px 5px 5px 5px"> health</th>
                    <th style="border: solid 1px black; padding: 5px 5px 5px 5px"> lifetime</th>
                    <th style="border: solid 1px black; padding: 5px 5px 5px 5px"> max acceleration</th>
                </tr>
                <tr align="center">
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> ship</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 1</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> -</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> -</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 10</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> -</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 10</td>
                </tr>
                <tr align="center">
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> bullet</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> &#8734;</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 10</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 120</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 10</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 7</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> -</td>
                </tr>
                <!-- BEGIN level2 level3 -->
                <!-- BEGIN level3 -->
                <tr align="center">
                    <!-- END -->
                    <!-- BEGIN level2 -->
                <tr align="center" style="color: #7cc576; background-color: rgba(124, 197, 118,.1)">
                    <!-- END -->
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> missile</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 8</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 15</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 200</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 7</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> -</td>
                    <td style="border: solid 1px black; padding: 5px 5px 5px 5px"> 30</td>
                </tr>
                <!-- END -->
            </table>
        </div>
            <div>
            <br>
            <br>
            
            Note that there is a debug mode available in the game settings!
    </div>

    </div>


    <div style="color: #7cc576;
                    background-color: rgba(124, 197, 118,.1);
                    padding: 2px;">
        <p style="text-align:center"><b>Acknowledgment</b></p>
        <p>This contribution was developed for the <i><b>Programming Programming Games</b></i> course, University of
            Wrocław, 2021.</p>
        <p>Authored by <i><b>Michał Opanowicz</b></i> (<a target="_blank"
                                                          href="https://www.codingame.com/profile/c8fbe194ecea052756348280ed3ae8ee6584812">@MichalOp</a>),
            <i><b>Katarzyna Miernikiewicz</b></i> (<a target="_blank"
                                                      href="https://www.codingame.com/profile/068eb3c4e38aeac8038b14288804fb497707273">@Manwi23</a>),
            <i><b>Agnieszka Pawicka</b></i> (<a target="_blank"
                                                href="https://www.codingame.com/profile/c70bc4ee60fd15da0ffe221d1439d82c0542212">@Agn</a>).
        </p>
        <p>Supervised by <i><b>Jakub Kowalski</b></i> (<a target="_blank"
                                                          href="https://www.codingame.com/profile/b528dd3b279d7578674a1129305918e0400484">@aCat</a>).
        </p>
    </div>

    <!-- PROTOCOL -->
    <div class="statement-section statement-protocol">
        <h2>
            <span class="icon icon-protocol">&nbsp;</span>
            <span>Game Input</span>
        </h2>
        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Input for one game turn</div>
            <div class="text"><span class="statement-lineno">Line 1:</span> One integer <var>units</var> for the number
                of units on the board. <br>
                <span class="statement-lineno">Next <var>units</var> lines:</span> Two integers <var>unit_id</var>,
                <var>faction</var>, being the unit's unique ID and faction (
                <const>1</const>
                for the player,
                <const>-1</const>
                for the opponent), one char <var>type</var> being
                <const>S</const>
                hip
                <!-- BEGIN level1 -->
                or
                <!-- END -->
                <!-- BEGIN level2 level3 -->
                ,
                <!-- END -->
                <const>B</const>
                ullet
                <!-- BEGIN level2 level3 -->
                or
                <const>M</const>
                issile
                <!-- END -->
                , six floats <var>health</var>, <var>position_x</var>, <var>position_y</var>, <var>velocity_x</var>,
                <var>velocity_y</var>, <var>gun_cooldown</var> for the unit's health points left, its position on each
                axis and velocity on each axis, followed by gun cooldown, which indicates the number of rounds till the
                next bullet can be shot if this unit is a ship,
                <const>-1</const>
                otherwise.
                <br>
            </div>

        </div>

        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Output for one game turn</div>
            <div class="text">
                <!-- BEGIN level1 -->
                <span class="statement-lineno"><const>1</const> line</span>, a command for your ship in the
                form: <var> unit_id | A x y | F x y </var>, having one or more commands
                for the ship separated by
                <const>|</const>
                .
                <const>unit_id</const>
                is your ship's ID.
                You can also use
                <const>S</const>
                instead of
                <const>unit_id</const>
                .
                <!-- END -->
                <!-- BEGIN level2 level3 -->
                <span class="statement-lineno"><const>n</const> lines</span>, where
                <const>n</const>
                is the number of ships and missiles actively controlled by the player,
                with each line being a command for unit with unit_id in the
                form: <var> unit_id | A x y | F x y </var>, having one or more commands
                for
                <const>unit_id</const>
                separated by
                <const>|</const>
                .
                For a ship
                <const>S</const>
                can be used instead of
                <const>unit_id</const>
                .
                <!-- END -->
                <const>x,y</const>
                are both dot-formatted doubles - if given with more
                precision than 2 decimal places, then rounded up to at most 2 decimal places.
            </div>
            <br>
            Available commands:<br>
            <div>
                <!-- BEGIN level2 level3 -->
                For a ship:
                <!-- END -->
                <ul>

                    <li><var> [A | ACCELERATE] ax ay </var> - put acceleration
                        <const>ax</const>
                        on x-axis,
                        <const>ay</const>
                        on y-axis (with instant effect)
                    </li>
                    <li><var> [F | FIRE] vx vy </var> - fire a bullet with velocity
                        <const>vx</const>
                        on x-axis,
                        <const>vy</const>
                        on y-axis (relative to ship)
                    </li>
                    <!-- BEGIN level2 -->

                    <li>
                        <div class="statement-new-league-rule"><var> [M | MISSILE] ax ay </var> - fire a missile with
                            acceleration
                            <const>ax</const>
                            on x-axis,
                            <const>ay</const>
                            on y-axis, with velocity equal to the current ship's velocity (this missile will be
                            receiving orders starting from the next turn)
                        </div>
                    </li>

                    <!-- END -->
                    <!-- BEGIN level3 -->

                    <li><var> [M | MISSILE] ax ay </var> - fire a missile with acceleration
                        <const>ax</const>
                        on x-axis,
                        <const>ay</const>
                        on y-axis, with velocity equal to the current ship's velocity (this missile will be receiving
                        its own orders starting from the next turn)

                    </li>

                    <!-- END -->
                    <li><var> [P | PRINT] message </var> - print a message - it can also be a multiline one, with lines
                        split with an escaped newline symbol ("\\n")
                    </li>
                    <li><var> [W | WAIT] </var> - do nothing</li>
                </ul>
            </div>
            <!-- BEGIN level2 level3 -->
            <!-- BEGIN level2 -->
            <div class="statement-new-league-rule">
                <!-- END -->

                For a missile:
                <ul>
                    <li><var> [A | ACCELERATE] ax ay </var> - put acceleration
                        <const>ax</const>
                        on x-axis,
                        <const>ay</const>
                        on y-axis (with instant effect)
                    </li>
                    <li><var> [D | DETONATE] </var> - detonate</li>
                    <li><var> [W | WAIT] </var> - do nothing</li>
                </ul>
                <!-- BEGIN level2 -->
            </div>
            <!-- END -->
            <!-- END -->

            <!-- END -->

            <div>
                <br>
                <br>
                Example outputs:<br><br>

                <!-- BEGIN level1 -->
                You want to put acceleration (0, -4) on your ship,
                which has id 23, and at the same time fire a bullet with velocity (relative to the ship) (2, 1).<br>
                <var>23 | A 0 -4 | F 2 1</var>
                <br><br>
                You want to not do anything in this round.<br>
                <var>23 | W</var>
                <br><br>
                You want to only fire a bullet with velocity (relative to the ship) (1, 1) and print "Hello there" and a
                debug information
                (note that 'S' is used instead of unit_id).<br>
                <var>S | F 1 1 | P Hello there</var>
                <br><br>
                <!-- END -->
                <!-- BEGIN level2 level3 -->
                You only own a ship. You want to put acceleration (0, -4) on your ship,
                which has id 23, and at the same time fire a bullet with velocity (relavite to the ship) (2, 1) and a
                new missile with acceleration (1, 1).<br>
                <var>23 | A 0 -4 | F 2 1 | M 1 1</var>
                <br><br>
                You own a ship (id 17) and two missiles (ids 9 and 11). You want the ship to wait and both missiles to
                detonate.<br>
                <var>S | W</var><br>
                <var>9 | D</var><br>
                <var>11 | D</var><br>
                <br>
                You own a ship (id 7) and a missile (id 18). You want to put acceleration (1, 1) on the ship, (-1, 2) on
                the missile, and print "General Kenobi" as a debug information.<br>
                <var>7 | A 1 1 | P General Kenobi</var><br>
                <var>18 | A -1 2</var>


            </div>

        </div>


        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Constraints</div>
            <div class="text">Response time for first turn ≤ 1000ms <br>
                Response time for one turn ≤ 100ms
            </div>
        </div>
        <!-- LEAGUE ALERT -->
        <!-- BEGIN level1 -->

        <div style="color: #7cc576;
                              background-color: rgba(124, 197, 118,.1);
                              padding: 20px;
                              margin-top: 10px;
                              text-align: left;">
            <div style="text-align: center; margin-bottom: 6px"><img
                    src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png"/></div>

            <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">What is in the higher league?
            </p>
            Missiles! They have their own engines and can be moved around just as your ship. A deadly weapon.
        </div>
        <!-- END -->

    </div>

</div>