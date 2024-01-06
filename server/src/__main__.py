from src.mouse_server import MouseServer

def main():
    server = MouseServer()
    print(f'Starting server... {server}')
    server.start()

if __name__ == '__main__':
    main()
