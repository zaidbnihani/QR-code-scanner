import urllib.request
import re

url = "https://play.google.com/store/apps/details?id=scan.qr.code.barcode.scanner"
req = urllib.request.Request(
    url, 
    headers={
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36',
        'Accept-Language': 'en-US,en;q=0.9',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
    }
)
try:
    html = urllib.request.urlopen(req).read().decode('utf-8')
    match = re.search(r'<meta property="og:image" content="([^"]+)"', html)
    if match:
        img_url = match.group(1)
        print("IMG_URL:", img_url)
        img_url = img_url.replace('=w240-h480-rw', '=s512')
        urllib.request.urlretrieve(img_url, "/app/src/main/res/drawable/custom_app_icon.png")
        print("Successfully downloaded!")
    else:
        print("og:image not found.")
except Exception as e:
    print("Error:", e)
