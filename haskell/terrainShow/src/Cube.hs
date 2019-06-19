module Cube where

import Graphics.Rendering.OpenGL as GL

cube :: GLfloat -> [Vertex3 GLfloat]
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
    Vertex3 (-w) w (-w)
    ]