import socket
from pynput.mouse import Controller, Button

ip_address = "0.0.0.0"
port = 5382

mouse_scale_x = 1.5
mouse_scale_y = 1.5

class UdpServer:
    def __init__(self):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.server.bind((ip_address, port))
        self.online = False
        self.mouse = Controller()

    def start(self):
        print(f"UDP server listening on port {port}")
        self.online = True

        while self.online:
            data, _ = self.server.recvfrom(4096)
            print(f"Received data: {data}")
            self.handle_data(data)

        self.close()

    def close(self):
        self.online = False
        self.server.close()

    def handle_data(self, data: bytes):
        decoded_data = data.decode("utf-8").split(" ")
        unpacked_data = []
        
        for part in decoded_data:
            try:
                unpacked_data.append(float(part))
            except ValueError:
                unpacked_data.append(int(part))
        
        sensor, x, y, z, lmb = unpacked_data

        print(f"Sensor: {sensor}, x: {x}, y: {y}, z: {z}, lmb: {lmb}")
        
        if lmb == 1:
            self.mouse.click(Button.left)
            return
        
        if x == 0 and y == 0: # skip if no mouse movement is needed
            return
        
        dx, dy = x * mouse_scale_x, y * mouse_scale_y
        self.mouse.move(dx, dy)
