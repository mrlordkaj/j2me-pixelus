/*
 * Copyright (C) 2012 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class Story extends StoryPage {
    public static final String[] CHARACTER_NAMES = new String[] {
        "Cylop",
        "Flora",
        "Cupid",
        "Neptune",
        "Venus",
        "Bacchus",
        "Vulcan",
        "Diana",
        "Proserpina",
        "Jupiter",
        "Player"
    };
    public static final byte STORY_CYLOP_CYLOP = 00;
    public static final byte STORY_CYLOP_FLORA = 01;
    public static final byte STORY_FLORA_FLORA = 11;
    public static final byte STORY_FLORA_CUPID = 12;
    public static final byte STORY_FLORA_NEPTUNE = 13;
    public static final byte STORY_FLORA_VENUS = 14;
    public static final byte STORY_FLORA_BACCHUS = 15;
    public static final byte STORY_FLORA_VULCAN = 16;
    public static final byte STORY_FLORA_DIANA = 17;
    public static final byte STORY_FLORA_PROSERPINA = 18;
    public static final byte STORY_FLORA_JUPITER = 19;
    public static final byte STORY_CUPID_CUPID = 22;
    public static final byte STORY_CUPID_NEPTUNE = 23;
    public static final byte STORY_CUPID_VENUS = 24;
    public static final byte STORY_CUPID_BACCHUS = 25;
    public static final byte STORY_CUPID_VULCAN = 26;
    public static final byte STORY_CUPID_DIANA = 27;
    public static final byte STORY_CUPID_PROSERPINA = 28;
    public static final byte STORY_CUPID_JUPITER = 29;
    public static final byte STORY_NEPTUNE_NEPTUNE = 33;
    public static final byte STORY_NEPTUNE_VENUS = 34;
    public static final byte STORY_NEPTUNE_BACCHUS = 35;
    public static final byte STORY_NEPTUNE_VULCAN = 36;
    public static final byte STORY_NEPTUNE_DIANA = 37;
    public static final byte STORY_NEPTUNE_PROSERPINA = 38;
    public static final byte STORY_NEPTUNE_JUPITER = 39;
    public static final byte STORY_VENUS_VENUS = 44;
    public static final byte STORY_VENUS_BACCHUS = 45;
    public static final byte STORY_VENUS_VULCAN = 46;
    public static final byte STORY_VENUS_DIANA = 47;
    public static final byte STORY_VENUS_PROSERPINA = 48;
    public static final byte STORY_VENUS_JUPITER = 49;
    public static final byte STORY_BACCHUS_BACCHUS = 55;
    public static final byte STORY_BACCHUS_VULCAN = 56;
    public static final byte STORY_BACCHUS_DIANA = 57;
    public static final byte STORY_BACCHUS_PROSERPINA = 58;
    public static final byte STORY_BACCHUS_JUPITER = 59;
    public static final byte STORY_VULCAN_VULCAN = 66;
    public static final byte STORY_VULCAN_DIANA = 67;
    public static final byte STORY_VULCAN_PROSERPINA = 68;
    public static final byte STORY_VULCAN_JUPITER = 69;
    public static final byte STORY_DIANA_DIANA = 77;
    public static final byte STORY_DIANA_PROSERPINA = 78;
    public static final byte STORY_DIANA_JUPITER = 79;
    public static final byte STORY_PROSERPINA_PROSERPINA = 88;
    public static final byte STORY_PROSERPINA_JUPITER = 89;
    public static final byte STORY_JUPITER_JUPITER = 99;
    public static final byte STORY_JUPITER_CYLOP = 90;
    
    private int[] character;
    private String[][] say;
    private int currentDialog = -1;
    private Image storyImage = Image.createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    private Graphics storyGraphic;
    private final Sprite avatar;
    private int timeline = 24;
    private boolean showWaitText = false;
    private final StoryPlayer parent;
    
    public static Story getStory(int id, StoryPlayer parent) {
        Story story = new Story(parent);
        if (id < 10) {
            getCylopStory(id, parent, story);
        }
        else if (id < 20) {
            getFloraStory(id, parent, story);
        }
        else if (id < 30) {
            getCupidStory(id, parent, story);
        }
        else if (id < 40) {
            getNeptuneStory(id, parent, story);
        }
        else if (id < 50) {
            getVenusStory(id, parent, story);
        }
        else if (id < 60) {
            getBacchusStory(id, parent, story);
        }
        else if (id < 70) {
            getVulcanStory(id, parent, story);
        }
        else if (id < 80) {
            getDianaStory(id, parent, story);
        }
        else if (id < 90) {
            getProserpinaStory(id, parent, story);
        }
        else {
            getJupiterStory(id, parent, story);
        }
        return story;
    }
    
    private static void getCylopStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_CYLOP_CYLOP:
                story.character = new int[] { 0, 10, 0 };
                story.say = new String[][] {
                    {
                        "Welcome to the island of Pixelus, " + parent.getPlayerName() + "!",
                        "The gods of the island are upset because their",
                        "temple floors are broken. Can you help?"
                    },
                    {
                        "Hmm. I got shipwrecked here. Maybe if I",
                        "appease the gods, they will help me to",
                        "get back home. What can I do?"
                    },
                    {
                        "I am too old to work myself, but I know",
                        "a thing or two about making moisacs.",
                        "Come into my cave!"
                    }
                };
                break;
                
            case STORY_CYLOP_FLORA:
                story.character = new int[] { 0, 10, 0 };
                story.say = new String[][] {
                    {
                        "Well done, " + parent.getPlayerName() + ".",
                        "I have taught you everything I know!"
                    },
                    {
                        "Thank you, wise tutor.",
                        "I shall now talk to Jupiter,",
                        "and offer my skills."
                    },
                    {
                        "Wait! Do not be so rash!",
                        "Jupiter will not see unknown mortals.",
                        "Seek out Flora first."
                    }
                };
                break;
        }
    }
    
    private static void getFloraStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_FLORA_FLORA:
                story.character = new int[] { 1, 10, 1 };
                story.say = new String[][] {
                    {
                        "These floors are all perfect!",
                        "Now all I need are some windowboxes.",
                        "Thank you!"
                    },
                    {
                        "My work here is done."
                    },
                    {
                        "I'll add a gold medal to my temple",
                        "to indicate that it's finished."
                    }
                };
                break;
                
            case STORY_FLORA_CUPID:
                story.character = new int[] { 1, 10, 1 };
                story.say = new String[][] {
                    {
                        "Ah, " + parent.getPlayerName() + "! You are progressing well!",
                        "I'm recommending you to my best customer,",
                        "Cupid."
                    },
                    {
                        "Thank you, oh fragrant Flora.",
                        "I will not disappoint you."
                    },
                    {
                        "If he wants more flowers,",
                        "tell him to come and get them.",
                        "Roses half price!"
                    }
                };
                break;
                
            case STORY_FLORA_NEPTUNE:
                story.character = new int[] { 1, 10, 1 };
                story.say = new String[][] {
                    {
                        "Thank you, " + parent.getPlayerName() + "!",
                        "My floors look better than ever!",
                        "How can I repay you now?"
                    },
                    {
                        "Cupid has not helped me.",
                        "I was hoping he could",
                        "introduce me to Jupiter."
                    },
                    {
                        "You could try his brother Neptune.",
                        "Be careful, though -",
                        "he's rather tempestuous."
                    }
                };
                break;
                
            case STORY_FLORA_VENUS:
                story.character = new int[] { 1, 10, 1 };
                story.say = new String[][] {
                    {
                        "That last mosaic was beautifully made.",
                        "You are really improving!",
                        "I will help you if you need it."
                    },
                    {
                        "I still haven't met with Jupiter.",
                        "Is there anyone else I can talk to?"
                    },
                    {
                        "Of course. Venus, his daughter.",
                        "Another customer of mine.",
                        "Try the island to the east."
                    }
                };
                break;
                
            case STORY_FLORA_BACCHUS:
                story.character = new int[] { 1, 10, 1 };
                story.say = new String[][] {
                    {
                        "Oh " + parent.getPlayerName() + "!",
                        "Such pretty mosaics!",
                        "How can I possibly thank you?"
                    },
                    {
                        "It's my pleasure to make them.",
                        "I'm still looking for Jupiter."
                    },
                    {
                        "Maybe Bacchus can help you.",
                        "Zephyrus keeps having meals with him.",
                        "I think that's where he gets his wind."
                    }
                };
                break;
                
            case STORY_FLORA_VULCAN:
                story.character = new int[] { 1, 10, 1 };
                story.say = new String[][] {
                    {
                        "You are getting very good at these mosaics!",
                        "Thank you for your help!",
                        "Would you like some flowers?"
                    },
                    {
                        "Only if they will help me get to Jupiter."
                    },
                    {
                        "You could ask his neighbor Vulcan.",
                        "I don't visit that part of the island much.",
                        "It's a bit dreary."
                    }
                };
                break;
                
            case STORY_FLORA_DIANA:
                story.character = new int[] { 1, 10, 1 };
                story.say = new String[][] {
                    {
                        "Oh " + parent.getPlayerName() + "!",
                        "These are such pretty mosaics!",
                        "Where do you get your ideas?"
                    },
                    {
                        "They just come to me,",
                        "Unlike Jupiter.",
                        "I'm still looking for him."
                    },
                    {
                        "Maybe he's out hunting with Diana.",
                        "Go and look in the woods.",
                        "Ah, the woods are so pretty..."
                    }
                };
                break;
                
            case STORY_FLORA_PROSERPINA:
                story.character = new int[] { 1, 10, 1 };
                story.say = new String[][] {
                    {
                        "Oh " + parent.getPlayerName() + ".",
                        "With these floors,",
                        "you are really spoiling me!"
                    },
                    {
                        "Not at all.",
                        "I was hoping you could help me,",
                        "with an introduction to Jupiter."
                    },
                    {
                        "I wish I could help.",
                        "Perhaps I can!",
                        "Proserpina must know where he is."
                    }
                };
                break;
                
            case STORY_FLORA_JUPITER:
                story.character = new int[] { 1, 10, 9 };
                story.say = new String[][] {
                    {
                        "Thank you " + parent.getPlayerName() + ".",
                        "That is exquisite craftsmanship.",
                        "Thanks a bunch!"
                    },
                    {
                        "It was easy!",
                        "But, have you seen Jupiter?",
                        "I've been asking everywhere--"
                    },
                    {
                        "Ah, there you are, mortal.",
                        "Why waste your talents down here?",
                        "Come and apply them where they are needed!"
                    }
                };
                break;
        }
    }
    
    private static void getCupidStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_CUPID_CUPID:
                story.character = new int[] { 2, 10, 2 };
                story.say = new String[][] {
                    {
                        "Oh, I love my new temple!",
                        "It's just perfect!"
                    },
                    {
                        "It was my pleasure."
                    },
                    {
                        "I know a mermaid that",
                        "you would just love!",
                        "Let me give you her number."
                    }
                };
                break;
                
            case STORY_CUPID_NEPTUNE:
                story.character = new int[] { 2, 10, 2 };
                story.say = new String[][] {
                    {
                        "I love what you're doing with my floors.",
                        "I will do what I can to help you."
                    },
                    {
                        "I wish to gain an audience with Jupiter,",
                        "so that I can leave this island.",
                        "Can you arrange it?"
                    },
                    {
                        "Unfortunately grandfather is angry with me.",
                        "You could try talking to uncle Neptune.",
                        "Go to the watery temple to the east."
                    }
                };
                break;
                
            case STORY_CUPID_VENUS:
                story.character = new int[] { 2, 10, 2 };
                story.say = new String[][] {
                    {
                        "I love my new floors.",
                        "I'll do what I can to help you."
                    },
                    {
                        "Thank you!",
                        "Neptune has not been of assistance.",
                        "Does anyone else know Jupiter?"
                    },
                    {
                        "Of course! My Mum, Venus!",
                        "She is Jupiter's daughter.",
                        "I'll let her know you're coming."
                    }
                };
                break;
                
            case STORY_CUPID_BACCHUS:
                story.character = new int[] { 2, 10, 2 };
                story.say = new String[][] {
                    {
                        "Oh, lovely mosaics, " + parent.getPlayerName() + "!",
                        "Please let me repay you.",
                        "Is there anyone you fancy?"
                    },
                    {
                        "I want to talk to Jupiter."
                    },
                    {
                        "I had no idea - oh, I see.",
                        "After the Juno incident he won't see me.",
                        "Talk to Bacchus. He owes me a favor."
                    }
                };
                break;
                
            case STORY_CUPID_VULCAN:
                story.character = new int[] { 2, 10, 2 };
                story.say = new String[][] {
                    {
                        "You are getting good!",
                        "Tell me who you desire, and",
                        "I'll stick 'em with an arrow."
                    },
                    {
                        "That won't be necessary.",
                        "Just help me find Jupiter, please."
                    },
                    {
                        "Hmm. Vulcan lives up the hill.",
                        "Follow the trail of lava."
                    }
                };
                break;
                
            case STORY_CUPID_DIANA:
                story.character = new int[] { 2, 10, 2 };
                story.say = new String[][] {
                    {
                        "Thank you, thank you, " + parent.getPlayerName() + ".",
                        "These are lovely mosaics."
                    },
                    {
                        "I'm glad you like them.",
                        "Now, have you seen Jupiter?"
                    },
                    {
                        "It breaks my heart to say no.",
                        "Perhaps Diana can help you?"
                    }
                };
                break;
                
            case STORY_CUPID_PROSERPINA:
                story.character = new int[] { 2, 10, 2 };
                story.say = new String[][] {
                    {
                        parent.getPlayerName() + ". I love you!",
                        "And I love your mosaics!",
                        "Please let me aid you!"
                    },
                    {
                        "I've been looking for Jupiter for ages.",
                        "But I'm not having much luck."
                    },
                    {
                        "Proserpina was here earlier.",
                        "She wanted me to stop shooting people with arrows.",
                        "You could ask her about Jupiter?"
                    }
                };
                break;
                
            case STORY_CUPID_JUPITER:
                story.character = new int[] { 2, 10, 9 };
                story.say = new String[][] {
                    {
                        "Your work gets ever more lovely.",
                        "Are you sure you don't want some",
                        "of this dust of yearning?"
                    },
                    {
                        "No thanks.",
                        "I just want to talk to Jupiter!",
                        "But I don't--"
                    },
                    {
                        "Ah, mortal.",
                        "I didn't expect to find you in this temple!",
                        "Come and work on some real floors!"
                    }
                };
                break;
        }
        
    }

    private static void getNeptuneStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_NEPTUNE_NEPTUNE:
                story.character = new int[] { 3, 10, 3 };
                story.say = new String[][] {
                    {
                        "Shiver me mizzenmasts!",
                        "Ye've perfected me temple!",
                        "Arrr!"
                    },
                    {
                        "It was no problem."
                    },
                    {
                        "^^"
                    }
                };
                break;
                
            case STORY_NEPTUNE_VENUS:
                story.character = new int[] { 3, 10, 3 };
                story.say = new String[][] {
                    {
                        "Arrr!",
                        "Thank thee " + parent.getPlayerName() + " for these mosaics!",
                        "Would that I could reward you amply."
                    },
                    {
                        "You would! I mean you can!",
                        "I want to talk to Jupiter."
                    },
                    {
                        "Bah! He keeps kith and kin",
                        "at trident's length - exceptin'",
                        "his dear daughter Venus."
                    }
                };
                break;
                
            case STORY_NEPTUNE_BACCHUS:
                story.character = new int[] { 3, 10, 3 };
                story.say = new String[][] {
                    {
                        "Ha-harrr!",
                        "For a puny mortal, you impress me!",
                        "I'll warrant ye'll be wantin' bounty!"
                    },
                    {
                        "Ummm, not exactly.",
                        "I need to find Jupiter."
                    },
                    {
                        "Venus no good to ye?",
                        "Ask that scurvy dog Bacchus.",
                        "And tell him to eat some fresh fruit!"
                    }
                };
                break;
                
            case STORY_NEPTUNE_VULCAN:
                story.character = new int[] { 3, 10, 3 };
                story.say = new String[][] {
                    {
                        "Ha harrr harrr!",
                        "Ye're bargin' through like a hammerhead!",
                        "Swab me mussels if ye're a barnacle!"
                    },
                    {
                        "Errr, thanks, I think.",
                        "I'm trying---"
                    },
                    {
                        "Aye, aye, Jupiter, I heard.",
                        "Ye'll be wantin' to speak to Vulcan.",
                        "Mind ye. He's like a squib wi' a porpoise!"
                    }
                };
                break;
                
            case STORY_NEPTUNE_DIANA:
                story.character = new int[] { 3, 10, 3 };
                story.say = new String[][] {
                    {
                        "Arr harrr!",
                        "Ye're whippin' these mosaics up a storm!",
                        "Some gold for your sea chest?"
                    },
                    {
                        "Maybe later.",
                        "Right now I want to find Jupiter,",
                        "so I can get back home."
                    },
                    {
                        "Perhaps Diana can hunt him down for ye.",
                        "Ye can find her in the forest.",
                        "Arrr."
                    }
                };
                break;
                
            case STORY_NEPTUNE_PROSERPINA:
                story.character = new int[] { 3, 10, 3 };
                story.say = new String[][] {
                    {
                        "Ha ha harrr!",
                        "Ye've got a knack for puzzlin'!",
                        "Name thy reward!"
                    },
                    {
                        "Thank ye - I mean thank you.",
                        "I need an introduction to Jupiter.",
                        "Please help me!"
                    },
                    {
                        "Bah! That old rogue!",
                        "I'm not sure ye're ready,",
                        "but Proserpina may help ye!"
                    }
                };
                break;
                
            case STORY_NEPTUNE_JUPITER:
                story.character = new int[] { 3, 10, 9 };
                story.say = new String[][] {
                    {
                        "Arrr ha ha harrr!",
                        "I be mighty impressed, " + parent.getPlayerName() + "!"
                    },
                    {
                        "I'm glad, mighty sea-god.",
                        "Now can you--"
                    },
                    {
                        "Ah, found you, mortal.",
                        "Why waste your time with salty old doodles?",
                        "Come, and work on a real temple!"
                    }
                };
                break;
        }
    }
    
    private static void getVenusStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_VENUS_VENUS:
                story.character = new int[] { 4, 10, 4 };
                story.say = new String[][] {
                    {
                        "Beautiful, " + parent.getPlayerName() + "!",
                        "A shiny golden temple.",
                        "It's perfect!"
                    },
                    {
                        "Some of them were tricky,",
                        "but I got there in the end."
                    },
                    {
                        "..."
                    }
                };
                break;
                
            case STORY_VENUS_BACCHUS:
                story.character = new int[] { 4, 10, 4 };
                story.say = new String[][] {
                    {
                        "Oh, " + parent.getPlayerName() + "!",
                        "You're so good with your hands.",
                        "I grant you a favor!"
                    },
                    {
                        "Thank you, oh Venus!",
                        "I must speak to Jupiter.",
                        "Can you help me?"
                    },
                    {
                        "Patience!",
                        "You are not yet ready.",
                        "You should first help Bacchus."
                    }
                };
                break;
                
            case STORY_VENUS_VULCAN:
                story.character = new int[] { 4, 10, 4 };
                story.say = new String[][] {
                    {
                        "Oh, " + parent.getPlayerName() + "!",
                        "How do you do such great things...",
                        "with such little hands?"
                    },
                    {
                        "It's a talent I guess.",
                        "Can you help me?",
                        "I want to talk to Jupiter."
                    },
                    {
                        "I still think you are not ready.",
                        "Talk to Vulcan.",
                        "His temple needs some work."
                    }
                };
                break;
                
            case STORY_VENUS_DIANA:
                story.character = new int[] { 4, 10, 4 };
                story.say = new String[][] {
                    {
                        "My my. What craftsmanship!",
                        "I am in your debt, " + parent.getPlayerName() + ".",
                        "What is your bidding?"
                    },
                    {
                        "Please, introduce me to Jupiter.",
                        "I need his help."
                    },
                    {
                        "Patience, young man.",
                        "You are still not ready.",
                        "Seek out Diana, in the woods."
                    }
                };
                break;
                
            case STORY_VENUS_PROSERPINA:
                story.character = new int[] { 4, 10, 4 };
                story.say = new String[][] {
                    {
                        "Oh, thank you, " + parent.getPlayerName() + "!",
                        "You really know how to use your hands!"
                    },
                    {
                        "I'm glad you appreciate it.",
                        "My wrist hurts!",
                        "Can you introduce me to Jupiter?"
                    },
                    {
                        "You are nearly ready!",
                        "Proserpina needs your help though."
                    }
                };
                break;
                
            case STORY_VENUS_JUPITER:
                story.character = new int[] { 4, 10, 9 };
                story.say = new String[][] {
                    {
                        "Oh, wow, " + parent.getPlayerName() + "!",
                        "Your technique is so good!"
                    },
                    {
                        "It is my pleasure to serve you.",
                        "Now--"
                    },
                    {
                        "I hate to interrupt, but...",
                        "Come to my temple when you are ready."
                    }
                };
                break;
        }
    }
    
    private static void getBacchusStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_BACCHUS_BACCHUS:
                story.character = new int[] { 5, 10, 5 };
                story.say = new String[][] {
                    {
                        "My temple is perfect!",
                        "I feel a strange sensation...",
                        "like after a huge meal..."
                    },
                    {
                        "Satisfied?"
                    },
                    {
                        "No, that can't be it.",
                        "Aha! Ravenous!"
                    }
                };
                break;
                
            case STORY_BACCHUS_VULCAN:
                story.character = new int[] { 5, 10, 5 };
                story.say = new String[][] {
                    {
                        "Oh boy! These are good mosaics!",
                        "Thanks, " + parent.getPlayerName() + ".",
                        "What was it you wanted?"
                    },
                    {
                        "An introduction to Jupiter."
                    },
                    {
                        "Haven't seen him around.",
                        "You could try his neighbor Vulcan.",
                        "Tell him I need coal for me barbie."
                    }
                };
                break;
                
            case STORY_BACCHUS_DIANA:
                story.character = new int[] { 5, 10, 5 };
                story.say = new String[][] {
                    {
                        "Hey, thanks!",
                        "These mosaics keep my pile of food clean!",
                        "Would you like some lunch?"
                    },
                    {
                        "No thanks. I just ate.",
                        "Can you help me get to Jupiter?"
                    },
                    {
                        "Try asking Diana.",
                        "Don't tell her I sent you though.",
                        "She's one of those militant vegetarians."
                    }
                };
                break;
                
            case STORY_BACCHUS_PROSERPINA:
                story.character = new int[] { 5, 10, 5 };
                story.say = new String[][] {
                    {
                        "Delicious work!",
                        "Have a sausage.",
                        "Go on! There's hardly an ounce on you!"
                    },
                    {
                        "I'm still trying to find Jupiter.",
                        "Do you have any ideas?"
                    },
                    {
                        "Proserpina dropped by earlier.",
                        "She didn't eat much either... odd.",
                        "She said her temple needed work."
                    }
                };
                break;
                
            case STORY_BACCHUS_JUPITER:
                story.character = new int[] { 5, 10, 9 };
                story.say = new String[][] {
                    {
                        "Superb job!",
                        "These floors are clean enough to eat off!",
                        "Not that that stopped me before."
                    },
                    {
                        "Glad you like them.",
                        "Do you think you can introduce me to--"
                    },
                    {
                        "Aah, mortal!",
                        "Your reputation precedes you!",
                        "Come, please, and work on my temple!"
                    }
                };
                break;
        }
    }
    
    private static void getVulcanStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_VULCAN_VULCAN:
                story.character = new int[] { 6, 10, 6 };
                story.say = new String[][] {
                    {
                        "WHY IS VULCAN DISTURBED?",
                        "YOU PERFECTED VULCAN'S TEMPLE?",
                        "VULCAN IS SLIGHTLY LESS ANGRY!"
                    },
                    {
                        "Phew!"
                    },
                    {
                        "NOW GET OUT OF HERE,",
                        "BEFORE VULCAN BLOWS HIS TOP!"
                    }
                };
                break;
                
            case STORY_VULCAN_DIANA:
                story.character = new int[] { 6, 10, 6 };
                story.say = new String[][] {
                    {
                        "WHAT?",
                        "YOU DARE DISTURB VULCAN?",
                        "THIS HAD BETTER BE GOOD!"
                    },
                    {
                        "Forgive me, mighty Vulcan!",
                        "I beg your help!",
                        "I seek an audience with Jupiter."
                    },
                    {
                        "FOOL! YOU ARE WEAK!",
                        "JUPITER NOT INTERESTED IN YOU!",
                        "FATE PUTS DIANA IN YOUR PATH!"
                    }
                };
                break;
                
            case STORY_VULCAN_PROSERPINA:
                story.character = new int[] { 6, 10, 6 };
                story.say = new String[][] {
                    {
                        "WHAT IS IT NOW?",
                        "VULCAN IS A BUSY GOD!",
                        "DO NOT WASTE VULCAN'S TIME!"
                    },
                    {
                        "I ask forgiveness, mighty Vulcan!",
                        "And a favor! Jupiter--"
                    },
                    {
                        "FEEBLE MORTAL!",
                        "JUPITER WOULD CRUSH YOU!",
                        "YOUR DESTINY IS PROSERPINA!"
                    }
                };
                break;
                
            case STORY_VULCAN_JUPITER:
                story.character = new int[] { 6, 10, 9 };
                story.say = new String[][] {
                    {
                        parent.getPlayerName().toUpperCase() + "!",
                        "VULCAN IMPRESSED!",
                        "SPEAK!"
                    },
                    {
                        "I am glad, mighty Vulcan!",
                        "I respectfully--"
                    },
                    {
                        "Ah, mortal, there you are.",
                        "Get up off your knees",
                        "and come to my temple."
                    }
                };
                break;
        }
    }
    
    private static void getDianaStory(int id, StoryPlayer parent, Story story) {
        switch(id) {
            case STORY_DIANA_DIANA:
                story.character = new int[] { 7, 10, 7 };
                story.say = new String[][] {
                    {
                        "Oh yes, there's a good human!",
                        "They're perfect mosaics! Perfect!",
                        "What are they?"
                    },
                    {
                        "Perfect?"
                    },
                    {
                        "That's right! Good, " + parent.getPlayerName() + ", good!"
                    }
                };
                break;
                
            case STORY_DIANA_PROSERPINA:
                story.character = new int[] { 7, 10, 7 };
                story.say = new String[][] {
                    {
                        "Come here " + parent.getPlayerName() + "!",
                        "Good mortal! Good!"
                    },
                    {
                        "Err, thanks.",
                        "Do you know Jupiter?",
                        "I need to speak to him."
                    },
                    {
                        "Oh, no. I'm sorry.",
                        "You could ask his daughter.",
                        "Proserpina I mean."
                    }
                };
                break;
                
            case STORY_DIANA_JUPITER:
                story.character = new int[] { 7, 10, 9 };
                story.say = new String[][] {
                    {
                        "Who's a good mortal?",
                        "You're a good mortal! Yes you are!",
                        "Would you like a treat?"
                    },
                    {
                        "Umm, no thanks.",
                        "If you could just let Jupiter know",
                        "I'm available--"
                    },
                    {
                        "That won't be necessary, Diana.",
                        "Come mortal, and see my temple!"
                    }
                };
                break;
        }
    }
    
    private static void getProserpinaStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_PROSERPINA_PROSERPINA:
                story.character = new int[] { 8, 10, 8 };
                story.say = new String[][] {
                    {
                        "I suppose I could stay here now.",
                        "These floors look okay."
                    },
                    {
                        "I worked long and hard on them!",
                        "They're perfect!"
                    },
                    {
                        "Yes, they're not bad.",
                        "Almost cheerful."
                    }
                };
                break;
                
            case STORY_PROSERPINA_JUPITER:
                story.character = new int[] { 8, 10, 9 };
                story.say = new String[][] {
                    {
                        "You're doing a great job.",
                        "Are you sure you don't want to",
                        "cross over the river here?"
                    },
                    {
                        "Tempting.",
                        "But I want to talk to Jupiter.",
                        "Do you--"
                    },
                    {
                        "Ah, mortal, I thought I'd find you here.",
                        "I've been watching you.",
                        "You are ready for my temple!"
                    }
                };
                break;
        }
    }
    
    private static void getJupiterStory(int id, StoryPlayer parent, Story story) {
        switch (id) {
            case STORY_JUPITER_JUPITER:
                story.character = new int[] { 9, 10, 9 };
                story.say = new String[][] {
                    {
                        "By Jove, these floors are perfect!",
                        "You have outdone yourself!",
                        "Mi templo es su templo."
                    },
                    {
                        "Gracias!"
                    },
                    {
                        "Make yourself at home!",
                        "The freedom of the island is yours!"
                    }
                };
                break;
                
            case STORY_JUPITER_CYLOP:
                story.character = new int[] { 9, 10, 9 };
                story.say = new String[][] {
                    {
                        "I never thought you would succeed!",
                        "Claudius, you amaze me.",
                        "Your will is my command."
                    },
                    {
                        "When I started out I wanted nothing",
                        "more than to leave this island.",
                        "But it's beautiful. I think I'll stay!"
                    },
                    {
                        "Make yourself at home!",
                        "The freedom of the island is yours!"
                    }
                };
                break;
        }
    }
    
    private Story(StoryPlayer parent) {
        this.parent = parent;
        storyGraphic = storyImage.getGraphics();
        storyGraphic.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        Image backgroundImage = ImageHelper.loadImage("/images/storybackground.png");
        storyGraphic.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
        avatar = new Sprite(ImageHelper.loadImage("/images/avatar.png"), 60, 60);
    }
    
    public void update() {
        timeline++;
        if (currentDialog < 2) {
            if (timeline == 30) {
                timeline = 0;
                nextDialog();
            }
        }
        else {
            if (timeline > 10) {
                timeline = 0;
                showWaitText = !showWaitText;
            }
        }
    }
    
    public void paint(Graphics g) {
        g.drawImage(storyImage, 0, 0, Graphics.LEFT | Graphics.TOP);
        if (currentDialog == 2 && showWaitText)
            g.drawString(SplashScene.TEXT_WAITING, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 10, Graphics.HCENTER | Graphics.BASELINE);
    }
    
    public void dispose() {
        storyGraphic = null;
        storyImage = null;
        character = null;
        say = null;
    }
    
    public void pointerPressed(int x, int y) {
        if (currentDialog == 2)
            parent.closeStory();
    }
    
    private void nextDialog() {
        if (currentDialog != 2) {
            currentDialog++;
            int textLeft = 0, textTop = 0;
            //Image avatar = ImageHelper.loadImage("/images/avatar"+characterName[character[currentDialog]].toLowerCase()+".png");
            avatar.setFrame(character[currentDialog]);
            Image bubble = ImageHelper.loadImage("/images/storybubble" + currentDialog % 2 + ".png");
//#if ScreenWidth == 400
//#             switch (currentDialog) {
//#                 case 0:
//#                     avatar.setPosition(10, 10);
//#                     //storyGraphics.drawImage(avatar, 10, 10, Graphics.LEFT | Graphics.TOP);
//#                     storyGraphic.drawImage(bubble, 80, 10, Graphics.LEFT | Graphics.TOP);
//#                     textLeft = 240;
//#                     textTop = 26;
//#                     break;
//# 
//#                 case 1:
//#                     avatar.setPosition(330, 80);
//#                     //storyGraphics.drawImage(avatar, 330, 80, Graphics.LEFT | Graphics.TOP);
//#                     storyGraphic.drawImage(bubble, 10, 80, Graphics.LEFT | Graphics.TOP);
//#                     textLeft = 160;
//#                     textTop = 96;
//#                     break;
//# 
//#                 case 2:
//#                     avatar.setPosition(10, 150);
//#                     //storyGraphics.drawImage(avatar, 10, 150, Graphics.LEFT | Graphics.TOP);
//#                     storyGraphic.drawImage(bubble, 80, 150, Graphics.LEFT | Graphics.TOP);
//#                     textLeft = 240;
//#                     textTop = 166;
//#                     break;
//#             }
//#             avatar.paint(storyGraphic);
//#             for (int i = 0; i < say[currentDialog].length; i++) {
//#                 storyGraphic.drawString(say[currentDialog][i], textLeft, textTop + 16 * i, Graphics.HCENTER | Graphics.BASELINE);
//#             }
//#elif ScreenWidth == 320
            switch (currentDialog) {
                case 0:
                    avatar.setPosition(2, 10);
                    //storyGraphics.drawImage(avatar, 10, 10, Graphics.LEFT | Graphics.TOP);
                    storyGraphic.drawImage(bubble, 62, 10, Graphics.LEFT | Graphics.TOP);
                    textLeft = 190;
                    textTop = 26;
                    break;

                case 1:
                    avatar.setPosition(258, 80);
                    //storyGraphics.drawImage(avatar, 330, 80, Graphics.LEFT | Graphics.TOP);
                    storyGraphic.drawImage(bubble, 2, 80, Graphics.LEFT | Graphics.TOP);
                    textLeft = 130;
                    textTop = 96;
                    break;

                case 2:
                    avatar.setPosition(2, 150);
                    //storyGraphics.drawImage(avatar, 10, 150, Graphics.LEFT | Graphics.TOP);
                    storyGraphic.drawImage(bubble, 62, 150, Graphics.LEFT | Graphics.TOP);
                    textLeft = 190;
                    textTop = 166;
                    break;
            }
            avatar.paint(storyGraphic);
            for (int i = 0; i < say[currentDialog].length; i++) {
                storyGraphic.drawString(say[currentDialog][i], textLeft, textTop + 12 * i, Graphics.HCENTER | Graphics.BASELINE);
            }
//#endif
        }
    }
}
