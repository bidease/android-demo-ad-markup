package com.bidease.android.demo.admarkup

fun getTestBannerMarkup(): String = """<div style="width:320px;height:50px;background:linear-gradient(90deg, #667eea, #764ba2);color:white;text-align:center;line-height:50px;font-family:Arial,sans-serif;border-radius:5px;">TEST BANNER</div>""".trimIndent()

fun getTestInterstitialMarkup(): String = """<div style="width:320px;height:480px;background:linear-gradient(45deg, #ff6b6b, #4ecdc4);color:white;text-align:center;display:flex;flex-direction:column;justify-content:center;align-items:center;font-family:Arial,sans-serif;">
        <h2>TEST INTERSTITIAL</h2>
        <p>Full screen ad</p>
        <button style="background:white;color:#333;border:none;padding:10px 20px;border-radius:5px;cursor:pointer;">CLICK ME</button>
    </div>""".trimIndent()

fun getTestRewardedMarkup(): String = """<div style="width:320px;height:480px;background:linear-gradient(135deg, #667eea 0%, #764ba2 100%);color:white;text-align:center;display:flex;flex-direction:column;justify-content:center;align-items:center;font-family:Arial,sans-serif;">
        <h2>REWARDED AD</h2>
        <p>Watch to earn reward</p>
        <button style="background:white;color:#333;border:none;padding:10px 20px;border-radius:5px;cursor:pointer;margin-top:20px;">CLICK FOR REWARD</button>
    </div>""".trimIndent()

fun getTestMraidMarkup(): String = """<html>
<head>
    <script src="mraid.js"></script>
</head>
<body style="margin:0;padding:0;">
    <div style="width:320px;height:50px;background:linear-gradient(90deg, #f093fb 0%, #f5576c 100%);color:white;text-align:center;line-height:50px;font-family:Arial,sans-serif;border-radius:5px;cursor:pointer;" onclick="mraid.open('https://example.com')">
        MRAID BANNER - Click to expand
    </div>
</body>
</html>""".trimIndent()