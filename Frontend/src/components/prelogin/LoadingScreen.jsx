import { motion, AnimatePresence } from 'motion/react';
import { useState, useEffect } from 'react';

export default function LoadingScreen({ message = 'Đang tải...', isVisible }) {
  const [progress, setProgress] = useState(0);
  const [phase, setPhase] = useState('loading'); // 'loading' | 'falling' | 'reveal' | 'exit'

  useEffect(() => {
    if (isVisible) {
      setProgress(0);
      setPhase('loading');

      const interval = setInterval(() => {
        setProgress((prev) => {
          if (prev >= 100) {
            clearInterval(interval);
            setPhase('falling');
            setTimeout(() => {
              setPhase('reveal');
              setTimeout(() => setPhase('exit'), 1500);
            }, 2000);
            return 100;
          }
          return prev + 2;
        });
      }, 30);

      return () => clearInterval(interval);
    }
  }, [isVisible]);

  const ballPosition = Math.min(progress, 100);
  const isComplete = progress >= 100;

  const cueStickPosition =
    progress < 5 ? -200 : progress < 10 ? -50 : -300;

  return (
    <AnimatePresence>
      {isVisible && (
        <motion.div
          className="fixed inset-0 z-50 flex items-center justify-center overflow-hidden"
          initial={{ opacity: 0 }}
          animate={{
            opacity: phase === 'exit' ? 0 : 1,
            backgroundColor:
              phase === 'falling'
                ? 'rgba(0, 0, 0, 1)'
                : phase === 'reveal'
                ? 'rgba(10, 14, 20, 0.98)'
                : phase === 'exit'
                ? 'rgba(10, 14, 20, 0)'
                : 'rgba(10, 14, 20, 0.98)',
          }}
          exit={{ opacity: 0 }}
          transition={{
            opacity: { duration: phase === 'exit' ? 0.6 : 0.3 },
            backgroundColor: {
              duration: phase === 'reveal' ? 1.5 : phase === 'exit' ? 0.6 : 0.3,
            },
          }}
        >
          {/* Billiard texture background - fades in during reveal */}
          <motion.div
            className="absolute inset-0 billiard-texture"
            initial={{ opacity: 0 }}
            animate={{ opacity: phase === 'reveal' ? 1 : phase === 'exit' ? 0 : 0 }}
            transition={{ duration: phase === 'reveal' ? 1.5 : 0.6 }}
          />

          <div className="relative w-full max-w-2xl px-8">
            <div className="text-center">
              <AnimatePresence mode="wait">
                {phase === 'loading' && (
                  <motion.div
                    key="loading-phase"
                    exit={{ opacity: 0 }}
                    transition={{ duration: 0.3 }}
                  >
                    {/* Logo text at top */}
                    <motion.div
                      initial={{ scale: 0.8, opacity: 0 }}
                      animate={{ scale: 1, opacity: 1 }}
                      transition={{ delay: 0.2, duration: 0.5 }}
                      className="mb-16"
                    >
                      <h2
                        className="text-5xl mb-2"
                        style={{ color: 'var(--gold-accent)', fontWeight: 700 }}
                      >
                        BilliardPro
                      </h2>
                    </motion.div>

                    {/* Cue Stick and Ball Animation Area */}
                    <div className="relative w-full h-24 mb-8">
                      {/* Subtle track line */}
                      <div
                        className="absolute top-1/2 left-0 right-0 h-0.5 opacity-10"
                        style={{
                          background:
                            'linear-gradient(90deg, transparent 0%, var(--gold-accent) 15%, var(--gold-accent) 85%, transparent 100%)',
                          transform: 'translateY(-50%)',
                        }}
                      />

                      {/* Pocket/Hole at the end */}
                      <motion.div
                        className="absolute rounded-full"
                        style={{
                          width: 56,
                          height: 56,
                          right: 0,
                          top: '50%',
                          marginTop: -28,
                          background:
                            'radial-gradient(circle, #000000 0%, #1a1a1a 60%, #2a2a2a 100%)',
                          boxShadow:
                            'inset 0 4px 16px rgba(0,0,0,0.95), 0 0 25px rgba(241, 194, 50, 0.25)',
                          border: '3px solid #1a1a1a',
                          zIndex: 1,
                        }}
                        animate={{
                          boxShadow: isComplete
                            ? [
                                'inset 0 4px 16px rgba(0,0,0,0.95), 0 0 25px rgba(241, 194, 50, 0.25)',
                                'inset 0 4px 16px rgba(0,0,0,0.95), 0 0 60px rgba(241, 194, 50, 0.9)',
                                'inset 0 4px 16px rgba(0,0,0,0.95), 0 0 25px rgba(241, 194, 50, 0.25)',
                              ]
                            : 'inset 0 4px 16px rgba(0,0,0,0.95), 0 0 25px rgba(241, 194, 50, 0.25)',
                        }}
                        transition={{
                          duration: 0.4,
                          repeat: isComplete ? Infinity : 0,
                        }}
                      >
                        <div
                          className="absolute inset-3 rounded-full"
                          style={{
                            background:
                              'radial-gradient(circle, #000000 0%, #0a0a0a 100%)',
                            boxShadow: 'inset 0 2px 10px rgba(0,0,0,1)',
                          }}
                        />
                      </motion.div>

                      {/* Cue Stick */}
                      <motion.div
                        className="absolute"
                        style={{
                          width: 200,
                          height: 10,
                          left: cueStickPosition,
                          top: '50%',
                          marginTop: -5,
                          background:
                            'linear-gradient(90deg, #654321 0%, #8B4513 20%, #D2691E 50%, #F4A460 60%, #8B4513 80%, #654321 100%)',
                          borderRadius: '5px',
                          boxShadow: '0 2px 8px rgba(0,0,0,0.5)',
                          zIndex: progress < 10 ? 3 : 0,
                          opacity: progress > 10 ? 0 : 1,
                        }}
                        animate={{ left: cueStickPosition }}
                        transition={{
                          duration: progress < 5 ? 1 : 0.15,
                          ease: progress < 10 ? 'easeOut' : 'easeIn',
                        }}
                      >
                        <div
                          className="absolute"
                          style={{
                            width: 12,
                            height: 12,
                            right: -6,
                            top: -1,
                            background:
                              'radial-gradient(circle, #4169E1, #1E3A8A)',
                            borderRadius: '50%',
                            border: '1px solid #0F172A',
                          }}
                        />
                      </motion.div>

                      {/* 8-Ball rolling */}
                      <motion.div
                        className="absolute rounded-full"
                        style={{
                          width: 48,
                          height: 48,
                          left: `${Math.max(5, ballPosition)}%`,
                          top: '50%',
                          marginTop: -24,
                          background:
                            'radial-gradient(circle at 35% 35%, #2a2a2a, #000000)',
                          boxShadow:
                            '0 6px 16px rgba(0,0,0,0.7), inset 0 2px 4px rgba(255,255,255,0.15), 0 0 20px rgba(241, 194, 50, 0.2)',
                          border: '2px solid rgba(0,0,0,0.4)',
                          zIndex: 2,
                          transform: 'translateX(-50%) translateY(-50%)',
                        }}
                        animate={{
                          left: `${Math.min(ballPosition, 97)}%`,
                          rotate: ballPosition * 7.2,
                        }}
                        transition={{
                          left: { duration: 0.05, ease: 'linear' },
                          rotate: { duration: 0.05, ease: 'linear' },
                        }}
                      >
                        <div
                          className="absolute rounded-full"
                          style={{
                            width: 12,
                            height: 12,
                            top: '20%',
                            left: '25%',
                            background:
                              'radial-gradient(circle, rgba(255,255,255,0.4) 0%, rgba(255,255,255,0) 100%)',
                          }}
                        />

                        <div
                          className="absolute rounded-full flex items-center justify-center"
                          style={{
                            width: 26,
                            height: 26,
                            top: '50%',
                            left: '50%',
                            transform: 'translate(-50%, -50%)',
                            backgroundColor: '#ffffff',
                            boxShadow:
                              '0 1px 3px rgba(0,0,0,0.3), inset 0 1px 2px rgba(0,0,0,0.1)',
                          }}
                        >
                          <span
                            style={{
                              fontSize: 15,
                              fontWeight: 900,
                              color: '#000000',
                              fontFamily: 'system-ui, -apple-system, sans-serif',
                            }}
                          >
                            8
                          </span>
                        </div>

                        <div
                          className="absolute inset-0 rounded-full"
                          style={{
                            boxShadow: 'inset 0 -3px 8px rgba(0,0,0,0.4)',
                            pointerEvents: 'none',
                          }}
                        />
                      </motion.div>
                    </div>

                    <motion.div
                      animate={{ opacity: [0.5, 1, 0.5] }}
                      transition={{ duration: 1.5, repeat: Infinity }}
                      className="text-center"
                    >
                      <p className="text-sm text-muted-foreground">
                        {message} {Math.round(progress)}%
                      </p>
                    </motion.div>
                  </motion.div>
                )}

                {phase === 'falling' && (
                  /* Falling into hole phase - 2s */
                  <motion.div
                    key="falling-phase"
                    className="flex items-center justify-center"
                    style={{ minHeight: '400px' }}
                  >
                    <motion.div
                      className="relative rounded-full"
                      style={{
                        width: 120,
                        height: 120,
                        background:
                          'radial-gradient(circle at 35% 35%, #2a2a2a, #000000)',
                        border: '3px solid rgba(0,0,0,0.3)',
                      }}
                      initial={{ y: -300, scale: 0.3, opacity: 0, rotate: 0 }}
                      animate={{
                        y: 0,
                        scale: 1,
                        opacity: 1,
                        rotate: 1080,
                        boxShadow: [
                          '0 0 40px rgba(241, 194, 50, 0.4)',
                          '0 0 100px rgba(241, 194, 50, 1), 0 0 140px rgba(241, 194, 50, 0.7)',
                          '0 0 40px rgba(241, 194, 50, 0.4)',
                        ],
                      }}
                      transition={{
                        duration: 2,
                        y: { duration: 2, ease: [0.34, 1.56, 0.64, 1] },
                        scale: { duration: 2, ease: 'easeOut' },
                        opacity: { duration: 0.3 },
                        rotate: { duration: 2, ease: 'linear' },
                        boxShadow: { duration: 2, repeat: Infinity },
                      }}
                    >
                      <div
                        className="absolute rounded-full"
                        style={{
                          width: 30,
                          height: 30,
                          top: '20%',
                          left: '25%',
                          background:
                            'radial-gradient(circle, rgba(255,255,255,0.5) 0%, transparent 70%)',
                        }}
                      />
                      <div
                        className="absolute rounded-full flex items-center justify-center"
                        style={{
                          width: 66,
                          height: 66,
                          top: '50%',
                          left: '50%',
                          transform: 'translate(-50%, -50%)',
                          backgroundColor: '#ffffff',
                          boxShadow:
                            '0 3px 10px rgba(0,0,0,0.4), inset 0 2px 4px rgba(0,0,0,0.1)',
                        }}
                      >
                        <span
                          style={{
                            fontSize: 38,
                            fontWeight: 900,
                            color: '#000000',
                            fontFamily: 'system-ui, -apple-system, sans-serif',
                          }}
                        >
                          8
                        </span>
                      </div>
                      <div
                        className="absolute inset-0 rounded-full"
                        style={{
                          boxShadow: 'inset 0 -6px 16px rgba(0,0,0,0.5)',
                          pointerEvents: 'none',
                        }}
                      />
                    </motion.div>
                  </motion.div>
                )}

                {(phase === 'reveal' || phase === 'exit') && (
                  /* Reveal phase */
                  <motion.div
                    key="reveal-phase"
                    className="flex flex-col items-center justify-center relative"
                    style={{ minHeight: '400px' }}
                    initial={{ opacity: 0 }}
                    animate={{ opacity: phase === 'exit' ? 0 : 1 }}
                    transition={{ duration: phase === 'exit' ? 0.4 : 0.6 }}
                  >
                    {/* 8-Ball */}
                    <motion.div
                      className="relative rounded-full"
                      style={{
                        width: 120,
                        height: 120,
                        background:
                          'radial-gradient(circle at 35% 35%, #2a2a2a, #000000)',
                        boxShadow:
                          '0 10px 40px rgba(0,0,0,0.8), inset 0 4px 8px rgba(255,255,255,0.2), 0 0 60px rgba(241, 194, 50, 0.4)',
                        border: '3px solid rgba(0,0,0,0.3)',
                      }}
                      initial={{ scale: 1, y: 0, x: 0 }}
                      animate={
                        phase === 'exit'
                          ? {
                              scale: 0.25,
                              y: -window.innerHeight / 2 + 60,
                              x: -window.innerWidth / 2 + 100,
                              transition: {
                                duration: 0.8,
                                ease: [0.34, 1.56, 0.64, 1],
                              },
                            }
                          : {
                              scale: [1, 1.05, 1],
                              transition: { duration: 2, repeat: Infinity },
                            }
                      }
                    >
                      <div
                        className="absolute rounded-full"
                        style={{
                          width: 30,
                          height: 30,
                          top: '20%',
                          left: '25%',
                          background:
                            'radial-gradient(circle, rgba(255,255,255,0.4) 0%, transparent 70%)',
                        }}
                      />
                      <div
                        className="absolute rounded-full flex items-center justify-center"
                        style={{
                          width: 66,
                          height: 66,
                          top: '50%',
                          left: '50%',
                          transform: 'translate(-50%, -50%)',
                          backgroundColor: '#ffffff',
                          boxShadow:
                            '0 3px 10px rgba(0,0,0,0.4), inset 0 2px 4px rgba(0,0,0,0.1)',
                        }}
                      >
                        <span
                          style={{
                            fontSize: 38,
                            fontWeight: 900,
                            color: '#000000',
                            fontFamily: 'system-ui, -apple-system, sans-serif',
                          }}
                        >
                          8
                        </span>
                      </div>
                      <div
                        className="absolute inset-0 rounded-full"
                        style={{
                          boxShadow: 'inset 0 -6px 16px rgba(0,0,0,0.5)',
                          pointerEvents: 'none',
                        }}
                      />
                    </motion.div>

                    <motion.h2
                      initial={{ opacity: 0, y: 30 }}
                      animate={{
                        opacity: phase === 'exit' ? 0 : 1,
                        y: phase === 'exit' ? -20 : 0,
                      }}
                      transition={{
                        delay: phase === 'exit' ? 0 : 0.3,
                        duration: 0.6,
                        ease: 'easeOut',
                      }}
                      className="text-5xl mt-8 mb-3"
                      style={{ color: 'var(--gold-accent)', fontWeight: 700 }}
                    >
                      BilliardPro
                    </motion.h2>

                    <motion.p
                      initial={{ opacity: 0, y: 30 }}
                      animate={{
                        opacity: phase === 'exit' ? 0 : 1,
                        y: phase === 'exit' ? -20 : 0,
                      }}
                      transition={{
                        delay: phase === 'exit' ? 0 : 0.6,
                        duration: 0.8,
                        ease: 'easeOut',
                      }}
                      className="text-xl text-muted-foreground italic"
                    >
                      "Quản lý thông minh - Kinh doanh hiệu quả"
                    </motion.p>

                    {/* Glowing particles */}
                    {phase === 'reveal' &&
                      Array.from({ length: 16 }).map((_, i) => (
                        <motion.div
                          key={i}
                          className="absolute rounded-full"
                          style={{
                            width: 8,
                            height: 8,
                            background: 'var(--gold-accent)',
                            boxShadow: '0 0 16px var(--gold-accent)',
                          }}
                          initial={{ scale: 0, x: 0, y: -60 }}
                          animate={{
                            scale: [0, 1.5, 0],
                            x: Math.cos((i * 22.5 * Math.PI) / 180) * 140,
                            y: -60 + Math.sin((i * 22.5 * Math.PI) / 180) * 140,
                            opacity: [0, 1, 0],
                          }}
                          transition={{
                            duration: 1.8,
                            delay: 0.8 + i * 0.04,
                            ease: 'easeOut',
                          }}
                        />
                      ))}
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
