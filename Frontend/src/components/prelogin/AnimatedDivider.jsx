import React from 'react';

export default function AnimatedDivider({ className = '' }) {
  return (
    <div
      className={`h-px w-full ${className}`}
      style={{
        background: 'linear-gradient(90deg, transparent, var(--gold-accent), transparent)',
        transformOrigin: 'left',
        animation: 'bm-reveal 1.2s ease-out both'
      }}
    >
      <style>{`
        @keyframes bm-reveal { from { transform: scaleX(0); opacity: 0 } to { transform: scaleX(1); opacity: 1 } }
      `}</style>
    </div>
  );
}

