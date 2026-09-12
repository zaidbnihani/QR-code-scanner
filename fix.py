with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if "Modal Bottom Sheet for Scan History" in line:
        skip = True
        # add the closing braces for MainScannerScreen
        new_lines.append("            }\n")
        new_lines.append("        }\n")
        new_lines.append("    }\n")
        new_lines.append("}\n\n")
    if "@Composable" in line and "fun CameraPermissionScreen" in line:
        skip = False
    
    if not skip:
        new_lines.append(line)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.writelines(new_lines)
