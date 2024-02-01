# Phone Touchpad and Mouse

## Release is available [here](https://github.com/mfrankic/hci-project/releases)

- see OS prerequisites for running the app

## Run and build instructions

### Server

#### Server Prerequisites

- Python 3.10
- pip
- Linux with X11 Window System (tested on Ubuntu 22.04 with GNOME 42.9)
  - does not work on Linux with Wayland
  - does not work on Windows

cd into `server` directory

#### Create virtual environment

`python -m venv venv`

#### Activate virtual environment

`source venv/bin/activate`

#### Install dependencies

`pip install -r requirements.txt`

#### Run server

Run with `python -m src`

### Client

#### Client Prerequisites

- Android Studio
- Android 8.1 (API level 27) or higher (tested on Android 10 - API level 29)

`client` directory is an Android Studio project

#### Build and run client

Run with Android Studio
