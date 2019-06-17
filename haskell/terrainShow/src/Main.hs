import Graphics.Rendering.OpenGL as GL
import Graphics.UI.GLFW as GLFW

import Bindings
import Display

import Control.Monad

main :: IO ()
main = do
  -- Init GLFW
  GLFW.init

  -- Window hints
  GLFW.defaultWindowHints
  GLFW.windowHint ( WindowHint'Resizable False )

  -- Create window, set context
  Just win <- GLFW.createWindow 640 480 "GLFW Demo" Nothing Nothing
  GLFW.makeContextCurrent ( Just win )

  -- Set callbacks
  GLFW.setWindowSizeCallback win ( Just reshape )
  GLFW.setKeyCallback win ( Just keyPressed )
  GLFW.setWindowCloseCallback win ( Just winClosed )

  -- Render loop!
  onDisplay win

  -- Cleanup window in case loop ends without shutdown (crash mostly)
  GLFW.destroyWindow win
  GLFW.terminate

onDisplay :: Window -> IO ()
onDisplay win = forever $ do
  -- Run the display function
  display
  -- Swap buffers
  GLFW.swapBuffers win
  -- Poll events
  GLFW.pollEvents