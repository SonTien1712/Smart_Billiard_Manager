
import { useState, useEffect } from 'react';
import { Button } from '../ui/button';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Textarea } from '../ui/textarea';
import { Switch } from '../ui/switch';
import {
  LayoutDashboard, CreditCard, Users, BarChart3,
  Clock, Shield, Zap, Star, Check,
  TableProperties, Receipt, UserCog, TrendingUp,
  DollarSign, Calendar, Bell, Smartphone, Cloud, Headphones,
  Mail, Phone, Building2, ArrowRight
} from 'lucide-react';
import { motion } from 'motion/react';
import AnimatedLogo from './AnimatedLogo';

function Landing() {
  // Hook điều hướng của react-router: dùng để chuyển trang nội bộ
  const navigate = useNavigate();

  // Hàm trợ giúp điều hướng nhanh theo chuỗi đích hoặc đặc biệt 'login'
  const goTo = (target) => {
    if (target === 'login') return navigate('/signin');
    if (typeof target === 'string') return navigate(target);
  };

  // Trạng thái chọn chu kỳ thanh toán: false = theo tháng, true = theo năm
  const [isAnnual, setIsAnnual] = useState(false);

  // Trạng thái dữ liệu form liên hệ/đăng ký tư vấn ở phần Contact
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    clubName: '',
    message: ''
  });

  // Danh sách tính năng chính hiển thị ở mục "Tính năng"
  const mainFeatures = [
    {
      icon: <TableProperties className="w-12 h-12" />,
      title: 'Quản lý bàn chơi',
      description: 'Theo dõi trạng thái tất cả bàn billiard theo thời gian thực',
      details: [
        'Sơ đồ trực quan với màu sắc phân biệt trạng thái',
        'Tự động tính tiền theo thời gian chơi',
        'Đặt bàn trước và quản lý hàng chờ',
        'Lịch sử sử dụng từng bàn'
      ],
      image:
        'https://images.unsplash.com/photo-1545774425-73eb393f05d5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiaWxsaWFyZCUyMHBvb2wlMjB0YWJsZSUyMHBsYXllcnxlbnwxfHx8fDE3NjExMzY3OTd8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral'
    },
    {
      icon: <Receipt className="w-12 h-12" />,
      title: 'Hóa đơn & Thanh toán',
      description: 'Xuất hóa đơn nhanh chóng và quản lý thanh toán dễ dàng',
      details: [
        'Tạo hóa đơn tự động khi kết thúc chơi',
        'Hỗ trợ nhiều hình thức thanh toán',
        'In hóa đơn hoặc gửi qua email',
        'Quản lý công nợ khách hàng'
      ],
      image:
        'https://images.unsplash.com/photo-1633851463569-29e72ba1fac4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiaWxsaWFyZCUyMGN1ZSUyMHN0aWNrJTIwYmFsbHN8ZW58MXx8fHwxNzYxMTM2Nzk4fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral'
    },
    {
      icon: <UserCog className="w-12 h-12" />,
      title: 'Quản lý nhân viên & Ca làm',
      description: 'Sắp xếp ca làm việc và theo dõi hiệu suất nhân viên',
      details: [
        'Tạo lịch làm việc linh hoạt',
        'Chấm công tự động',
        'Theo dõi doanh thu theo nhân viên',
        'Phân quyền truy cập hệ thống'
      ],
      image:
        'https://images.unsplash.com/photo-1745972880151-3f4fa366e05b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwb29sJTIwaGFsbCUyMGJpbGxpYXJkJTIwcHJvZmVzc2lvbmFsfGVufDF8fHx8MTc2MTEzNjc5OHww&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral'
    },
    {
      icon: <TrendingUp className="w-12 h-12" />,
      title: 'Báo cáo & Phân tích',
      description: 'Thống kê chi tiết giúp ra quyết định kinh doanh',
      details: [
        'Báo cáo doanh thu theo ngày/tuần/tháng',
        'Biểu đồ trực quan dễ hiểu',
        'Phân tích bàn chơi phổ biến nhất',
        'Xuất báo cáo Excel/PDF'
      ],
      image:
        'https://images.unsplash.com/photo-1732401727462-bda2d1d69036?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiaWxsaWFyZCUyMHRhYmxlJTIwZ2FtZSUyMG5pZ2h0fGVufDF8fHx8MTc2MTEzNjc5OHww&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral'
    }
  ];

  // Các tính năng bổ sung (icon + tiêu đề + mô tả ngắn) hiển thị dạng lưới
  const additionalFeatures = [
    { icon: <Clock />, title: 'Tính giờ tự động', desc: 'Tự động tính thời gian và tiền' },
    { icon: <DollarSign />, title: 'Giá linh hoạt', desc: 'Thiết lập giá theo khung giờ' },
    { icon: <Calendar />, title: 'Đặt bàn trước', desc: 'Khách hàng có thể đặt bàn online' },
    { icon: <Bell />, title: 'Thông báo realtime', desc: 'Nhận cảnh báo khi có sự kiện' },
    { icon: <Shield />, title: 'Bảo mật cao', desc: 'Dữ liệu được mã hóa an toàn' },
    { icon: <Smartphone />, title: 'Responsive design', desc: 'Sử dụng trên mọi thiết bị' },
    { icon: <Cloud />, title: 'Lưu trữ cloud', desc: 'Không lo mất dữ liệu' },
    { icon: <Headphones />, title: 'Hỗ trợ 24/7', desc: 'Đội ngũ hỗ trợ luôn sẵn sàng' }
  ];

  // Lợi ích tổng quan (hiển thị ở mục "Tại sao chọn BilliardPro?")
  const benefits = [
    { icon: <Clock />, text: 'Tiết kiệm thời gian quản lý' },
    { icon: <Shield />, text: 'Bảo mật dữ liệu an toàn' },
    { icon: <Zap />, text: 'Giao diện nhanh và mượt mà' },
    { icon: <Star />, text: 'Hỗ trợ 24/7' }
  ];

  // Cấu hình các gói giá (Basic/Pro/Enterprise)
  // - monthlyPrice: giá trả theo tháng
  // - annualPrice: giá trả theo năm (thường rẻ hơn tổng 12 tháng)
  // - features: danh sách quyền lợi của gói
  // - popular: đánh dấu gói nổi bật để nhấn mạnh trong UI
  const plans = [
    {
      name: 'Basic',
      description: 'Cho quán nhỏ, mới bắt đầu',
      monthlyPrice: 299000,
      annualPrice: 2990000,
      features: [
        'Tối đa 5 bàn chơi',
        'Quản lý hóa đơn cơ bản',
        '2 tài khoản nhân viên',
        'Báo cáo cơ bản',
        'Hỗ trợ email',
        'Lưu trữ 3 tháng'
      ],
      popular: false
    },
    {
      name: 'Pro',
      description: 'Phù hợp với hầu hết các quán',
      monthlyPrice: 599000,
      annualPrice: 5990000,
      features: [
        'Tối đa 15 bàn chơi',
        'Quản lý hóa đơn nâng cao',
        '10 tài khoản nhân viên',
        'Báo cáo chi tiết + biểu đồ',
        'Đặt bàn trước online',
        'Hỗ trợ chat realtime',
        'Lưu trữ 1 năm',
        'Xuất báo cáo Excel/PDF'
      ],
      popular: true
    },
    {
      name: 'Enterprise',
      description: 'Cho chuỗi quán, quy mô lớn',
      monthlyPrice: 1299000,
      annualPrice: 12990000,
      features: [
        'Không giới hạn bàn chơi',
        'Quản lý đa chi nhánh',
        'Không giới hạn nhân viên',
        'Báo cáo nâng cao + AI insights',
        'API tích hợp',
        'Hỗ trợ 24/7 ưu tiên',
        'Lưu trữ vĩnh viễn',
        'Tùy chỉnh theo yêu cầu',
        'Đào tạo onboarding'
      ],
      popular: false
    }
  ];

  // Định dạng số tiền theo locale Việt Nam (VND)
  const formatPrice = (price) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(price);
  };

  // Tính khoản tiết kiệm khi chọn gói năm so với trả tháng (áp dụng gói Pro)
  const getSavings = () => {
    const monthlyCost = plans[1].monthlyPrice * 12;
    const annualCost = plans[1].annualPrice;
    const savings = monthlyCost - annualCost;
    const percentage = Math.round((savings / monthlyCost) * 100);
    return { savings, percentage };
  };

  // Giải cấu trúc kết quả tính toán để dùng trong UI bảng giá
  const { savings, percentage } = getSavings();

  // Xử lý submit form liên hệ: demo log + thông báo cảm ơn
  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Form submitted:', formData);
    alert('Cảm ơn bạn đã đăng ký! Chúng tôi sẽ liên hệ sớm nhất.');
  };

  return (
    <div className="dark landing-theme min-h-screen billiard-texture relative pt-16">
      {/* Hiệu ứng ánh sáng/đèn chùm để tạo không khí quán billiard */}
      <div className="fixed inset-0 pointer-events-none z-10">
        {/* Vệt sáng chính từ phía trên trung tâm */}
        <div
          className="absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[800px]"
          style={{
            background: 'radial-gradient(ellipse at center, rgba(241, 194, 50, 0.12) 0%, transparent 60%)',
            filter: 'blur(40px)'
          }}
        />
        {/* Hiệu ứng tối góc (vignette) để tăng độ tập trung nội dung */}
        <div
          className="absolute inset-0"
          style={{
            background: 'radial-gradient(ellipse at center, transparent 0%, rgba(0, 0, 0, 0.4) 100%)'
          }}
        />
      </div>

      {/* Navigation (theo mẫu: sticky + blur, nền bán trong suốt) */}
      <nav className="border-b border-border/50 backdrop-blur-sm sticky top-0 z-50 bg-background/80 relative">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <motion.div animate={{ y: [0, -3, 0] }} transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}>
              <AnimatedLogo size="sm" />
            </motion.div>
            <div className="hidden md:flex gap-6">
              <a href="#home" className="text-white hover:text-primary transition-colors">Trang chủ</a>
              <a href="#features" className="text-white hover:text-primary transition-colors">Tính năng</a>
              <a href="#pricing" className="text-white hover:text-primary transition-colors">Bảng giá</a>
              <a href="#pricing" className="text-white hover:text-primary transition-colors">Liên hệ</a>
            </div>
            <Button onClick={() => goTo('login')} style={{ backgroundColor: 'var(--gold-accent)', color: '#0A0E14' }} className="rounded-full px-5">Đăng nhập</Button>
          </div>
        </div>
      </nav>

      {/* Hero Section (theo mẫu: padding dọc, ảnh mờ 20%) */}
      <section id="home" className="relative overflow-hidden py-32 md:py-44 min-h-[520px] md:min-h-[720px]">
        <div className="absolute inset-0 opacity-20">
          <img
            src="https://images.unsplash.com/photo-1662550402015-82675fdc44aa?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiaWxsaWFyZCUyMHRhYmxlJTIwcG9vbCUyMGhhbGwlMjBtb2Rlcm58ZW58MXx8fHwxNzYxMDM0OTU3fDA&ixlib=rb-4.1.0&q=80&w=1080"
            alt="Pool hall"
            className="w-full h-full object-cover"
            style={{ objectPosition: 'center 50%' }}
          />
        </div>
        {/* Spotlight nhẹ theo mẫu */}
        <div
          className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[1000px] h-[600px] pointer-events-none"
          style={{
            background: 'radial-gradient(ellipse at center, rgba(241, 194, 50, 0.08) 0%, transparent 70%)',
            filter: 'blur(60px)'
          }}
        />

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 z-20">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="text-center max-w-3xl mx-auto"
          >
            <h1 className="text-5xl md:text-6xl mb-4">
              <span style={{ color: 'var(--gold-accent)' }}>Quản lý quán Billiard</span>
              <br />
              <span className="text-white">chuyên nghiệp & hiện đại</span>
            </h1>
            <p className="italic text-lg md:text-2xl mb-4" style={{ color: 'var(--gold-accent)' }}>
              "Quản lý thông minh - Kinh doanh hiệu quả"
            </p>
            <p className="text-xl text-white mb-8">
              Giải pháp toàn diện cho chủ quán billiard: quản lý bàn chơi, nhân viên, hóa đơn và báo cáo doanh thu
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <motion.div
                whileHover={{ scale: 1.05, y: -2 }}
                whileTap={{ scale: 0.98 }}
                transition={{ type: 'spring', stiffness: 400, damping: 17 }}
              >
                <Button
                  size="lg"
                  onClick={() => goTo('login')}
                  style={{ backgroundColor: 'var(--gold-accent)', color: '#0A0E14' }}
                  className="gold-glow"
                >
                  Dùng thử miễn phí
                </Button>
              </motion.div>
              <motion.div
                whileHover={{ scale: 1.05, y: -2 }}
                whileTap={{ scale: 0.98 }}
                transition={{ type: 'spring', stiffness: 400, damping: 17 }}
              >
                <Button
                  size="lg"
                  variant="outline"
                  onClick={() => document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' })}
                >
                  Xem tính năng
                </Button>
              </motion.div>
            </div>
          </motion.div>
        </div>

        {/* Đường kẻ gradient vàng mảnh để phân tách các khối nội dung */}
        <motion.div
          className="h-px w-full mt-24"
          style={{ background: 'linear-gradient(90deg, transparent, var(--gold-accent), transparent)' }}
          initial={{ scaleX: 0, opacity: 0 }}
          whileInView={{ scaleX: 1, opacity: 1 }}
          transition={{ duration: 1.2, ease: 'easeOut' }}
          viewport={{ once: true }}
        />
      </section>

      {/* Khu vực Lợi ích: tóm tắt nhanh lợi ích nổi bật dành cho chủ quán */}
      <section className="py-24 md:py-28 relative scroll-mt-32" style={{ backgroundColor: 'var(--billiard-green-dark)' }}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-20 md:mb-24">
            <h2 className="text-4xl mb-4 text-white">Tại sao chọn BilliardPro?</h2>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8 max-w-7xl mx-auto">
            {benefits.map((benefit, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, scale: 0.9 }}
                whileInView={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                viewport={{ once: true }}
                className="flex flex-col items-center text-center"
                
              >
                <div
                  className="w-20 h-20 rounded-full flex items-center justify-center mb-4"
                  style={{ backgroundColor: 'var(--gold-accent)', color: '#0A0E14' }}
                >
                  {benefit.icon}
                </div>
                <p className="text-white text-lg md:text-xl">{benefit.text}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Khu vực Tính năng chính: hiển thị các bloc mô tả chi tiết + ảnh minh họa */}
      <section id="features" className="py-24 pt-28 relative scroll-mt-32 mt-20 md:mt-28">
        {/* Ánh sáng vàng nhạt phía sau nội dung để tạo chiều sâu */}
        <div
          className="absolute inset-0 pointer-events-none -z-10"
          style={{
            background:
              'radial-gradient(700px at 20% 20%, rgba(241, 194, 50, 0.08), transparent 60%), radial-gradient(700px at 80% 60%, rgba(241, 194, 50, 0.06), transparent 60%)'
          }}
        />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              viewport={{ once: true }}
            >
              <h2 className="text-4xl md:text-5xl mb-4 text-white">
                Tính năng <span style={{ color: 'var(--gold-accent)' }}>toàn diện</span>
              </h2>
              <p className="text-xl text-white max-w-3xl mx-auto">
                Mọi công cụ bạn cần để quản lý câu lạc bộ billiard chuyên nghiệp và hiệu quả
              </p>
            </motion.div>
          </div>

          <div className="space-y-36 md:space-y-44">
            {mainFeatures.map((feature, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 40 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6 }}
                viewport={{ once: true }}
                className="grid md:grid-cols-2 gap-12 md:gap-16 items-center"
              >
                {index % 2 === 0 ? (
                  <>
                    {/* Text first on even rows */}
                    <div>
                      <div
                        className="w-20 h-20 rounded-xl flex items-center justify-center mb-6"
                        style={{ backgroundColor: 'var(--billiard-green)', color: 'var(--gold-accent)' }}
                      >
                        {feature.icon}
                      </div>
                      <h3 className="text-4xl md:text-5xl mb-4 text-white leading-tight">{feature.title}</h3>
                      <p className="text-white mb-6 text-lg">{feature.description}</p>
                      <ul className="space-y-3">
                        {feature.details.map((detail, i) => (
                          <motion.li
                            key={i}
                            className="flex items-start gap-3"
                            initial={{ opacity: 0, x: -20 }}
                            whileInView={{ opacity: 1, x: 0 }}
                            transition={{ duration: 0.4, delay: i * 0.1 }}
                            viewport={{ once: true }}
                          >
                            <motion.div
                              className="w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5"
                              style={{ backgroundColor: 'var(--gold-accent)' }}
                              whileHover={{ scale: 1.2, rotate: 360 }}
                              transition={{ type: 'spring', stiffness: 300 }}
                            >
                              <svg
                                className="w-4 h-4"
                                style={{ color: '#0A0E14' }}
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                              >
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                              </svg>
                            </motion.div>
                            <span className="text-white">{detail}</span>
                          </motion.li>
                        ))}
                      </ul>
                    </div>
                    <div>
                      <motion.div whileHover={{ scale: 1.02, y: -8 }} transition={{ type: 'spring', stiffness: 300, damping: 20 }}>
                        <Card className="smooth-shadow overflow-hidden rounded-3xl shadow-xl shadow-black/30 ring-1 ring-white/10">
                          <div className={`relative overflow-hidden ${index === 3 ? 'h-[220px] sm:h-[260px] md:h-[320px] lg:h-[360px]' : 'h-[220px] sm:h-[260px] md:h-[340px] lg:h-[380px]'}`}>
                            <motion.img
                              src={feature.image}
                              alt={feature.title}
                              className="w-full h-full object-cover"
                              style={index === 3 ? { objectPosition: 'center 40%' } : {}}
                              whileHover={{ scale: 1.1 }}
                              transition={{ duration: 0.6 }}
                            />
                            <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
                            <motion.div
                              className="absolute inset-0"
                              initial={{ opacity: 0 }}
                              whileHover={{ opacity: 1 }}
                              transition={{ duration: 0.3 }}
                              style={{ background: 'radial-gradient(circle at center, rgba(241, 194, 50, 0.15) 0%, transparent 60%)' }}
                            />
                          </div>
                        </Card>
                      </motion.div>
                    </div>
                  </>
                ) : (
                  <>
                    {/* Image first on odd rows */}
                    <div>
                      <motion.div whileHover={{ scale: 1.02, y: -8 }} transition={{ type: 'spring', stiffness: 300, damping: 20 }}>
                        <Card className="smooth-shadow overflow-hidden rounded-3xl shadow-xl shadow-black/30 ring-1 ring-white/10">
                          <div className={`relative overflow-hidden ${index === 3 ? 'h-[220px] sm:h-[260px] md:h-[320px] lg:h-[360px]' : 'h-[220px] sm:h-[260px] md:h-[340px] lg:h-[380px]'}`}>
                            <motion.img
                              src={feature.image}
                              alt={feature.title}
                              className="w-full h-full object-cover"
                              style={index === 3 ? { objectPosition: 'center 40%' } : {}}
                              whileHover={{ scale: 1.1 }}
                              transition={{ duration: 0.6 }}
                            />
                            <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
                            <motion.div
                              className="absolute inset-0"
                              initial={{ opacity: 0 }}
                              whileHover={{ opacity: 1 }}
                              transition={{ duration: 0.3 }}
                              style={{ background: 'radial-gradient(circle at center, rgba(241, 194, 50, 0.15) 0%, transparent 60%)' }}
                            />
                          </div>
                        </Card>
                      </motion.div>
                    </div>
                    <div>
                      <div
                        className="w-20 h-20 rounded-xl flex items-center justify-center mb-6"
                        style={{ backgroundColor: 'var(--billiard-green)', color: 'var(--gold-accent)' }}
                      >
                        {feature.icon}
                      </div>
                      <h3 className="text-4xl md:text-5xl mb-4 text-white leading-tight">{feature.title}</h3>
                      <p className="text-white mb-6 text-lg">{feature.description}</p>
                      <ul className="space-y-3">
                        {feature.details.map((detail, i) => (
                          <motion.li
                            key={i}
                            className="flex items-start gap-3"
                            initial={{ opacity: 0, x: -20 }}
                            whileInView={{ opacity: 1, x: 0 }}
                            transition={{ duration: 0.4, delay: i * 0.1 }}
                            viewport={{ once: true }}
                          >
                            <motion.div
                              className="w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5"
                              style={{ backgroundColor: 'var(--gold-accent)' }}
                              whileHover={{ scale: 1.2, rotate: 360 }}
                              transition={{ type: 'spring', stiffness: 300 }}
                            >
                              <svg
                                className="w-4 h-4"
                                style={{ color: '#0A0E14' }}
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                              >
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                              </svg>
                            </motion.div>
                            <span className="text-white">{detail}</span>
                          </motion.li>
                        ))}
                      </ul>
                    </div>
                  </>
                )}
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Additional Features Grid */}
      <section className="py-24 scroll-mt-32 mt-28 md:mt-40" style={{ backgroundColor: 'var(--billiard-green-dark)' }}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl mb-4 text-white">Và còn nhiều hơn thế nữa...</h2>
            <p className="text-white">Những tính năng bổ sung giúp trải nghiệm hoàn hảo hơn</p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6 max-w-7xl mx-auto">
            {additionalFeatures.map((feature, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, scale: 0.9 }}
                whileInView={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.4, delay: index * 0.05 }}
                viewport={{ once: true }}
              >
                <Card className="h-full hover:border-primary transition-all duration-300 rounded-2xl">
                  <CardHeader>
                    <div
                      className="w-12 h-12 rounded-lg flex items-center justify-center mb-3"
                      style={{ backgroundColor: 'var(--gold-accent)', color: '#0A0E14' }}
                    >
                      {feature.icon}
                    </div>
                    <CardTitle className="text-lg text-white">{feature.title}</CardTitle>
                  </CardHeader>
                    <CardContent>
                    <CardDescription>{feature.desc}</CardDescription>
                  </CardContent>
                </Card>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Khu vực Bảng giá: chuyển đổi tháng/năm, hiển thị 3 gói và lợi ích */}
      <section id="pricing" className="py-24 pt-28 scroll-mt-32 mt-28 md:mt-40">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              viewport={{ once: true }}
            >
              <h2 className="text-4xl md:text-5xl mb-4 text-white">
                Bảng giá <span style={{ color: 'var(--gold-accent)' }}>minh bạch</span>
              </h2>
              <p className="text-xl text-white/80 max-w-3xl mx-auto mb-8">
                Chọn gói phù hợp với quy mô quán của bạn. Dùng thử miễn phí 14 ngày.
              </p>

              {/* Công tắc chuyển đổi hình thức thanh toán: tháng <-> năm */}
              <div className="flex items-center justify-center gap-4 mb-4">
                <Label htmlFor="billing-toggle" className={!isAnnual ? 'text-white' : 'text-white/60'}>
                  Thanh toán hàng tháng
                </Label>
                <Switch id="billing-toggle" checked={isAnnual} onCheckedChange={setIsAnnual} />
                <Label htmlFor="billing-toggle" className={isAnnual ? 'text-white' : 'text-white/60'}>
                  Thanh toán hàng năm
                </Label>
              </div>
              {isAnnual && (
                // Khi chọn trả theo năm, hiển thị thông tin phần trăm và số tiền tiết kiệm ước tính
                <motion.p initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} className="text-sm" style={{ color: 'var(--gold-accent)' }}>
                  Tiết kiệm {percentage}% ({formatPrice(savings)}) khi thanh toán theo năm
                </motion.p>
              )}
            </motion.div>
          </div>

          <div className="grid md:grid-cols-3 gap-8 mb-16">
            {plans.map((plan, index) => (
              <motion.div
                key={plan.name}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                viewport={{ once: true }}
                whileHover={{ y: -8, transition: { duration: 0.3 } }}
                className="relative"
              >
                {plan.popular && (
                  <motion.div
                    className="absolute -top-5 left-1/2 -translate-x-1/2 px-4 py-1 rounded-full text-sm flex items-center gap-1"
                    style={{ backgroundColor: 'var(--gold-accent)', color: '#0A0E14' }}
                    animate={{ y: [0, -3, 0] }}
                    transition={{ duration: 2, repeat: Infinity }}
                  >
                    <Star className="w-4 h-4 fill-current" />
                    Phổ biến nhất
                  </motion.div>
                )}
                <Card
                  className={`h-full smooth-shadow rounded-2xl ${plan.popular ? 'border-2' : ''}`}
                  style={plan.popular ? { borderColor: 'var(--gold-accent)' } : {}}
                >
                  <CardHeader>
                    <CardTitle className="text-2xl text-white">{plan.name}</CardTitle>
                    <CardDescription>{plan.description}</CardDescription>
                    <div className="mt-4">
                      <div className="flex items-baseline gap-2">
                        <span className="text-4xl" style={{ color: 'var(--gold-accent)' }}>
                          {formatPrice(isAnnual ? plan.annualPrice : plan.monthlyPrice)}
                        </span>
                        <span className="text-white/60">{isAnnual ? '/ năm' : '/ tháng'}</span>
                      </div>
                      {isAnnual && (
                        <p className="text-sm text-white/60 mt-1">
                          {formatPrice(Math.round(plan.annualPrice / 12))}/tháng khi tính trung bình
                        </p>
                      )}
                    </div>
                  </CardHeader>
                  <CardContent>
                    <Button
                      className="w-full mb-6 rounded-full"
                      variant={plan.popular ? 'default' : 'outline'}
                      onClick={() => goTo('login')}
                      style={plan.popular ? { backgroundColor: 'var(--gold-accent)', color: '#0A0E14' } : {}}
                    >
                      Dùng thử miễn phí
                    </Button>

                    {/* Danh sách tính năng/đặc quyền của từng gói */}
                    <ul className="space-y-3">
                      {plan.features.map((feature, i) => (
                        <li key={i} className="flex items-start gap-3">
                          <div
                            className="w-5 h-5 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5"
                            style={{ backgroundColor: 'var(--billiard-green)' }}
                          >
                            <Check className="w-3 h-3" style={{ color: 'var(--gold-accent)' }} />
                          </div>
                          <span className="text-sm text-white/90">{feature}</span>
                        </li>
                      ))}
                    </ul>
                  </CardContent>
                </Card>
              </motion.div>
            ))}
          </div>

          {/* FAQ */}
          <div className="max-w-3xl mx-auto mt-20">
            <h3 className="text-3xl mb-8 text-center text-white">Câu hỏi thường gặp</h3>
            <div className="space-y-4">
              {[
                {
                  q: 'Tôi có thể dùng thử miễn phí không?',
                  a: 'Có! Tất cả gói đều có 14 ngày dùng thử miễn phí, không cần thẻ tín dụng.'
                },
                {
                  q: 'Tôi có thể nâng cấp hoặc hạ cấp gói bất cứ lúc nào không?',
                  a: 'Hoàn toàn có thể. Bạn có thể thay đổi gói bất cứ lúc nào và chỉ trả phần chênh lệch.'
                },
                {
                  q: 'Phương thức thanh toán nào được hỗ trợ?',
                  a: 'Chúng tôi hỗ trợ chuyển khoản ngân hàng, ví điện tử (Momo, ZaloPay) và thẻ tín dụng.'
                },
                {
                  q: 'Dữ liệu của tôi có an toàn không?',
                  a: 'Dữ liệu được mã hóa và sao lưu hàng ngày. Chúng tôi tuân thủ các tiêu chuẩn bảo mật cao nhất.'
                }
              ].map((faq, index) => (
                <Card key={index}>
                  <CardHeader>
                    <CardTitle className="text-lg text-white">{faq.q}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <p className="text-white/70">{faq.a}</p>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-border/50 py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid md:grid-cols-4 gap-8">
            <div>
              <AnimatedLogo size="sm" />
              <p className="text-sm text-white mt-4">Giải pháp quản lý quán billiard hàng đầu Việt Nam</p>
            </div>
            <div>
              <h4 className="mb-4 text-white">Sản phẩm</h4>
              <ul className="space-y-2 text-sm text-white">
                <li>
                  <a href="#features" className="hover:text-primary transition-colors">
                    Tính năng
                  </a>
                </li>
                <li>
                  <a href="#pricing" className="hover:text-primary transition-colors">
                    Bảng giá
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="mb-4 text-white">Hỗ trợ</h4>
              <ul className="space-y-2 text-sm text-white">
                <li>
                  <a href="#" className="hover:text-primary transition-colors">
                    Tài liệu
                  </a>
                </li>
                <li>
                  <a href="#pricing" className="hover:text-primary transition-colors">Liên hệ</a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="mb-4 text-white">Pháp lý</h4>
              <ul className="space-y-2 text-sm text-white">
                <li>
                  <a href="#" className="hover:text-primary transition-colors">
                    Điều khoản
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-primary transition-colors">
                    Bảo mật
                  </a>
                </li>
              </ul>
            </div>
          </div>
          <div className="mt-12 pt-8 border-t border-border/50 text-center text-sm text-white">
            © 2025 BilliardPro. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
}


export default Landing;
