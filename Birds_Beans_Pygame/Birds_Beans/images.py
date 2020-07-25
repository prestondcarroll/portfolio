#player
"""
This module is used to pull individual sprites from sprite sheets.
"""
import pygame
from os import path


class SpriteSheet(object):
    """ Class used to grab images out of a sprite sheet. """

    def __init__(self, file_name):

        # Load the sprite sheet.
        self.sprite_sheet = pygame.image.load(file_name)

    def get_image(self, x, y, width, height):

        image = pygame.Surface([width, height], pygame.SRCALPHA, 32)
        image = image.convert_alpha()

        # Copy the sprite from the large sheet onto the smaller image
        image.blit(self.sprite_sheet, (0, 0), (x, y, width, height))

        return image

sprite_sheet = SpriteSheet(path.join("images", "pyoro4.png"))

class Angel(pygame.sprite.Sprite):
    def __init__(self,location,block):

        # Call the parent's constructor
        super().__init__()

        self.fall_frames = []
        self.rise_frames = []
        self.counter = 0
        self.flag = 0

        image = sprite_sheet.get_image(0, 64, 32, 48)
        self.fall_frames.append(image)
        image = sprite_sheet.get_image(32, 64, 32, 48)
        self.fall_frames.append(image)
        image = sprite_sheet.get_image(64, 64, 32, 32)
        self.rise_frames.append(image)
        image = sprite_sheet.get_image(96, 64, 32, 32)
        self.rise_frames.append(image)
        self.image = self.fall_frames[0]
        self.rect = self.image.get_rect()

        self.rect.x = self.rect.x + 7 + location
        self.rect.y -= 50
        self.block = block
        self.block.active = True

    def update(self,scale):
        if self.flag == 0:
            self.descend(scale)
        else:
            self.ascend(scale)

    def descend(self,scale):
        self.counter += 1
        frame = (self.counter // 10) % len(self.fall_frames)
        self.image = self.fall_frames[frame]
        if self.rect.y > 310:
            self.block.hitbox.y = 352
            self.block.rect.y = 352
            self.block.visible = True
            self.flag = 1
        elif self.rect.y > 275:
            self.rect.y += 5 * scale
        elif self.rect.y > 128:
            self.rect.y += 11 * scale
        elif self.rect.y > -64:
            self.rect.y += 16 * scale


    def ascend(self, scale):
        self.counter += 1
        frame = (self.counter // 10) % len(self.rise_frames)
        self.image = self.rise_frames[frame]
        if self.rect.y > 275:
            self.rect.y -= 6 * scale
        elif self.rect.y > 128:
            self.rect.y -= 11 * scale
        elif self.rect.y > -64:
            self.rect.y -= 16 * scale


class Spit_line(pygame.sprite.Sprite):
    def __init__(self):

        # Call the parent's constructor
        super().__init__()

        self.direction = "R"
        self.image_l = pygame.image.load(path.join("images", "line.png")).convert_alpha()
        self.image_r = pygame.transform.flip(self.image_l, True, False)
        self.image = self.image_r
        self.rect = self.image.get_rect()
        self.rect.x = 278
        self.rect.y = -187

        self.mask = pygame.mask.from_surface(self.image)

    def draw(self,screen):
        screen.blit(self.image, (self.rect.x,self.rect.y))

    def update(self):
        self.mask = pygame.mask.from_surface(self.image)

    def go_right(self, speed):
        self.image = self.image_r
        self.rect.x += speed
        if(self.direction == "L"):
            self.rect.x += 544
            self.direction = "R"

    def go_left(self, speed):
        self.image = self.image_l
        self.rect.x -= speed
        if(self.direction == "R"):
            self.rect.x -= 544
            self.direction = "L"

class Background(pygame.sprite.Sprite):

    # -- Methods
    def __init__(self,file_name):
        """ Constructor function """

        # Background image
        self.background = pygame.image.load(file_name).convert_alpha()

    def draw(self, screen):
        screen.blit(self.background, (0,0))


class Bean(pygame.sprite.Sprite):
    def __init__(self,location, speed):

        # Call the parent's constructor
        super().__init__()

        self.fall_frames = []
        self.speed = speed
        self.counter = 0
        self.pos = 0.0
        self.color = "Green"

        image = sprite_sheet.get_image(32, 128, 32, 32)
        self.fall_frames.append(image)
        self.fall_frames.append(image)
        image = sprite_sheet.get_image(0, 128, 32, 32)
        self.fall_frames.append(image)
        image = sprite_sheet.get_image(64, 128, 32, 32)
        self.fall_frames.append(image)
        self.fall_frames.append(image)
        image = sprite_sheet.get_image(0, 128, 32, 32)
        self.fall_frames.append(image)

        self.image = image
        self.rect = self.image.get_rect()
        self.rect.x = location
        self.rect.y -= 32

        self.hitbox = self.rect.copy()
        self.hitbox.inflate_ip(-23,0)
        self.pos -= 32


    def update(self):
        # See if we hit the player
        self.pos += self.speed
        self.rect.y = int(round(self.pos))
        self.hitbox.y = int(round(self.pos))
        self.counter += 1
        frame = (self.counter // 7) % len(self.fall_frames)
        self.image = self.fall_frames[frame]

    def draw(self,screen):
        pygame.draw.rect(screen, (255,0,0), self.rect)

class White_Bean(pygame.sprite.Sprite):
    def __init__(self,location, speed):

        # Call the parent's constructor
        super().__init__()

        self.fall_frames = []
        self.speed = speed
        self.counter = 0
        self.pos = 0.0
        self.color = "white"

        image = sprite_sheet.get_image(128, 128, 32, 32)
        self.fall_frames.append(image)
        self.fall_frames.append(image)
        image = sprite_sheet.get_image(96, 128, 32, 32)
        self.fall_frames.append(image)
        image = sprite_sheet.get_image(160, 128, 32, 32)
        self.fall_frames.append(image)
        self.fall_frames.append(image)
        image = sprite_sheet.get_image(96, 128, 32, 32)
        self.fall_frames.append(image)

        self.image = image
        self.rect = self.image.get_rect()
        self.rect.x = location
        self.rect.y -= 32

        self.hitbox = self.rect.copy()
        self.hitbox.inflate_ip(-23,0)
        self.pos -= 32


    def update(self):
        # See if we hit the player
        self.pos += self.speed
        self.rect.y = int(round(self.pos))
        self.hitbox.y = int(round(self.pos))
        self.counter += 1
        frame = (self.counter // 7) % len(self.fall_frames)
        self.image = self.fall_frames[frame]

    def draw(self,screen):
        pygame.draw.rect(screen, (255,0,0), self.rect)

class Flash_Bean(pygame.sprite.Sprite):
    def __init__(self,location, speed):

        # Call the parent's constructor
        super().__init__()

        self.fall_frames = [[0 for x in range(3)] for x in range(3)]
        self.speed = speed
        self.counter = 0
        self.pos = 0.0
        self.color = "flash"

        image = sprite_sheet.get_image(0, 128, 32, 32)
        self.fall_frames[0][0] = image
        image = sprite_sheet.get_image(32, 128, 32, 32)
        self.fall_frames[0][1] = image
        image = sprite_sheet.get_image(64, 128, 32, 32)
        self.fall_frames[0][2] = image
        image = sprite_sheet.get_image(96, 128, 32, 32)
        self.fall_frames[1][0] = image
        image = sprite_sheet.get_image(128, 128, 32, 32)
        self.fall_frames[1][1] = image
        image = sprite_sheet.get_image(160, 128, 32, 32)
        self.fall_frames[1][2] = image
        image = sprite_sheet.get_image(192, 128, 32, 32)
        self.fall_frames[2][0] = image
        image = sprite_sheet.get_image(224, 128, 32, 32)
        self.fall_frames[2][1] = image
        image = sprite_sheet.get_image(256, 128, 32, 32)
        self.fall_frames[2][2] = image

        self.image = image
        self.rect = self.image.get_rect()
        self.rect.x = location
        self.rect.y -= 32

        self.hitbox = self.rect.copy()
        self.hitbox.inflate_ip(-23,0)
        self.pos -= 32


    def update(self):
        # See if we hit the player
        self.pos += self.speed
        self.rect.y = int(round(self.pos))
        self.hitbox.y = int(round(self.pos))
        self.counter += 1
        angle = (self.counter // 13) % 3
        color = self.counter % 3

        self.image = self.fall_frames[color][angle]

    def draw(self,screen):
        pygame.draw.rect(screen, (255,0,0), self.rect)


class Block(pygame.sprite.Sprite):
    def __init__(self, x, y):
        """ Constructor function """

        # Call the parent's constructor
        super().__init__()


        self.visible = True
        self.active = True
        self.vis_frame = []

        image = sprite_sheet.get_image(64, 287, 16, 16)
        self.image = image
        self.vis_frame.append(image)

        image = sprite_sheet.get_image(543, 0, 16, 16)
        self.vis_frame.append(image)

        self.rect = self.image.get_rect()
        self.rect.x = x
        self.rect.y = y
        self.hitbox = self.rect.copy()
        self.hitbox.inflate_ip(-8,0)

    def update(self):
        #set block visible
        if self.visible == True:
            self.image = self.vis_frame[0]
        else:
            self.image = self.vis_frame[1]


class Player(pygame.sprite.Sprite):
    """ This class represents the bar at the bottom that the player
    controls. """

    # -- Methods
    def __init__(self):
        """ Constructor function """

        # Call the parent's constructor
        super().__init__()

        # -- Attributes
        # Set speed vector of player
        self.change_x = 0
        self.change_y = 0

        self.walking_frames_l = []
        self.walking_frames_r = []
        self.spitting_frames_l = []
        self.spitting_frames_r = []
        self.current_frame = 0;

        self.direction = "R"
        self.spitt = False
        self.dead = False

        # Load all the left frectacing images into a list
        image = sprite_sheet.get_image(0, 0, 58, 46)
        self.walking_frames_l.append(image)
        image = sprite_sheet.get_image(57, 0, 58, 46)
        self.walking_frames_l.append(image)

        image = sprite_sheet.get_image(348, 0, 58, 46)
        self.spitting_frames_l.append(image)
        image = sprite_sheet.get_image(290, 0, 58, 46)
        self.spitting_frames_l.append(image)
        image = sprite_sheet.get_image(232, 0, 58, 46)
        self.spitting_frames_l.append(image)
        image = sprite_sheet.get_image(174, 0, 58, 46)
        self.spitting_frames_l.append(image)

        # Load all the left facing images, then flip them
        # to face left.
        image = sprite_sheet.get_image(0, 0, 58, 46)
        image = pygame.transform.flip(image, True, False)
        self.walking_frames_r.append(image)
        image = sprite_sheet.get_image(57, 0, 58, 46)
        image = pygame.transform.flip(image, True, False)
        self.walking_frames_r.append(image)

        image = sprite_sheet.get_image(348, 0, 58, 46)
        image = pygame.transform.flip(image, True, False)
        self.spitting_frames_r.append(image)
        image = sprite_sheet.get_image(290, 0, 58, 46)
        image = pygame.transform.flip(image, True, False)
        self.spitting_frames_r.append(image)
        image = sprite_sheet.get_image(232, 0, 58, 46)
        image = pygame.transform.flip(image, True, False)
        self.spitting_frames_r.append(image)
        image = sprite_sheet.get_image(174, 0, 58, 46)
        image = pygame.transform.flip(image, True, False)
        self.spitting_frames_r.append(image)

        self.death_frame = sprite_sheet.get_image(116, 0, 58, 46)


        # Set the image the player starts with
        self.image = self.walking_frames_r[0]

        # Set a reference to the image rect.
        self.rect = self.image.get_rect()
        self.rect.x = 233
        self.rect.y = 306
        self.hitbox = self.rect.copy()
        self.hitbox.inflate_ip(-40, -16)
        self.hitbox.move_ip(0, 8)

    def update(self):
        """ Move the player. """

        if self.dead == True:
            self.image = self.death_frame
            self.rect.y += 1
            return

        # Move left/right
        self.rect.x += self.change_x
        self.hitbox.x += self.change_x
        pos = self.rect.x
        if self.direction == "R":
            frame = (pos // 5) % len(self.walking_frames_r)
            self.image = self.walking_frames_r[frame]
            self.mask = pygame.mask.from_surface(self.image)
        else:
            frame = (pos // 5) % len(self.walking_frames_r)
            self.image = self.walking_frames_l[frame]
            self.mask = pygame.mask.from_surface(self.image)

        #do animation for spitting
        if self.spitt == True:
            self.current_frame += 1
            if self.direction == "R":
                if self.current_frame >= len(self.spitting_frames_r):
                    self.spitt = False
                    self.current_frame = 0
                self.image = self.spitting_frames_r[self.current_frame - 1]
            else:
                if self.current_frame >= len(self.spitting_frames_l):
                    self.spitt = False
                    self.current_frame = 0
                self.image = self.spitting_frames_l[self.current_frame - 1]
            if self.current_frame == 3 or self.current_frame == 4:
                return True
            else:
                return False


    # Player-controlled movement:
    def go_left(self,speed):
        """ Called when the user hits the left arrow. """
        self.change_x = -speed
        self.direction = "L"

    def go_right(self,speed):
        """ Called when the user hits the right arrow. """
        self.change_x = speed
        self.direction = "R"

    def stop(self):
        """ Called when the user lets off the keyboard. """
        self.change_x = 0

    def spit(self):
        #
        self.spitt = True

    def die(self):
        self.dead = True
        self.change_x = 0

#
