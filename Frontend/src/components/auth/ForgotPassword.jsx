import React, { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../ui/card';
import { Alert, AlertDescription } from '../ui/alert';
import { ArrowLeft, Mail, Shield } from 'lucide-react';

import { authService } from '../../services/authService';


export function ForgotPassword({ onNavigate }) {
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [step, setStep] = useState('email');
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const navigate = useNavigate();

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage('');
    try {
      const res = await authService.forgotPassword(email.trim()); 
      setMessage(res.message || 'Nếu email tồn tại, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu.');
      setStep('token');
    } catch (err) {
      setMessage(err?.message || 'Gửi yêu cầu thất bại');
    } finally {
      setIsLoading(false);
    }
  };

  const handleTokenSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage('');
    try {
      const { valid } = await authService.verifyResetToken(token.trim());
      if (!valid) {
        setMessage('Token không hợp lệ hoặc đã hết hạn');
        return;
      }
      setMessage('Token hợp lệ. Vui lòng nhập mật khẩu mới.');
      setStep('reset');
    } catch (err) {
      setMessage(err?.message || 'Xác thực token thất bại');
    } finally {
      setIsLoading(false);
    }
  };

  const handleResetSubmit = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      setMessage('Mật khẩu xác nhận không khớp');
      return;
    }
    setIsLoading(true);
    try {
      await authService.resetPassword(token, newPassword);
      setMessage('Mật khẩu đã được đặt lại thành công!');
      setTimeout(() => navigate('/signin'), 2000);
    } catch (err) {
      setMessage(err?.message || 'Đặt lại mật khẩu thất bại');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-muted/30 px-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1">
          <div className="flex items-center space-x-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => navigate('/signin')}
              className="p-0 h-auto"
            >
              <ArrowLeft className="h-4 w-4" />
            </Button>
            <CardTitle className="text-2xl">Reset Password</CardTitle>
          </div>
          <CardDescription>
            {step === 'email' 
              ? 'Enter your email address to receive a verification token'
              : 'Enter the verification token sent to your email'
            }
          </CardDescription>
        </CardHeader>

        {(() => {
          if (step === 'email') {
            return (
              <form onSubmit={handleEmailSubmit}>
                <CardContent className="space-y-4">
                  <div className="flex items-center space-x-2 p-4 bg-accent/50 rounded-lg">
                    <Mail className="h-5 w-5 text-primary" />
                    <div className="text-sm">
                      We'll send a verification token to your email address.
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="email">Email Address</Label>
                    <Input
                      id="email"
                      type="email"
                      placeholder="Enter your email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                    />
                  </div>
                </CardContent>

                <CardFooter>
                  <Button
                    type="submit"
                    className="w-full"
                    disabled={isLoading}
                  >
                    {isLoading ? 'Sending...' : 'Send Verification Token'}
                  </Button>
                </CardFooter>
              </form>
            );
          } else if (step === 'token') {
            return (
              <form onSubmit={handleTokenSubmit}>
                <CardContent className="space-y-4">
                  {message && (
                    <Alert>
                      <Mail className="h-4 w-4" />
                      <AlertDescription>{message}</AlertDescription>
                    </Alert>
                  )}

                  <div className="flex items-center space-x-2 p-4 bg-accent/50 rounded-lg">
                    <Shield className="h-5 w-5 text-primary" />
                    <div className="text-sm">
                      Check your email for the verification token.
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="token">Verification Token</Label>
                    <Input
                      id="token"
                      type="text"
                      placeholder="Enter the verification token"
                      value={token}
                      onChange={(e) => setToken(e.target.value)}
                      required
                    />
                  </div>
                </CardContent>

                <CardFooter className="flex flex-col space-y-2">
                  <Button
                    type="submit"
                    className="w-full"
                    disabled={isLoading}
                  >
                    {isLoading ? 'Verifying...' : 'Verify Token'}
                  </Button>

                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={() => setStep('email')}
                    className="w-full"
                  >
                    Resend Token
                  </Button>
                </CardFooter>
              </form>
            );
          } else if (step === 'reset') {
            return (
              <form onSubmit={handleResetSubmit}>
                <CardContent className="space-y-4">
                  {message && (
                    <Alert>
                      <Shield className="h-4 w-4" />
                      <AlertDescription>{message}</AlertDescription>
                    </Alert>
                  )}

                  <div className="space-y-2">
                    <Label htmlFor="newPassword">Mật Khẩu Mới</Label>
                    <Input
                      id="newPassword"
                      type="password"
                      placeholder="Nhập mật khẩu mới"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="confirmPassword">Xác Nhận Mật Khẩu</Label>
                    <Input
                      id="confirmPassword"
                      type="password"
                      placeholder="Nhập lại mật khẩu mới"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      required
                    />
                  </div>
                </CardContent>

                <CardFooter>
                  <Button
                    type="submit"
                    className="w-full"
                    disabled={isLoading}
                  >
                    {isLoading ? 'Đang đặt lại...' : 'Đặt Lại Mật Khẩu'}
                  </Button>
                </CardFooter>
              </form>
            );
          }
          return null;
        })()}
      </Card>
    </div>
  );
}