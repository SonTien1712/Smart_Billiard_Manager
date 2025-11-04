import React, { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../ui/card';
import { Alert, AlertDescription } from '../ui/alert';
import { useAuth } from '../AuthProvider';
import { Eye, EyeOff } from 'lucide-react';
import AnimatedLogo from '../prelogin/AnimatedLogo.jsx';


export function SignIn({ onNavigate }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      await login({ email, password });
      onNavigate('dashboard');
    } catch (err) {
      setError(err.message || 'Invalid email or password');
    } finally {
      setIsLoading(false);
    }
  };

<<<<<<< Updated upstream
  const handleGoogleSignIn = async () => {
    setIsGoogleLoading(true);
    setError('');

    try {
      // In a real implementation, you would get the Google token from Google OAuth flow
      // For now, we'll simulate it
      const mockGoogleToken = 'mock-google-token';
      await loginWithGoogle({ googleToken: mockGoogleToken, role: 'CUSTOMER' });
      onNavigate('dashboard');
    } catch (err) {
      setError(err.message || 'Failed to sign in with Google');
    } finally {
      setIsGoogleLoading(false);
    }
  };

=======
>>>>>>> Stashed changes
  return (
    <div className="dark min-h-screen relative px-4 py-8 overflow-hidden">
      {/* Background image overlay */}
      <div className="absolute inset-0 opacity-20">
        <img
          src="https://images.unsplash.com/photo-1662550402015-82675fdc44aa?auto=format&fit=crop&w=1600&q=70"
          alt="Billiard background"
          className="w-full h-full object-cover"
        />
      </div>

      {/* Top brand */}
      <div className="relative z-10 max-w-xl mx-auto mb-8 flex flex-col items-center">
        <AnimatedLogo size="md" />
        <p className="mt-2 text-sm italic text-muted-foreground">"Quản lý thông minh - Kinh doanh hiệu quả"</p>
      </div>

      {/* Login Card */}
      <Card className="relative z-10 w-full max-w-lg mx-auto smooth-shadow" style={{ backgroundColor: 'rgba(3, 4, 8, 0.82)', backdropFilter: 'blur(6px)' }}>
        <CardHeader className="space-y-1">
          <CardTitle className="text-xl">Đăng nhập</CardTitle>
          <CardDescription>Đăng nhập bằng Email (Admin/Chủ quán) hoặc Tên đăng nhập (Nhân viên)</CardDescription>
        </CardHeader>

        <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              {error && (
                <Alert variant="destructive">
                  <AlertDescription>{error}</AlertDescription>
                </Alert>
              )}

              <div className="space-y-2">
                <Label htmlFor="email">Email hoặc Tên đăng nhập</Label>
                <Input
                  id="email"
                  type="text"
                  placeholder={'admin@example.com | chuquan@example.com | username_nv'}
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>

<<<<<<< Updated upstream
            <div className="text-center">
              <Button
                type="button"
                variant="link"
                className="text-sm text-primary"
                onClick={() => onNavigate('forgot-password')}
              >
                Forgot your password?
=======
              <div className="space-y-2">
                <Label htmlFor="password">Mật khẩu</Label>
                <div className="relative">
                  <Input
                    id="password"
                    type={showPassword ? 'text' : 'password'}
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <EyeOff className="h-4 w-4 text-muted-foreground" />
                    ) : (
                      <Eye className="h-4 w-4 text-muted-foreground" />
                    )}
                  </Button>
                </div>
              </div>

              <Button type="submit" className="w-full" disabled={isLoading} style={{ backgroundColor: 'var(--gold-accent)', color: '#0A0E14' }}>
                {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
>>>>>>> Stashed changes
              </Button>

              <div className="text-center">
                <Button
                  type="button"
                  variant="link"
<<<<<<< Updated upstream
                  className="p-0 h-auto text-primary"
                  onClick={() => onNavigate('signup')}
=======
                  className="text-sm text-primary"
                  onClick={() => navigate('/forgot-password')}
>>>>>>> Stashed changes
                >
                  Quên mật khẩu?
                </Button>
              </div>
            </form>
          <div className="text-xs text-muted-foreground text-center mt-2" />
        </CardContent>
        <CardFooter className="justify-center">
          <Button type="button" variant="link" className="text-muted-foreground" onClick={() => navigate('/landing')}>
            ← Quay lại trang chủ
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
}
