module Bindings ( reshape, keyPressed, winClosed ) where

import Graphics.Rendering.OpenGL as GL
import Graphics.UI.GLFW as GLFW

import System.Exit ( exitWith, ExitCode( .. ) )

-- Window resize
reshape :: GLFW.WindowSizeCallback
reshape win w h = do
  GL.viewport $= ( GL.Position 0 0, GL.Size ( fromIntegral w ) ( fromIntegral h ) )

-- Keyboard event
keyPressed :: GLFW.KeyCallback
keyPressed win _ _ _ _ = return( )

-- Window closed
winClosed :: GLFW.WindowCloseCallback
winClosed win = GLFW.setWindowShouldClose win True