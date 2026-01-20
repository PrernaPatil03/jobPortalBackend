package com.jobportal.utility;

public class Data {
    public static String getMessageBody(String otp, String name) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <title>Your OTP Code</title>
                  <style>
                    body {
                      font-family: Arial, sans-serif;
                      background-color: #f7f8fc;
                      margin: 0;
                      padding: 0;
                    }
                    .container {
                      max-width: 600px;
                      background-color: #ffffff;
                      margin: 40px auto;
                      padding: 30px;
                      border-radius: 10px;
                      box-shadow: 0 0 10px rgba(0,0,0,0.05);
                    }
                    .header {
                      text-align: center;
                      padding-bottom: 20px;
                    }
                    .otp-box {
                      background-color: #f1f3f6;
                      color: #2c3e50;
                      font-size: 28px;
                      font-weight: bold;
                      padding: 15px;
                      text-align: center;
                      letter-spacing: 5px;
                      border-radius: 8px;
                      margin: 20px 0;
                    }
                    .footer {
                      font-size: 14px;
                      color: #999;
                      text-align: center;
                      margin-top: 30px;
                    }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header">
                      <h2>Email Verification</h2>
                    </div>
                    <p>Hello %s,</p>
                    <p>Use the following One-Time Password (OTP) to verify your email address. The OTP is valid for 10 minutes.</p>
                    <div class="otp-box">
                      <span>%s</span>
                    </div>
                    <p>If you didn’t request this, you can safely ignore this email.</p>
                    <p>Thanks,<br><strong>FindJob</strong></p>
                    <div class="footer">
                      &copy; 2025 FindJob. All rights reserved.
                    </div>
                  </div>
                </body>
                </html>
                """, name, otp); // 👈 Pass both name and otp here
    }
    
    public static String getVerificationEmailBody(String companyName, String verifyLink) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <title>Verify Employer Account</title>
                  <style>
                    body { font-family: Arial,sans-serif; background-color: #f7f8fc; margin:0; padding:0;}
                    .container { max-width:600px; background-color:#fff; margin:40px auto; padding:30px; border-radius:10px;
                                box-shadow:0 0 10px rgba(0,0,0,0.05);}
                    .header { text-align:center; padding-bottom:20px;}
                    .button { display:inline-block; padding:10px 20px; background-color:#4CAF50; color:#fff; 
                              text-decoration:none; border-radius:5px; margin:20px 0;}
                    .footer { font-size:14px; color:#999; text-align:center; margin-top:30px;}
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header"><h2>Verify Your Employer Account</h2></div>
                    <p>Dear %s HR,</p>
                    <p>Click the button below to verify your employer account:</p>
                    <a href="%s" class="button">Verify Now</a>
                    <p>This link will expire in 2 days.</p>
                    <p>Thanks,<br><strong>FindJob Team</strong></p>
                    <div class="footer">&copy; 2025 FindJob. All rights reserved.</div>
                  </div>
                </body>
                </html>
                """, companyName, verifyLink);
    }

}
