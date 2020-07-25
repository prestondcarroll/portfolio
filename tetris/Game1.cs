using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;
using System;

namespace Tetris
{
    /// <summary>
    /// This is the main type for your game.
    /// </summary>
    public class Game1 : Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;
        private SpriteFont spriteFont;
        private KeyboardState oldState;



        Texture2D block;
        Vector2 piecelocation = new Vector2(0, 0);
        Color tintColor = Color.White;

        private int blockSize = 32;

        Tetrominoe tetro = new Tetrominoe();
        Board board= new Board();


        //Vector2 tetroLocation = new Vector2(416, 64);
        //Vector2 boardLocation = new Vector2(256, 32);

        int StepTime = 800;
        // Time step between updates in ms i
        int ElapsedTime = 0;
        // Total elapsed time since the last update 
        int KeyBoardElapsedTime = 0;
        // Total elapsed time since handling the last keypress



        public Game1()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            // TODO: Add your initialization logic here

            base.Initialize();
            graphics.PreferredBackBufferWidth = 320;  // set this value to the desired width of your window
            graphics.PreferredBackBufferHeight = 704;   // set this value to the desired height of your window
            graphics.ApplyChanges();
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);
            spriteFont = Content.Load<SpriteFont>("Resources/Arial");

            // TODO: use this.Content to load your game content here
            block = Content.Load<Texture2D>("Resources/block");

            Song song = Content.Load<Song>("Resources/tetris");  // Put the name of your song here instead of "song_title"
            MediaPlayer.Play(song);
            MediaPlayer.IsRepeating = true;

        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// game-specific content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back == ButtonState.Pressed || Keyboard.GetState().IsKeyDown(Keys.Escape))
                Exit();
            // TODO: Add your update logic here


            ElapsedTime += gameTime.ElapsedGameTime.Milliseconds;
            KeyBoardElapsedTime += gameTime.ElapsedGameTime.Milliseconds;


            KeyboardState ks = Keyboard.GetState();
            if (KeyBoardElapsedTime > 200)
            {
                if (ks.IsKeyDown(Keys.Left) || ks.IsKeyDown(Keys.Right))
                {
                    board.updateSideways(tetro, ks);
                    KeyBoardElapsedTime = 0;
                }
                if (ks.IsKeyDown(Keys.Z))
                {
                    board.updateRotate(tetro, false);
                    KeyBoardElapsedTime = 0;
                }
                if (ks.IsKeyDown(Keys.X))
                {
                    board.updateRotate(tetro, true);
                    KeyBoardElapsedTime = 0;
                }
                if (ks.IsKeyDown(Keys.Down))
                {
                    ElapsedTime = StepTime + 1;
                    KeyBoardElapsedTime = 175;
                }
            }


            if (ElapsedTime > StepTime)
            {
                // Create a new location for this spawned piece to go to on the next update                 
                // Now check to see if we can place the piece at that new location 

                if (board.updateNewStep(tetro))
                {
                    this.Exit();
                }
                ElapsedTime = 0;
            }


            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.CornflowerBlue);

            // TODO: Add your drawing code here
            spriteBatch.Begin();

            //draw board
            for (int x = 0; x < board.getBoardWidth(); x++)
            {
                for (int y = 0; y < board.getBoardHeight(); y++)
                {
                    tintColor = board.TetronimoColors[board.boardArr[x, y]];
                    // Since for the board itself background colors are transparent, we'll go ahead and give this one
                    // a custom color. This can be omitted if you draw a background image underneath your board
                    if (board.boardArr[x, y] == 0)
                    {
                        tintColor = Color.FromNonPremultiplied(50, 50, 50, 50);
                        spriteBatch.Draw(block
                                        , new Rectangle(board.getX() + x * blockSize, board.getY() + y * blockSize, blockSize, blockSize)
                                        , new Rectangle(0, 0, 32, 32)
                                        , tintColor);
                    }
                    else
                    {
                        tintColor = tetro.TetronimoColors[board.valueAt(x, y)];
                        spriteBatch.Draw(block
                                        , new Rectangle(board.getX() + x * blockSize, board.getY() + y * blockSize, blockSize, blockSize)
                                        , new Rectangle(0, 0, 32, 32)
                                        , tintColor);
                    }
                }
            }



            //draw current tetro
            int dim = tetro.getDim();
            for (int y = 0; y < dim; y++)
            {
                for (int x = 0; x < dim; x++)
                {
                    if (tetro.valueAt(x, y) != 0)
                    {

                        tintColor = tetro.TetronimoColors[tetro.valueAt(x, y)];
                        spriteBatch.Draw(block
                                        , new Rectangle(board.getX() + (tetro.getX() + x) * blockSize, board.getY() + (tetro.getY() + y) * blockSize, blockSize, blockSize)
                                        , new Rectangle(0, 0, 32, 32)
                                        , tintColor);

                    }
                }

            }


            spriteBatch.End();
            base.Draw(gameTime);
        }

    }
}
