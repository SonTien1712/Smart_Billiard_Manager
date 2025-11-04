import React, { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../ui/card';
import { Alert, AlertDescription } from '../ui/alert';
import { ArrowLeft, Mail, Shield } from 'lucide-react';


export function ForgotPassword({ onNavigate }) {
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [step, setStep] = useState<'email' | 'token'>('email');
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    
    // Mock API call
    setTimeout(() => {
      setStep('token');
      setMessage('A verification token has been sent to your email address.');
      setIsLoading(false);
    }, 1000);
  };

  const handleTokenSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    
    // Mock token verification
    setTimeout(() => {
      setMessage('Password reset successful! You can now sign in with your new password.');
      setIsLoading(false);
      // In real app, would redirect to signin or password reset form
      setTimeout(() => onNavigate('signin'), 2000);
    }, 1000);
  };

  return (
    <div className="dark min-h-screen relative px-4 py-8 overflow-hidden">
      <div className="absolute inset-0 opacity-20">
        <img
          src="https://images.unsplash.com/photo-1662550402015-82675fdc44aa?auto=format&fit=crop&w=1600&q=70"
          alt="Billiard background"
          className="w-full h-full object-cover"
        />
      </div>
      <Card className="w-full max-w-md mx-auto smooth-shadow relative z-10" style={{ backgroundColor: 'rgba(3,4,8,0.82)', backdropFilter: 'blur(6px)' }}>
        <CardHeader className="space-y-1">
          <div className="flex items-center space-x-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => onNavigate('signin')}
              className="p-0 h-auto"
            >
              <ArrowLeft className="h-4 w-4" />
            </Button>
            <CardTitle className="text-2xl">Đặt lại mật khẩu</CardTitle>
          </div>
          <CardDescription>
            {step === 'email' 
              ? 'Nhập email để nhận mã xác minh'
              : step === 'token'
                ? 'Nhập mã xác minh đã gửi tới email của bạn'
                : 'Tạo mật khẩu mới cho tài khoản'
            }
          </CardDescription>
        </CardHeader>

<<<<<<< Updated upstream
        {step === 'email' ? (
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
        ) : (
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
        )}
=======
        {(() => {
          if (step === 'email') {
            return (
              <form onSubmit={handleEmailSubmit}>
                <CardContent className="space-y-4">
                  <div className="flex items-center space-x-2 p-4 bg-accent/50 rounded-lg">
                    <Mail className="h-5 w-5 text-primary" />
                    <div className="text-sm">
                      Chúng tôi sẽ gửi mã xác minh tới email của bạn.
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="email">Địa chỉ Email</Label>
                    <Input
                      id="email"
                      type="email"
                      placeholder="Nhập email của bạn"
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
                    {isLoading ? 'Đang gửi...' : 'Gửi mã xác minh'}
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
                      Vui lòng kiểm tra email để lấy mã xác minh.
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="token">Mã xác minh</Label>
                    <Input
                      id="token"
                      type="text"
                      placeholder="Nhập mã xác minh"
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
                    {isLoading ? 'Đang kiểm tra...' : 'Xác minh'}
                  </Button>

                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={() => setStep('email')}
                    className="w-full"
                  >
                    Gửi lại mã
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
>>>>>>> Stashed changes
      </Card>
    </div>
  );
}
