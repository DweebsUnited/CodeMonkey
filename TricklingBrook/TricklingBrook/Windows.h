#pragma once
#include <cmath>
#include <string>

#define PI 3.141592653589793238460

namespace windows {

	class Window {
	public:
		virtual std::string name( ) = 0;
		virtual double func( double t, double T ) = 0;

	};

	class rect : public Window {
	public:

		std::string name( ) {

			return "Rectangle";

		}

		double func( double t, double T ) {

			return 1.0;

		}

	};

	class bartlett : public Window {
	public:

		std::string name( ) {

			return "Bartlett";

		}

		double func( double t, double T ) {

			return 1.0 - std::abs( ( t - T / 2 ) / ( T / 2 ) );

		}

	};

	class hanning : public Window {
	public:

		std::string name( ) {

			return "Hanning";

		}

		double func( double t, double T ) {

			double a = 0.5;

			return a - ( 1.0 - a ) * std::cos( 2.0 * PI * t / T );

		}

	};

	class hamming : public Window {
	public:

		std::string name( ) {

			return "Hamming";

		}

		double func( double t, double T ) {

			double a = 25.0 / 46.0;

			return a - ( 1.0 - a ) * std::cos( 2.0 * PI * t / T );

		}

	};

	class blackman : public Window {
	public:

		std::string name( ) {

			return "Blackman";

		}

		double func( double t, double T ) {

			// "Not very serious"
			double a0 = 0.42;
			double a1 = 0.5;
			double a2 = 0.08;

			return a0 - a1 * std::cos( 2 * PI * t / T ) + a2 * std::cos( 4 * PI * t / T );

		}

	};

	class eblackman : public Window {
	public:

		std::string name( ) {

			return "ExactBlackman";

		}

		double func( double t, double T ) {

			// Exact
			double a0 = 7938.0 / 18608.0;
			double a1 = 9240.0 / 18608.0;
			double a2 = 1430.0 / 18608.0;

			return a0 - a1 * std::cos( 2 * PI * t / T ) + a2 * std::cos( 4 * PI * t / T );

		}

	};

}

#undef PI