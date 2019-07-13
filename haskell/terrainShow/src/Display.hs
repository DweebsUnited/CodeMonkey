{-# LANGUAGE RecordWildCards #-}
module Display ( RenderState(..), setupDisplay, display ) where

import Graphics.Rendering.OpenGL as GL
import Graphics.UI.GLFW as GLFW
import GLMatrix
import Graphics.GLUtil.ShaderProgram

import Shape

data RenderState = RenderState {
    shader :: ShaderProgram,
    shapes :: [Shape]
}

setupDisplay :: IO RenderState
setupDisplay = do
    -- Load shaders
    shader <- simpleShaderProgram "/Users/ozzy/Documents/CodeMonkey/haskell/terrainShow/resources/shader.vert" "/Users/ozzy/Documents/CodeMonkey/haskell/terrainShow/resources/shader.frag"
    GL.currentProgram $= Just ( program shader )

    -- Return a single cube for now
    shapes <- fmap ( :[] ) ( makeShape $ cubeData 1 )

    return $ RenderState shader shapes

display :: GLFW.Window -> RenderState -> IO ( )
display win RenderState{..} = do
    -- Clear screen
    GL.clearColor $= Color4 0 0 0 1
    GL.clear [ColorBuffer]

    -- Camera
    let camMatData = concat $ lookAtMatrixG [ 5, -5, -2.5 ] [ 0, 0, 0 ] [ 0, 0, 1 ]
    let projMatData = concat $ perspectiveMatrix 90 ( 640.0 / 480.0 ) 0.01 1000

    camMat <- GL.newMatrix GL.ColumnMajor camMatData :: IO ( GLmatrix GLfloat )
    projMat <- GL.newMatrix GL.ColumnMajor projMatData :: IO ( GLmatrix GLfloat )

    GL.matrixMode $= GL.Projection
    GL.matrix ( Just GL.Projection ) $= camMat
    GL.multMatrix projMat

    -- Draw all the shapes
    mapM_ ( \shape -> do
        bindVertexArrayObject $= Just ( vao shape )
        drawArrays GL.Triangles 0 3
        ) shapes

    bindVertexArrayObject $= Nothing

    return ()
