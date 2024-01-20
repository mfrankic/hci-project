import socket
import struct
from pynput import mouse
from screeninfo import get_monitors

ip_address = "0.0.0.0"
port = 5382

mouse_scale_x = 1.5
mouse_scale_y = 1.5

class UdpServer2:
    def __init__(self):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.server.bind((ip_address, port))
        self.online = False
        self.mouse_controller = mouse.Controller()

    def start(self):
        print(f"UDP server listening on port {port}")
        self.online = True

        while self.online:
            data, _ = self.server.recvfrom(4096)
            self.handle_data(data)

        self.close()

    def close(self):
        self.online = False
        self.server.close()

    def reset_mouse_position(self):
        min_x, min_y = float('inf'), float('inf')
        max_x, max_y = 0, 0

        for m in get_monitors():
            min_x = min(min_x, m.x)
            min_y = min(min_y, m.y)
            max_x = max(max_x, m.x + m.width)
            max_y = max(max_y, m.y + m.height)

        total_width = max_x - min_x
        total_height = max_y - min_y

        center_x = min_x + total_width / 2
        center_y = min_y + total_height / 2

        self.mouse_controller.position = (center_x, center_y)

    def handle_data(self, data: bytes):
        decoded_data = None
        
        try:
            decoded_data = data.decode("utf-8").split(" ")
            self.move_mouse_sensors(decoded_data)
        except UnicodeDecodeError:
            x, y, lmb = struct.unpack("!hhb", data)
            self.move_mouse_touchpad(x, y, lmb)

    def move_mouse_sensors(self, data: bytes):
        if len(data) != 4:
            return

        x = float(data[0])
        y = float(data[1])
        lmb = True if data[2] == "1" else False
        reset_position = True if data[3] == "1" else False

        if reset_position:
            self.reset_mouse_position()
            return

        if lmb:
            print("left click")
            self.mouse_controller.click(mouse.Button.left)
            return

        dx = round(x) * mouse_scale_x
        dy = round(y) * mouse_scale_y

        if dx == 0 and dy == 0:
            return

        print(f"Move - dx: {dx}, dy: {dy}")

        self.mouse_controller.move(dx, dy)

    def move_mouse_touchpad(self, x: int, y: int, lmb: int):
        if lmb == 1:
            self.mouse_controller.click(mouse.Button.left)
            return
        
        if x == 0 and y == 0:
            return
        
        dx, dy = x * mouse_scale_x, y * mouse_scale_y
        self.mouse_controller.move(dx, dy)
