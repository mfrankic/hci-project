from src.mouse_server import UdpServer

def main():
    server = UdpServer()
    server.start()

if __name__ == '__main__':
    main()
