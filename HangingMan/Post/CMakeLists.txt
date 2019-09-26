add_executable( Post Post.cpp )

include_directories( ${CMAKE_SOURCE_DIR}/Post/include )

install( TARGETS Post RUNTIME DESTINATION bin )