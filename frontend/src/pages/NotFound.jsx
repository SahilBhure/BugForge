import { useNavigate } from 'react-router-dom';

export default function NotFound() {
  const navigate = useNavigate();
  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        gap: 16,
        background: 'var(--bg-base)',
        animation: 'fadeIn 0.4s ease',
      }}
    >
      <div style={{ fontSize: 80 }}>🐛</div>
      <h1 style={{ fontSize: 72, fontWeight: 900, color: 'var(--primary)', lineHeight: 1 }}>404</h1>
      <p style={{ fontSize: 20, fontWeight: 600, color: 'var(--text-primary)' }}>Page Not Found</p>
      <p style={{ fontSize: 14, color: 'var(--text-secondary)', maxWidth: 320 }}>
        Looks like this bug got away. The page you're looking for doesn't exist.
      </p>
      <button className="btn btn-primary" onClick={() => navigate('/dashboard')} id="go-home-btn" style={{ marginTop: 8 }}>
        Back to Dashboard
      </button>
    </div>
  );
}
