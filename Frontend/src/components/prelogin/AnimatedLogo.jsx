import React from 'react';
import { motion } from 'motion/react';

export function AnimatedLogo({ size = 'md', showText = true }) {
  const sizes = {
    sm: { container: 56, text: 'text-xl', ball: 40 },
    md: { container: 72, text: 'text-3xl', ball: 56 },
    lg: { container: 108, text: 'text-5xl', ball: 84 }
  };
  const current = sizes[size] || sizes.md;

  const particles = Array.from({ length: 12 }, (_, i) => ({ angle: (i * 360) / 12, delay: i * 0.1 }));

  return (
    <div className="flex items-center gap-3">
      <div className="relative flex items-center justify-center" style={{ width: current.container, height: current.container }}>
        <motion.div
          className="absolute rounded-full"
          style={{ width: current.container, height: current.container, background: 'radial-gradient(circle, rgba(241, 194, 50, 0.15) 0%, transparent 70%)' }}
          animate={{ scale: [1, 1.2, 1], opacity: [0.5, 0.8, 0.5] }}
          transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}
        />

        {particles.map((p, i) => (
          <motion.div
            key={i}
            className="absolute rounded-full"
            style={{ width: 4, height: 4, background: 'var(--gold-accent)', boxShadow: '0 0 8px var(--gold-accent)', left: '50%', top: '50%' }}
            animate={{
              x: [0, Math.cos((p.angle * Math.PI) / 180) * (current.container * 0.6), 0],
              y: [0, Math.sin((p.angle * Math.PI) / 180) * (current.container * 0.6), 0],
              opacity: [0, 1, 0],
              scale: [0, 1, 0]
            }}
            transition={{ duration: 3, repeat: Infinity, delay: p.delay, ease: 'easeInOut' }}
          />
        ))}

        <motion.div
          className="relative rounded-full z-10"
          style={{
            width: current.ball,
            height: current.ball,
            background: 'radial-gradient(circle at 35% 35%, #2a2a2a, #000000)',
            boxShadow: '0 6px 20px rgba(0,0,0,0.6), inset 0 2px 4px rgba(255,255,255,0.2), 0 0 30px rgba(241, 194, 50, 0.3)',
            border: '2px solid rgba(0,0,0,0.3)'
          }}
          initial={{ scale: 0, rotate: -180 }}
          animate={{ scale: 1, rotate: 0 }}
          transition={{ duration: 0.8, ease: 'backOut' }}
          whileHover={{ scale: 1.1, rotate: 360, transition: { duration: 0.6 } }}
        >
          <div className="absolute rounded-full" style={{ width: current.ball * 0.25, height: current.ball * 0.25, top: '20%', left: '25%', background: 'radial-gradient(circle, rgba(255,255,255,0.4) 0%, transparent 70%)' }} />
          <motion.div
            className="absolute rounded-full flex items-center justify-center"
            style={{ width: current.ball * 0.55, height: current.ball * 0.55, top: '50%', left: '50%', transform: 'translate(-50%, -50%)', backgroundColor: '#ffffff', boxShadow: '0 2px 6px rgba(0,0,0,0.3), inset 0 1px 2px rgba(0,0,0,0.1)' }}
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.3, duration: 0.4, ease: 'backOut' }}
          >
            <motion.span style={{ fontSize: current.ball * 0.32, fontWeight: 900, color: '#000' }} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.5 }}>
              8
            </motion.span>
          </motion.div>
          <div className="absolute inset-0 rounded-full" style={{ boxShadow: 'inset 0 -4px 12px rgba(0,0,0,0.4)', pointerEvents: 'none' }} />
        </motion.div>
      </div>

      {showText && <motion.h1 className={current.text} style={{ color: 'var(--gold-accent)', fontWeight: 700 }} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.4 }}>BilliardPro</motion.h1>}
    </div>
  );
}

export default AnimatedLogo;
