package CodeMonkey.signal;


public class CDMA {

	// TODO: DNE ( Do Not Encode ) mask, to not mux certain channels
	// Encoding: Given 2^codebits parallel bitstreams, create one bytestream
	// Map data: 0,1 -> -1,1
	// For each bit in input:
	// Transmit sum( code * bit ) over all streams

	public

	// Decoding: Given one bytestream( codebits )
	// Dot product with each code
	// Map result by element: -,0,+ -> 0,DNE,1
	// DNE means nothing was sent on this channel

}
