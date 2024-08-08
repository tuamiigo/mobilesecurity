import time
import frida

device = frida.get_usb_device()
pid = device.spawn(["be.howest.ti.mobilesecurity"])
device.resume(pid)
time.sleep(1)

session = device.attach(pid)

with open("file.js") as f:
	script = session.create_script(f.read())
script.load()

input()