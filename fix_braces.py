with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    text = f.read()

# I will find the part ending with 
#                 }
#         }
#     }
#             }
#         }
#     }
# }
# and replace it with 
#                 }
#             }
#         }
#     }
# }

import re

new_text = re.sub(r'                }\n        }\n    }\n\n            }\n        }\n    }\n}', r'                }\n            }\n        }\n    }\n}', text)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(new_text)
