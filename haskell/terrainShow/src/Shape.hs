module Shape ( ShapeData(..), Shape(..), makeShape, cubeData ) where

import Graphics.Rendering.OpenGL as GL
import Foreign.Marshal.Array
import Foreign.Ptr
import Foreign.Storable

data ShapeData = ShapeData {
    vertices :: [Vertex3 GLfloat],
    indices  :: Maybe [GLint],
    colors   :: Maybe ( Either [Vertex3 GLfloat] [Vertex2 GLfloat] ),
    normals  :: Maybe [Vertex3 GLfloat]
}

data Shape = Shape {
    vao :: VertexArrayObject,
    numVerts :: GLint
}

bufferOffset :: Integral a => a -> Ptr b
bufferOffset = plusPtr nullPtr . fromIntegral

makeShape :: ShapeData -> IO Shape
makeShape shapeData = do
    -- Make a new VAO
    vao <- genObjectName
    bindVertexArrayObject $= Just vao

    -- We'll need these later
    let numVerts = length $ vertices shapeData
        numTris = fromIntegral $ div numVerts 3

    -- Buffer vertex information
    vertBuffer <- genObjectName
    bindBuffer ArrayBuffer $= Just vertBuffer
    withArray ( vertices shapeData ) $ \arrptr -> do
        let size = fromIntegral ( numVerts * sizeOf ( head $ vertices shapeData ) )
        bufferData ArrayBuffer $= ( size, arrptr, StaticDraw )

    -- Link buffers to VAO
    let vLoc = AttribLocation 0
    vertexAttribArray vLoc $= Enabled
    vertexAttribPointer vLoc $= ( ToFloat, VertexArrayDescriptor numTris Float 0 ( bufferOffset 0 ) )

    bindVertexArrayObject $= Nothing

    return $ Shape { vao = vao, numVerts = fromIntegral numVerts }

cubeData :: Float -> ShapeData
cubeData w = ShapeData {
    vertices = [
        Vertex3 (-w) (-w) (-w),
        Vertex3 (-w) (-w) w,
        Vertex3 (-w) w    w,

        Vertex3 w    w    (-w),
        Vertex3 (-w) (-w) (-w),
        Vertex3 (-w) w    (-w),

        Vertex3 w    (-w) w,
        Vertex3 (-w) (-w) (-w),
        Vertex3 w    (-w) (-w),

        Vertex3 w    w    (-w),
        Vertex3 w    (-w) (-w),
        Vertex3 (-w) (-w) (-w),

        Vertex3 (-w) (-w) (-w),
        Vertex3 (-w) w    w,
        Vertex3 (-w) w    (-w),

        Vertex3 w    (-w) w,
        Vertex3 (-w) (-w) w,
        Vertex3 (-w) (-w) (-w),

        Vertex3 (-w) w    w,
        Vertex3 (-w) (-w) w,
        Vertex3 w    (-w) w,

        Vertex3 w    w    w,
        Vertex3 w    (-w) (-w),
        Vertex3 w    w    (-w),

        Vertex3 w    (-w) (-w),
        Vertex3 w    w    w,
        Vertex3 w    (-w) w,

        Vertex3 w    w    w,
        Vertex3 w    w    (-w),
        Vertex3 (-w) w    (-w),

        Vertex3 w    w    w,
        Vertex3 (-w) w    (-w),
        Vertex3 (-w) w    w,

        Vertex3 w    w    w,
        Vertex3 (-w) w    w,
        Vertex3 w    (-w) w
    ],
    indices = Just [
        0,  1,  2,
        3,  4,  5,
        6,  7,  8,
        9,  10, 11,
        12, 13, 14,
        15, 16, 17,
        18, 19, 20,
        21, 22, 23,
        24, 25, 26,
        27, 28, 29,
        30, 31, 32,
        33, 34, 35
    ],
    normals = Just [
        Vertex3 (-1) 0 0,
        Vertex3 (-1) 0 0,
        Vertex3 (-1) 0 0,

        Vertex3 0 0 (-1),
        Vertex3 0 0 (-1),
        Vertex3 0 0 (-1),

        Vertex3 0 (-1) 0,
        Vertex3 0 (-1) 0,
        Vertex3 0 (-1) 0,

        Vertex3 0 0 (-1),
        Vertex3 0 0 (-1),
        Vertex3 0 0 (-1),

        Vertex3 (-1) 0 0,
        Vertex3 (-1) 0 0,
        Vertex3 (-1) 0 0,

        Vertex3 0 (-1) 0,
        Vertex3 0 (-1) 0,
        Vertex3 0 (-1) 0,

        Vertex3 0 0 1,
        Vertex3 0 0 1,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 1 0 0,
        Vertex3 1 0 0,

        Vertex3 1 0 0,
        Vertex3 1 0 0,
        Vertex3 1 0 0,

        Vertex3 0 1 0,
        Vertex3 0 1 0,
        Vertex3 0 1 0,

        Vertex3 0 1 0,
        Vertex3 0 1 0,
        Vertex3 0 1 0,

        Vertex3 0 0 1,
        Vertex3 0 0 1,
        Vertex3 0 0 1
    ],
    colors = Just $ Left [
        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1,

        Vertex3 1 0 0,
        Vertex3 0 1 0,
        Vertex3 0 0 1
    ] }
