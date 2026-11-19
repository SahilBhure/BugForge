import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, Mail, Save, Trash2, LogOut } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../api/axios';

export default function Profile() {
  const { user, refreshUser, logout } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();

  const [form, setForm] = useState({ username: '', mail: '' });
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    if (user) {
      setForm({ username: user.username ?? '', mail: user.mail ?? '' });
    }
  }, [user]);

  const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    try {
      await api.put('/user', form);
      await refreshUser();
      addToast('Profile updated!');
    } catch (err) {
      addToast(err.response?.data?.message ?? 'Failed to update profile.', 'error');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!confirm('This will permanently delete your account and all your data. Are you sure?')) return;
    setDeleting(true);
    try {
      await api.delete('/user');
      logout();
      navigate('/login');
      addToast('Account deleted.');
    } catch {
      addToast('Failed to delete account.', 'error');
    } finally {
      setDeleting(false);
    }
  }

  const initials = user?.username ? user.username.slice(0, 2).toUpperCase() : '??';

  return (
    <div style={{ animation: 'fadeIn 0.4s ease', maxWidth: 580, margin: '0 auto' }}>
      <div className="page-header">
        <div>
          <h1 className="page-title">Profile</h1>
          <p className="page-subtitle">Manage your account settings</p>
        </div>
      </div>

      {/* Avatar card */}
      <div
        className="glass-card"
        style={{
          padding: '32px',
          marginBottom: 24,
          display: 'flex',
          alignItems: 'center',
          gap: 20,
          background: 'linear-gradient(135deg, rgba(108,99,255,0.1), rgba(255,101,132,0.07))',
        }}
      >
        <div
          style={{
            width: 72, height: 72, borderRadius: '50%',
            background: 'linear-gradient(135deg, var(--primary), var(--accent))',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 28, fontWeight: 800, color: '#fff',
            boxShadow: '0 4px 20px var(--primary-glow)',
            flexShrink: 0,
          }}
        >
          {initials}
        </div>
        <div>
          <div style={{ fontSize: 22, fontWeight: 700 }}>{user?.username}</div>
          <div style={{ fontSize: 14, color: 'var(--text-secondary)', marginTop: 4 }}>{user?.mail}</div>
          <div
            style={{
              display: 'inline-block',
              marginTop: 8,
              fontSize: 11,
              fontWeight: 600,
              background: 'rgba(108,99,255,0.2)',
              color: 'var(--primary-light)',
              borderRadius: 99,
              padding: '3px 10px',
            }}
          >
            {user?.roles?.join(', ') || 'USER'}
          </div>
        </div>
      </div>

      {/* Edit form */}
      <div className="glass-card" style={{ padding: 32, marginBottom: 24 }}>
        <h2 style={{ fontSize: 16, fontWeight: 700, marginBottom: 20, display: 'flex', alignItems: 'center', gap: 8 }}>
          <User size={16} style={{ color: 'var(--primary)' }} /> Account Details
        </h2>
        <form className="modal-form" onSubmit={handleSave} id="profile-form">
          <div className="input-group">
            <label className="input-label" htmlFor="profile-username">Username</label>
            <input
              id="profile-username"
              className="input-field"
              type="text"
              name="username"
              value={form.username}
              onChange={handle}
              minLength={5}
              maxLength={12}
              required
            />
          </div>
          <div className="input-group">
            <label className="input-label" htmlFor="profile-email">
              <span style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                <Mail size={13} /> Email address
              </span>
            </label>
            <input
              id="profile-email"
              className="input-field"
              type="email"
              name="mail"
              value={form.mail}
              onChange={handle}
              required
            />
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={saving}
              id="profile-save-btn"
            >
              <Save size={15} /> {saving ? 'Saving…' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>

      {/* Danger zone */}
      <div
        className="glass-card"
        style={{
          padding: 32,
          border: '1px solid rgba(255,101,132,0.25)',
          background: 'rgba(255,101,132,0.04)',
        }}
      >
        <h2 style={{ fontSize: 16, fontWeight: 700, marginBottom: 8, color: 'var(--accent-light)' }}>
          ⚠️ Danger Zone
        </h2>
        <p style={{ fontSize: 13, color: 'var(--text-secondary)', marginBottom: 20 }}>
          Permanently delete your account and all associated data. This action cannot be undone.
        </p>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <button
            className="btn btn-danger"
            onClick={handleDelete}
            disabled={deleting}
            id="delete-account-btn"
          >
            <Trash2 size={15} /> {deleting ? 'Deleting…' : 'Delete Account'}
          </button>
          <button
            className="btn btn-ghost"
            onClick={() => { logout(); navigate('/login'); }}
            id="profile-logout-btn"
          >
            <LogOut size={15} /> Logout
          </button>
        </div>
      </div>
    </div>
  );
}
