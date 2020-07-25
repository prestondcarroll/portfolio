#/usr/bin/env python
"""
Birds & Beans 2
a pygame clone of the game Birds & Beans 2

Ran on ubuntu 16.04 LTS with IDLE with python 3.5
Preston Carroll
2017
"""

#Import Modules
import numpy
import os, pygame
from pygame.locals import *
from random import randint, random, uniform
from images import *

if not pygame.font: print ('Warning, fonts disabled')
if not pygame.mixer: print ('Warning, sound disabled')

from os import path
working_dir = path.dirname(__file__)

screensize = (512,384)
gameDisplay = pygame.display.set_mode(screensize)
WHITE = (255,255,255)
BLACK = (0,0,0)
SPAWNBEAN = USEREVENT + 1
RESPAWNBEAN = USEREVENT + 2
FLASHBEANHIT = USEREVENT + 3
KILLBEANS = USEREVENT + 4
spawn_time = [1800,1000,857,625,600,461,428,343,300,266,240,214,200,185,165,150]
spawn_idx = 0
stage = 0

def main():

    pygame.mixer.pre_init(44100, -16, 2, 1536)
    pygame.mixer.init()
    pygame.init()
    screen = pygame.display.set_mode(screensize)
    pygame.display.set_caption("Bird & Beans")

#Prepare Game Resources
    #Values
    score = 0
    old_score = 0
    player_speed = 3
    bean_speed = 2
    angel_speed_scale = 1
    spawn_idx = 0
    collide_flag = False
    game_over = False
    game_over2 = True
    help_screen = False
    countdown = 0
    countdown2 = 1
    delta = 0
    stage = 0
    respawn_bean = pygame.event.Event(RESPAWNBEAN)
    pygame.time.set_timer(SPAWNBEAN, spawn_time[spawn_idx])
    clock = pygame.time.Clock()

    #Objects / Resources
    player = Player()
    spit_line = Spit_line()
    back = Background(path.join("images", "back1_4.png"))
    frame = Background(path.join("images", "frame2.png"))
    beans = pygame.sprite.Group()
    blocks = pygame.sprite.Group()
    angels = pygame.sprite.Group()
    blockArr = []
    #go through and create 30 blocks
    for i in range(0,30):
        x = (1+i)*16
        y = 352
        block = Block(x, y)
        blocks.add(block)
        blockArr.append(block)
    l_edge = Block(0,352)   #make the edges
    r_edge = Block(496,352)
    l_edge.visible = False
    r_edge.visible = False
    l_edge.active = False
    r_edge.active = False
    l_edge.hitbox.move_ip(0,-2)
    r_edge.hitbox.move_ip(0,-2)
    blockArr.append(l_edge)
    blockArr.append(r_edge)
    active_sprite_list = pygame.sprite.Group()
    active_sprite_list.add(player)

    #Sounds
    spit_sound = pygame.mixer.Sound(path.join("sounds", "spit.ogg"))
    bgm1_sound = pygame.mixer.Sound(path.join("sounds", "bgm1.ogg"))
    Angel_sound = pygame.mixer.Sound(path.join("sounds", "Angel.ogg"))
    Die_sound = pygame.mixer.Sound(path.join("sounds", "Die.ogg"))
    Game_Over_sound = pygame.mixer.Sound(path.join("sounds", "Game_Over.ogg"))
    Kill_Bean_sound = pygame.mixer.Sound(path.join("sounds", "Kill_Bean.ogg"))
    walk_sound = pygame.mixer.Sound(path.join("sounds", "walk3.ogg"))
    bomb_sound = pygame.mixer.Sound(path.join("sounds", "bomb.ogg"))
    bgm1_sound.play(-1)

    done = False

    #Main Loop
    while not done:

  # ***EVENTS***
        #handle single events
        for event in pygame.event.get(): # User did something
            if event.type == pygame.QUIT: # If user clicked close
                done = True # Flag that we are done so we exit this loop
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_z:
                    spit_sound.play()
                    player.stop()
                    player.spit()
                elif event.key == pygame.K_F1:
                    help_screen = True
                elif event.key == pygame.K_ESCAPE:
                    done = True
            elif event.type == SPAWNBEAN:
                testnum = randint(1,100)
                if testnum <= 13:
                    beans.add(White_Bean(randint(10,480), uniform(1,bean_speed)))
                else:
                    beans.add(Bean(randint(10,480), uniform(1,bean_speed)))
            elif event.type == RESPAWNBEAN:
                location = round(player.rect.x / 16) * 16
                if(block_test(blocks) == True):
                    Angel_sound.play()
                    spawn_loc = angel_spawn(location, blockArr)
                    angels.add(Angel(spawn_loc, blockArr[int(spawn_loc / 16)]))
            elif event.type == FLASHBEANHIT:
                if block_test(blocks) == False:
                    countdown = 0
                if(countdown > 0):
                    countdown -= 1
                if countdown == 0:
                    pygame.time.set_timer(FLASHBEANHIT, 0)
                    pygame.time.set_timer(RESPAWNBEAN, 0)
            elif event.type == KILLBEANS:
                for bean in beans:
                    beans.remove(bean)
                    bean.kill()
                    Kill_Bean_sound.play()
                    score += 50
                    break
                if len(beans) == 0:
                    pygame.time.set_timer(KILLBEANS, 0)


        #handle held down events
        keyState = pygame.key.get_pressed()
        if keyState[pygame.K_RIGHT] and player.spitt != True:
            walk_sound.play()
            player.go_right(player_speed)
            spit_line.go_right(player_speed)
        elif keyState[pygame.K_LEFT] and player.spitt != True:
            walk_sound.play()
            player.go_left(player_speed)
            spit_line.go_left(player_speed)
        else:
            player.stop()
        pygame.event.pump()


  # ***COLLISION***
        #handle bean collision with floor and player
        remove_one_flag = True
        for bean in beans:
            if bean.hitbox.y >= 295:
                for i in range(0,30):
                    if bean.hitbox.colliderect(blockArr[i].rect) and blockArr[i].visible == True and remove_one_flag == True:
                        bomb_sound.play()
                        beans.remove(bean)
                        bean.kill()
                        blockArr[i].visible = False
                        blockArr[i].active = False
                        blockArr[i].hitbox.move_ip(0,-2)
                        remove_one_flag = False
                    else:
                        remove_one_flag = True
            #test for hitting player and game over
            game_over = player_death_test(bean, player, game_over)
            if game_over == True and game_over2 == True:
                bgm1_sound.stop()
                Die_sound.play()
                Game_Over_sound.play()
                game_over2 = False
            if bean.rect.y >= 400:
                beans.remove(bean)
                bean.kill()

        #handle spit_line collision
        if collide_flag == True:
            collisions = pygame.sprite.spritecollide(spit_line, beans, False, pygame.sprite.collide_mask)
            score = update_score(len(collisions) , score)
            for sprite in collisions:
                if sprite.color == "white":
                    pygame.event.post(respawn_bean)
                elif sprite.color == "flash":
                    pygame.time.set_timer(KILLBEANS, 100)
                    pygame.time.set_timer(FLASHBEANHIT, 200)
                    pygame.time.set_timer(RESPAWNBEAN, 200)
                    countdown = 10
                Kill_Bean_sound.play()
                beans.remove(sprite)
                sprite.kill()


  # ***UPDATES***
        collide_flag = player.update()
        player_collisions(blockArr, player,spit_line, player_speed)
        spit_line.update()
        blocks.update()
        beans.update()
        for angel in angels:    #delete angels who are out of screen
            if angel.rect.y < -64:
                angels.remove(angel)
                angel.kill()
        angels.update(angel_speed_scale)

        #Tests to check whether or not to update certain values
        spawn_faster = False
        spawn_faster = update_spawn_time(old_score, score)
        if spawn_faster == True:
            spawn_idx += 1
            pygame.time.set_timer(SPAWNBEAN, spawn_time[spawn_idx])
        delta = flash_spawn_test(old_score, score ,delta, beans, bean_speed)
        bean_speed = update_speed_test(score, stage, bean_speed);
        stage = stage_test(bean_speed)
        old_score = score



        # ALL CODE TO DRAW SHOULD GO BELOW THIS COMMENT
        back.draw(screen)
        blocks.draw(screen)
        beans.draw(screen)
        frame.draw(screen)
        angels.draw(screen)
        active_sprite_list.draw(screen)
        draw_text(screen, "SCORE: " + str(score), 14, 250, 26)
        if game_over == True:
            draw_text(screen, "GAME OVER", 25 ,250 , 150)
        if help_screen == True:
            pygame.time.set_timer(SPAWNBEAN, 0)
            if countdown2 > 0:
                screen.fill((0,0,0))
                draw_text(screen, "Objective: Keep the beans from hitting your head", 18, 265, 25)
                draw_text(screen, "Press Z to spit in a diagonal line and clear the beans", 18, 265, 125)
                draw_text(screen, "Hitting multiple beans at the same time gives a higher score", 18, 265, 225)
                draw_text(screen, "Go for a high score!", 18,265, 325)
                countdown2 -= 1
            else:
                pygame.time.wait(10000)
                pygame.time.set_timer(SPAWNBEAN, spawn_time[spawn_idx])
                help_screen = False
        # ALL CODE TO DRAW SHOULD GO ABOVE THIS COMMENT

        clock.tick(40)

        # Go ahead and update the screen with what we've drawn.
        pygame.display.flip()

#test whether or not to go to the next stage of speed for the beans
def stage_test(bean_speed):
    if bean_speed == 2.4:
        return 1
    elif bean_speed == 2.7:
        return 2
    elif bean_speed == 3.0:
        return 3
    else:
        return 0

#test whether or not to update the speed for the beans based on the bean_speed
def update_speed_test(score, stage, speed):
    if score >= 20000 and stage == 2:
        speed = 3.0
    elif score >= 10000 and stage == 1:
        speed = 2.7
    elif score >= 5000 and stage == 0:
        speed = 2.4
    return speed

#test whether or not to spawn a flashing bean
#right now delta = 5000, so every 5000 it should spawn one
def flash_spawn_test(old_score, score ,delta, beans, bean_speed):
    delta += score - old_score
    if delta >= 5000:
        beans.add(Flash_Bean(randint(10,480), round(uniform(1,bean_speed))))
        delta = 0
    return delta

#test if there is a block that is missing
# return true if that is the case
# false if otherwise
def block_test(blocks):
    for block in blocks:
        if block.active == False:
            return True
    return False

#decide where to spawn an angel. Will look left first
# then right and then left even further and so on until it finds
# a spot.
#It will return a spot that is aligned with the block on the ground
def angel_spawn(location, blockArr):
    move = 0
    for i in range(0,30):
        move += 16
        index = int(location / 16) - 1
        if location - move > -1 and (0 <= (index - i) <= 29):
            index = index - i
            if blockArr[index].active == False:
                return index * 16
        index = int(location / 16) - 1
        if location + move < 512 and (0 <= (index + i) <= 29):
            index = index + i
            if blockArr[index].active == False:
                return index * 16

#decide if a player should or should not move left or right
#if they should not then reset their position to what they were before
#they collided
def player_collisions(blockArr, player,spit_line, player_speed):
    for i in range(len(blockArr)):
        if player.hitbox.colliderect(blockArr[i].hitbox):
            if player.direction == "L":
                player.rect.x += player_speed
                player.hitbox.x += player_speed
                spit_line.rect.x = spit_line.rect.x + player_speed
            else:
                player.rect.x -= player_speed
                player.hitbox.x -= player_speed
                spit_line.rect.x = spit_line.rect.x - player_speed

#test if a player should die based on the collision of the bean
# hitbox and the player
def player_death_test(bean, player, game_over):
    if player.hitbox.colliderect(bean.hitbox) or game_over == True:
        player.die()
        player.hitbox.move_ip(-200,-200)
        return True
    else:
        return False

#draw text onto the screen
def draw_text(screen, text, size, x, y):
        font_name = pygame.font.match_font('arial')
        font = pygame.font.Font(font_name, size)
        text_surface = font.render(text, True, WHITE)
        text_rect = text_surface.get_rect()
        text_rect.midtop = (x,y)
        screen.blit(text_surface, text_rect)

#update the total score based on how many the player hit in a row
def update_score(total, score):
    if total == 1:
        score += 50
    elif total == 2:
        score += 200
    elif total == 3:
        score += 500
    elif total == 4:
        score += 4000
    return score

#decides whether or not to update the spawn_time
# if it has crossed a certain threshold
# I want a switch statement here honestly
def update_spawn_time(old_score, new_score):
    if old_score < 20000 and new_score >= 20000:
        return True
    elif old_score < 16000 and new_score >= 16000:
        return True
    elif old_score < 13000 and new_score >= 13000:
        return True
    elif old_score < 10000 and new_score >= 10000:
        return True
    elif old_score < 9000 and new_score >= 9000:
        return True
    elif old_score < 8000 and new_score >= 8000:
        return True
    elif old_score < 7000 and new_score >= 7000:
        return True
    elif old_score < 6000 and new_score >= 6000:
        return True
    elif old_score < 5000 and new_score >= 5000:
        return True
    elif old_score < 4000 and new_score >= 4000:
        return True
    elif old_score < 3000 and new_score >= 3000:
        return True
    elif old_score < 2000 and new_score >= 2000:
        return True
    elif old_score < 1000 and new_score >= 1000:
        return True
    else:
        return False


#this calls the 'main' function when this script is executed
if __name__ == '__main__':
    try:
        main()
    finally:
        pygame.quit()

#
