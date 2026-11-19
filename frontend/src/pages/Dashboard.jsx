import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FolderKanban, Bug, CheckCircle, Clock, ArrowRight, Plus, CheckSquare } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

const STATUS_LABELS = {
  OPEN: 'Open',
  IN_PROGRESS: 'In Progress',
  RESOLVED: 'Resolved',
  CLOSED: 'Closed',
};

const STATUS_CLASSES = {
  OPEN: 'status-open',
  IN_PROGRESS: 'status-progress',
  RESOLVED: 'status-resolved',
  CLOSED: 'status-closed',
};

export default function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDashboardData() {
      try {
        const { data: projectList } = await api.get('/user/projects');
        const projs = Array.isArray(projectList) ? projectList : [];
        setProjects(projs);

        // Fetch tasks for each project
        const taskPromises = projs.map(async (p) => {
          try {
            const { data: pTasks } = await api.get(`/projects/${p.id}/tasks`);
            return (Array.isArray(pTasks) ? pTasks : []).map((t) => ({
              ...t,
              projectName: p.name,
              projectId: p.id,
            }));
          } catch {
            return [];
          }
        });

        const allTaskArrays = await Promise.all(taskPromises);
        const combinedTasks = allTaskArrays.flat();
        setTasks(combinedTasks);
      } catch (err) {
        console.error('Error loading dashboard data:', err);
      } finally {
        setLoading(false);
      }
    }

    loadDashboardData();
  }, []);

  const hour = new Date().getHours();
  const greeting = hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening';

  const openBugs = tasks.filter((t) => t.type === 'BUG' && t.status === 'OPEN').length;
  const inProgress = tasks.filter((t) => t.status === 'IN_PROGRESS').length;
  const resolved = tasks.filter((t) => t.status === 'RESOLVED' || t.status === 'CLOSED').length;

  return (
    <div style={{ animation: 'fadeIn 0.4s ease' }}>
      {/* Hero greeting */}
      <div
        className="glass-card"
        style={{
          padding: '32px 36px',
          marginBottom: 32,
          background: 'linear-gradient(135deg, rgba(108,99,255,0.12), rgba(255,101,132,0.08))',
          border: '1px solid rgba(108,99,255,0.2)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: 20,
        }}
      >
        <div>
          <div style={{ fontSize: 13, color: 'var(--text-secondary)', marginBottom: 6 }}>
            {greeting} 👋
          </div>
          <h1 style={{ fontSize: 30, fontWeight: 800, marginBottom: 6 }}>
            {user?.username ?? 'Developer'}
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: 14 }}>
            Here's what's happening in your workspace today.
          </p>
        </div>
        <div style={{ fontSize: 72, opacity: 0.6, flexShrink: 0 }}>🐞</div>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <StatCard
          icon={<FolderKanban size={22} />}
          value={loading ? '…' : projects.length}
          label="Total Projects"
          color="var(--primary)"
        />
        <StatCard
          icon={<Bug size={22} />}
          value={loading ? '…' : openBugs}
          label="Open Bugs"
          color="var(--accent)"
        />
        <StatCard
          icon={<Clock size={22} />}
          value={loading ? '…' : inProgress}
          label="In Progress"
          color="var(--warning)"
        />
        <StatCard
          icon={<CheckCircle size={22} />}
          value={loading ? '…' : resolved}
          label="Resolved / Closed"
          color="var(--success)"
        />
      </div>

      {/* Recent Projects */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <div>
          <h2 style={{ fontSize: 18, fontWeight: 700 }}>Recent Projects</h2>
          <p style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Jump back in</p>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <button className="btn btn-ghost btn-sm" onClick={() => navigate('/projects')} id="view-all-projects">
            View All <ArrowRight size={14} />
          </button>
          <button className="btn btn-primary btn-sm" onClick={() => navigate('/projects')} id="dashboard-new-project">
            <Plus size={14} /> New Project
          </button>
        </div>
      </div>

      {loading ? (
        <div className="spinner-wrap"><div className="spinner" /></div>
      ) : projects.length === 0 ? (
        <div className="glass-card empty-state" style={{ marginBottom: 32 }}>
          <div className="empty-state-icon">📂</div>
          <div className="empty-state-text">No projects yet</div>
          <div className="empty-state-sub">Create your first project to get started</div>
          <button className="btn btn-primary" onClick={() => navigate('/projects')} style={{ marginTop: 12 }}>
            <Plus size={16} /> Create Project
          </button>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: 16, marginBottom: 36 }}>
          {projects.slice(0, 6).map((p) => (
            <div
              key={p.id}
              className="glass-card"
              style={{ padding: '20px 24px', cursor: 'pointer', position: 'relative', overflow: 'hidden' }}
              onClick={() => navigate(`/projects/${p.id}/tasks`)}
              id={`dashboard-project-${p.id}`}
            >
              <div
                style={{
                  position: 'absolute', top: 0, left: 0, right: 0, height: 3,
                  background: 'linear-gradient(90deg, var(--primary), var(--accent))',
                }}
              />
              <div style={{ fontWeight: 700, marginBottom: 6 }}>{p.name}</div>
              <div style={{ fontSize: 12, color: 'var(--text-secondary)', marginBottom: 12 }}>
                {p.description?.slice(0, 70) ?? 'No description'}
                {p.description?.length > 70 ? '…' : ''}
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 12, color: 'var(--primary-light)' }}>
                <ArrowRight size={13} /> Open Board
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Recent Tasks */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <div>
          <h2 style={{ fontSize: 18, fontWeight: 700 }}>Recent Tasks</h2>
          <p style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Latest activity across all your projects</p>
        </div>
      </div>

      {loading ? (
        <div className="spinner-wrap"><div className="spinner" /></div>
      ) : tasks.length === 0 ? (
        <div className="glass-card empty-state">
          <div className="empty-state-icon">📝</div>
          <div className="empty-state-text">No tasks created yet</div>
          <div className="empty-state-sub">Open any project board above to add tasks</div>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {tasks.slice(0, 6).map((task) => (
            <div
              key={task.id}
              className="glass-card"
              style={{
                padding: '14px 20px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                cursor: 'pointer',
                transition: 'transform 0.15s ease, border-color 0.15s ease',
              }}
              onClick={() => navigate(`/projects/${task.projectId}/tasks`)}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
                <span className={`badge badge-${task.type?.toLowerCase()}`}>
                  {task.type}
                </span>
                <div>
                  <div style={{ fontWeight: 600, fontSize: 14 }}>{task.title}</div>
                  <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>
                    {task.projectName} {task.description ? `• ${task.description.slice(0, 50)}...` : ''}
                  </div>
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <span className={`badge badge-${task.priority?.toLowerCase()}`}>
                  {task.priority}
                </span>
                <span className={`badge ${STATUS_CLASSES[task.status] ?? 'status-open'}`}>
                  {STATUS_LABELS[task.status] ?? task.status}
                </span>
                <ArrowRight size={14} style={{ color: 'var(--text-secondary)', marginLeft: 6 }} />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function StatCard({ icon, value, label, color }) {
  return (
    <div className="glass-card stat-card">
      <div className="stat-icon" style={{ background: `${color}22` }}>
        <span style={{ color }}>{icon}</span>
      </div>
      <div className="stat-value" style={{ color }}>{value}</div>
      <div className="stat-label">{label}</div>
    </div>
  );
}
