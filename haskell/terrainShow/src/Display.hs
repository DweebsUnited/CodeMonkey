module Display ( display ) where

import Graphics.Rendering.OpenGL as GL
import Graphics.UI.GLFW as GLFW

import Cube

display :: GLFW.Window -> IO ( )
display win = do
  GL.clearColor $= Color4 0 0 0 1
  GL.clear [ColorBuffer]
  -- cube 0.2
  flush