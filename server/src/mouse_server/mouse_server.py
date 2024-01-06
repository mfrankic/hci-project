import socket
import pyautogui

class MouseServer:
    TYPE_ACCELEROMETER = 1
    TYPE_GYROSCOPE = 4
    
    def __init__(self):
        self._server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._server.bind(('0.0.0.0', 5382))
        self._mouse = pyautogui

    def start(self):
        self._server.listen()
        client, address = self._server.accept()
        print('Server started...')

        while True:
            try:
                data = client.recv(1024).decode('utf-8').strip().split()[-1]
                if not data:
                    break
                print(f'Received data: {data}')

                sensor_type, values = data.split(':')
                x, y, z = map(float, values.split(','))

                if sensor_type == str(self.TYPE_ACCELEROMETER):
                    self.move_mouse(x, y)
                elif sensor_type == str(self.TYPE_GYROSCOPE):
                    self.handle_clicks(x, y, z)
            except Exception as e:
                print(f"Error: {e}")
                break

        client.close()
        self._server.close()

    def move_mouse(self, x, y):
        # Convert accelerometer data to mouse movement
        # Adjust these scale factors as necessary
        dx, dy = x * 10, -y * 10
        pyautogui.moveRel(dx, dy)

    def handle_clicks(self, x, y, z):
        # Define thresholds for detecting tilts
        tilt_threshold = 1.0  # Adjust as necessary
        if x > tilt_threshold:
            pyautogui.click(button='right')
        elif x < -tilt_threshold:
            pyautogui.click(button='left')
        elif z > tilt_threshold:
            pyautogui.click(button='middle')
