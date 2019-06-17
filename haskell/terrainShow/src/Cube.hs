module Cube where

import Graphics.UI.GLUT

cube :: GLfloat -> IO ()
cube w = [
    Vertex3 w w w,
    Vertex3 w w (-w),
    Vertex3 w (-w) (-w),

    Vertex3 w (-w) w,
    Vertex3 w w w,
    Vertex3 w w (-w),

    Vertex3 (-w) w (-w),
    Vertex3 (-w) w w,
    Vertex3 w w w,

    Vertex3 w (-w) w,
    Vertex3 (-w) (-w) w,
    Vertex3 (-w) w w,

    Vertex3 (-w) w w,
    Vertex3 (-w) w (-w),
    Vertex3 (-w) (-w) (-w),

    Vertex3 (-w) (-w) w,
    Vertex3 w (-w) w,
    Vertex3 w (-w) (-w),

    Vertex3 (-w) (-w) (-w),
    Vertex3 (-w) (-w) w,
    Vertex3 w w (-w),

    Vertex3 w (-w) (-w),
    Vertex3 (-w) (-w) (-w),
    Vertex3 (-w) w (-w),
    ]