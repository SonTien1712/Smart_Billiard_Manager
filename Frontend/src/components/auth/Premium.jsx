import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthProvider';

export const plans = [
  {
    id: '1',
    title: '3 Month Package',
    price: '499.000đ',
    description:
      'Flexible choice for centers that are experimenting and want to explore management features.',
    badge: 'Popular',
    highlight: false,
    features: [
      'Smart table booking with time slot suggestions',
      'Daily and per-table revenue tracking',
      'Automatic reminders via SMS/Email',
      'Priority technical support during business hours',
    ],
  },
  {
    id: '2',
    title: '1 Year Package',
    price: '1.699.000đ',
    description:
      'Long-term cost savings, suitable for centers that want stable operation with full features.',
    badge: 'Save 15%',
    highlight: true,
    features: [
      'All features of the 3 Month Package',
      '24/7 technical support and operational optimization consulting',
    ],
  },
];

const Premium = () => {
  const [hoveredPlan, setHoveredPlan] = useState(null);
  const { logout } = useAuth();
  const { handlePayment } = useAuth();

  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const { refreshUser } = useAuth();

  const isSubscriptionActive = (expiry) => {
    if (!expiry) return false;
    return new Date(expiry).getTime() >= new Date().getTime();
  };

  useEffect(() => {
    const handlePaymentReturn = async () => {
      const success = searchParams.get('success');
      const error = searchParams.get('error');
      
      if (success) {
        // Refresh user data để cập nhật expiryDate
        const updatedUser = await refreshUser();
        // Force update state và check
        if (updatedUser?.role === 'CUSTOMER' && isSubscriptionActive(updatedUser?.expiryDate)) {
          setTimeout(() => {
            navigate('/dashboard/customer');
          }, 200);
        }
      } else if (error) {
        alert('cancelled payment');
        navigate('/premium');  // Stay on premium
      }
    };

    handlePaymentReturn();
  }, [searchParams, navigate, refreshUser]);

  return (
    <section style={styles.page}>
      <button
        onClick={logout}
        style={styles.logoutButton}
        onMouseEnter={(e) => e.target.style.transform = 'translateY(-2px)'}
        onMouseLeave={(e) => e.target.style.transform = 'translateY(0)'}
      >
        Đăng xuất
      </button>
      <div style={styles.container}>
        <header style={styles.header}>
          <h1 style={styles.title}>Nâng cấp Premium</h1>
          <p style={styles.subtitle}>
            Khai thác toàn bộ sức mạnh Smart Billiard Manager với báo cáo thời gian thực, nhắc lịch
            thông minh và dịch vụ hỗ trợ ưu tiên.
          </p>
        </header>

        <div style={styles.grid}>
          {plans.map((plan) => {
            const isActive = hoveredPlan === plan.id;

            return (
              <article
                key={plan.id}
                style={styles.card(isActive, plan.highlight)}
                onMouseEnter={() => setHoveredPlan(plan.id)}
                onMouseLeave={() => setHoveredPlan(null)}
              >
                <span style={styles.badge(plan.highlight)}>{plan.badge}</span>

                <div>
                  <h2 style={styles.planTitle}>{plan.title}</h2>
                  <p style={styles.description}>{plan.description}</p>
                </div>

                <div>
                  <div style={styles.price}>{plan.price}</div>
                  <div style={styles.period}>{plan.period}</div>
                </div>

                <ul style={styles.featureList}>
                  {plan.features.map((feature) => (
                    <li key={feature} style={styles.featureItem}>
                      <span style={styles.checkIcon}>✔</span>
                      <span>{feature}</span>
                    </li>
                  ))}
                </ul>

                <button type="button" style={styles.button(plan.highlight, isActive)} onClick={() => handlePayment(plan.id, plan.price)}>
                  Chọn gói này
                </button>
              </article>
            );
          })}
        </div>
      </div>
    </section>
  );
};

const styles = {
  page: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '64px 24px',
    background: 'linear-gradient(135deg, #eef2ff 0%, #fef9f5 50%, #f7f6ff 100%)',
  },
  container: {
    width: '100%',
    maxWidth: '1100px',
    display: 'grid',
    gap: '36px',
  },
  header: {
    textAlign: 'center',
    color: '#111827',
  },
  title: {
    fontSize: '38px',
    fontWeight: 700,
    marginBottom: '12px',
  },
  subtitle: {
    fontSize: '18px',
    color: '#4b5563',
    maxWidth: '640px',
    margin: '0 auto',
    lineHeight: 1.6,
  },
  grid: {
    display: 'grid',
    gap: '24px',
    gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
  },
  card: (active, highlight) => ({
    position: 'relative',
    display: 'flex',
    flexDirection: 'column',
    gap: '18px',
    padding: highlight ? '36px 32px' : '32px 28px',
    borderRadius: '24px',
    background: highlight ? '#ffffff' : 'rgba(255, 255, 255, 0.9)',
    border: highlight ? '2px solid rgba(99, 102, 241, 0.25)' : '1px solid rgba(148, 163, 184, 0.18)',
    boxShadow: active
      ? '0 22px 45px rgba(99, 102, 241, 0.24)'
      : '0 18px 35px rgba(15, 23, 42, 0.08)',
    transform: active ? 'translateY(-12px)' : 'translateY(0)',
    transition: 'transform 220ms ease, box-shadow 220ms ease, border 220ms ease',
  }),
  badge: (highlight) => ({
    alignSelf: 'flex-start',
    fontSize: '13px',
    fontWeight: 600,
    padding: '6px 14px',
    borderRadius: '999px',
    letterSpacing: '0.4px',
    color: highlight ? '#fff' : '#1f2937',
    background: highlight ? '#6366f1' : '#e0e7ff',
  }),
  planTitle: {
    fontSize: '24px',
    fontWeight: 700,
    marginBottom: '6px',
    color: '#111827',
  },
  description: {
    fontSize: '15px',
    color: '#4b5563',
    lineHeight: 1.7,
  },
  price: {
    fontSize: '36px',
    fontWeight: 700,
    color: '#111827',
  },
  period: {
    fontSize: '14px',
    fontWeight: 600,
    color: '#6366f1',
    letterSpacing: '0.3px',
  },
  featureList: {
    margin: '8px 0 0',
    padding: 0,
    listStyle: 'none',
    display: 'grid',
    gap: '10px',
  },
  featureItem: {
    display: 'flex',
    alignItems: 'flex-start',
    gap: '8px',
    fontSize: '15px',
    color: '#1f2937',
  },
  checkIcon: {
    flexShrink: 0,
    marginTop: '2px',
    width: '18px',
    height: '18px',
    borderRadius: '50%',
    border: '2px solid #6366f1',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '12px',
    fontWeight: 700,
    color: '#6366f1',
    background: 'rgba(99, 102, 241, 0.08)',
  },
  button: (highlight, active) => ({
    marginTop: 'auto',
    padding: '14px 18px',
    fontSize: '15px',
    fontWeight: 600,
    borderRadius: '12px',
    border: 'none',
    cursor: 'pointer',
    color: highlight ? '#fff' : '#1f2937',
    background: highlight ? '#6366f1' : '#e2e8f0',
    boxShadow: active && highlight ? '0 16px 35px rgba(79, 70, 229, 0.35)' : 'none',
    transform: active ? 'scale(1.03)' : 'scale(1)',
    transition: 'transform 220ms ease, box-shadow 220ms ease, background 220ms ease',
  }),
  logoutButton: {
    position: 'fixed',
    bottom: '20px',
    right: '20px',
    padding: '12px 24px',
    fontSize: '14px',
    fontWeight: 600,
    borderRadius: '8px',
    border: 'none',
    cursor: 'pointer',
    color: '#fff',
    background: 'linear-gradient(135deg, #ef4444, #dc2626)',
    boxShadow: '0 4px 12px rgba(239, 68, 68, 0.3)',
    transition: 'transform 200ms ease, box-shadow 200ms ease',
    zIndex: 1000,
  },
};

export { Premium };
