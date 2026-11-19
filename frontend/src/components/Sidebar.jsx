import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, FolderKanban, User, LogOut, Bug } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/projects',  icon: FolderKanban,   label: 'Projects'  },
  { to: '/profile',   icon: User,            label: 'Profile'   },
];

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  const initials = user?.username
    ? user.username.slice(0, 2).toUpperCase()
    : '??';

  return (
    <aside className="sidebar">
      {/* Logo */}
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">🐞</div>
        <span className="sidebar-logo-text">BugForge</span>
      </div>

      {/* Nav */}
      <nav className="sidebar-nav">
        {navItems.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
          >
            <Icon size={18} />
            {label}
          </NavLink>
        ))}
      </nav>

      {/* Footer */}
      <div className="sidebar-footer">
        <div className="sidebar-user" onClick={() => navigate('/profile')}>
          <div className="user-avatar">{initials}</div>
          <div className="user-info">
            <div className="user-name">{user?.username ?? 'User'}</div>
            <div className="user-email">{user?.mail ?? ''}</div>
          </div>
        </div>
        <button
          className="nav-link btn-ghost"
          style={{ width: '100%', marginTop: 8, border: 'none' }}
          onClick={handleLogout}
          id="logout-btn"
        >
          <LogOut size={16} />
          Logout
        </button>
      </div>
    </aside>
  );
}
