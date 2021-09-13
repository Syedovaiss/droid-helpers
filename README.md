# droid-helpers

## Put this in your build.gradle (project level)

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

## put this in your build.gradle(app level)

dependencies {
	        implementation 'com.github.Syedovaiss:droid-helpers:master-SNAPSHOT'
	}
