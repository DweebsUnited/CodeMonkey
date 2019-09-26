add_executable( Eyes Eyes.cpp )

target_include_directories( Eyes ${CMAKE_SOURCE_DIR}/Eyes/include )

target_include_directories( Eyes ${CMAKE_SOURCE_DIR}/../resources/glfw3.3/include )

if( WIN32 )
	target_link_directories( Eyes ${CMAKE_SOURCE_DIR}/../resources/glfw3.3/lib-vc2019 )
endif( )
# TODO: UNIX

if( WIN32 )
	# GLFW DLL
	target_link_libraries( Eyes glfw3dll )
	target_compile_definitions( Eyes GLFW_DLL )

	target_link_libraries( Eyes opengl32 )
endif( )

install( TARGETS Eyes RUNTIME DESTINATION bin )