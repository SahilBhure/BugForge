import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { UserPlus } from 'lucide-react';

export default function Register() {
  const { register } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();

  const [form, setForm] = useState({ username: '', mail: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  async function submit(e) {
    e.preventDefault();
    setError('');

    if (form.username.length < 5 || form.username.length > 12) {
      setError('Username must be 5–12 characters.');
      return;
    }

    setLoading(true);
    try {
      await register(form.username, form.mail, form.password);
      addToast('Account created! Please sign in.', 'success');
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message ?? 'Registration failed. Try again.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-bg" />
      <div className="auth-card">
        <div className="auth-logo">
          <div className="auth-logo-icon">🐞</div>
          <span className="auth-logo-name">BugForge</span>
        </div>

        <h1 className="auth-title">Create account</h1>
        <p className="auth-subtitle">Join BugForge and start tracking issues</p>

        {error && <div className="auth-error">{error}</div>}

        <form className="auth-form" onSubmit={submit} id="register-form">
          <div className="input-group">
            <label className="input-label" htmlFor="reg-username">Username</label>
            <input
              id="reg-username"
              className="input-field"
              type="text"
              name="username"
              placeholder="cooldev (5–12 chars)"
              value={form.username}
              onChange={handle}
              minLength={5}
              maxLength={12}
              required
            />
          </div>

          <div className="input-group">
            <label className="input-label" htmlFor="reg-email">Email address</label>
            <input
              id="reg-email"
              className="input-field"
              type="email"
              name="mail"
              placeholder="you@example.com"
              value={form.mail}
              onChange={handle}
              required
            />
          </div>

          <div className="input-group">
            <label className="input-label" htmlFor="reg-password">Password</label>
            <input
              id="reg-password"
              className="input-field"
              type="password"
              name="password"
              placeholder="Create a strong password"
              value={form.password}
              onChange={handle}
              required
            />
          </div>

          <button
            id="register-submit"
            type="submit"
            className="btn btn-primary auth-submit"
            disabled={loading}
          >
            {loading ? 'Creating account…' : <><UserPlus size={16} /> Create Account</>}
          </button>
        </form>

        <p className="auth-link">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
