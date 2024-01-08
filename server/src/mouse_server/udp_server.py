import socket
import pyautogui
import struct

ip_address = "0.0.0.0"
port = 5382

mouse_scale_x = 1.5
mouse_scale_y = 1.5

class UdpServer:
    def __init__(self):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.server.bind((ip_address, port))
        self.online = False

    def start(self):
        print(f"UDP server listening on port {port}")
        self.online = True

        while self.online:
            data, _ = self.server.recvfrom(5)
            self.handle_data(data)

        self.close()

    def close(self):
        self.online = False
        self.server.close()

    def handle_data(self, data: bytes):
        # ! - little endian, h - short, b - byte
        x, y, lmb = struct.unpack("!hhb", data)

        if lmb == 1:
            pyautogui.click(button='left')
            return
        
        if x == 0 and y == 0: # skip if no mouse movement is needed
            return
        
        self.move_mouse(x, y)

    def move_mouse(self, x, y):
        dx, dy = x * mouse_scale_x, y * mouse_scale_y
        pyautogui.moveRel(dx, dy)