export function formatVND(value) {
  const n = Number(value || 0);
  try {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      maximumFractionDigits: 0,
    }).format(n);
  } catch (_) {
    // Fallback without Intl (very rare)
    return `${Math.round(n).toLocaleString('vi-VN')} ₫`;
  }
}

