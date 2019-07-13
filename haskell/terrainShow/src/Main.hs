import Graphics.Rendering.OpenGL as GL
import Graphics.UI.GLFW as GLFW

import Bindings
import Display
import Shape

import Control.Monad
import System.Exit
import Data.Maybe

main :: IO ()
main = do
    -- Init GLFW
    GLFW.init

    -- Window hints
    GLFW.defaultWindowHints
    GLFW.windowHint ( WindowHint'Resizable False )
    --GLFW.windowHint ( WindowHint'ContextVersionMajor 2 )
    --GLFW.windowHint ( WindowHint'ContextVersionMinor 1 )
    --GLFW.windowHint ( WindowHint'OpenGLProfile OpenGLProfile'Core )

    -- Create window, set context
    mwin <- GLFW.createWindow 640 480 "GLFW Demo" Nothing Nothing
    unless ( isJust mwin ) ( do
        putStrLn "Couldn't get a window!"
        exitFailure
        )

    let Just win = mwin

    GLFW.makeContextCurrent mwin
    GLFW.setWindowTitle win "Terrain Shower"

    glVStr <- GL.glVersion
    putStrLn $ "OpenGL Version: " ++ glVStr

    -- Set callbacks
    GLFW.setWindowSizeCallback win ( Just reshape )
    GLFW.setKeyCallback win ( Just keyPressed )
    GLFW.setWindowCloseCallback win ( Just winClosed )

    -- Set up display objects
    state <- setupDisplay

    -- Render loop!
    renderLoop win state

    -- Cleanup window in case loop ends without shutdown (crash mostly)
    GLFW.destroyWindow win
    GLFW.terminate

renderLoop :: Window -> RenderState -> IO ()
renderLoop win state = do

    -- Run the display function
    display win state

    -- Swap buffers
    GLFW.swapBuffers win
    -- Poll events
    GLFW.pollEvents

    -- Check if we should close
    winByeBye <- GLFW.windowShouldClose win

    -- Close if triggered
    unless winByeBye $ renderLoop win state
