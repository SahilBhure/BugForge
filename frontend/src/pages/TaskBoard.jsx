import { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Plus, Filter, Users, UserPlus, Trash2, MessageSquare, Send } from 'lucide-react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import TaskCard from '../components/TaskCard';
import Modal from '../components/Modal';

const COLUMNS = [
  { key: 'OPEN',        label: 'Open',        color: 'var(--status-open)' },
  { key: 'IN_PROGRESS', label: 'In Progress',  color: 'var(--status-in-progress)' },
  { key: 'RESOLVED',    label: 'Resolved',     color: 'var(--status-resolved)' },
  { key: 'CLOSED',      label: 'Closed',       color: 'var(--status-closed)' },
];

const EMPTY_TASK = {
  title: '', description: '', type: 'BUG', status: 'OPEN', priority: 'MEDIUM', assigneeId: '',
};

export default function TaskBoard() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const { user: currentUser } = useAuth();
  const { addToast } = useToast();

  const [project, setProject] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterType, setFilterType] = useState('ALL');
  const [filterPriority, setFilterPriority] = useState('ALL');
  const [filterAssignee, setFilterAssignee] = useState('ALL');

  const [showModal, setShowModal] = useState(false);
  const [showMembersModal, setShowMembersModal] = useState(false);
  const [editTask, setEditTask] = useState(null);
  const [form, setForm] = useState(EMPTY_TASK);
  const [saving, setSaving] = useState(false);

  // New member invite state
  const [newMemberEmail, setNewMemberEmail] = useState('');
  const [newMemberRole, setNewMemberRole] = useState('DEVELOPER');
  const [addingMember, setAddingMember] = useState(false);

  // Comments state
  const [comments, setComments] = useState([]);
  const [loadingComments, setLoadingComments] = useState(false);
  const [newComment, setNewComment] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);

  // drag state
  const dragTask = useRef(null);
  const [dragOver, setDragOver] = useState(null);

  const fetchMembers = () => {
    api.get(`/projects/${projectId}/members`)
      .then((r) => setMembers(Array.isArray(r.data) ? r.data : []))
      .catch(console.error);
  };

  useEffect(() => {
    Promise.all([
      api.get(`/user/projects/${projectId}`),
      api.get(`/projects/${projectId}/tasks`),
      api.get(`/projects/${projectId}/members`),
    ])
      .then(([projRes, tasksRes, membersRes]) => {
        setProject(projRes.data);
        setTasks(Array.isArray(tasksRes.data) ? tasksRes.data : []);
        setMembers(Array.isArray(membersRes.data) ? membersRes.data : []);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [projectId]);

  const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  /* ── Drag & Drop ── */
  function onDragStart(task) { dragTask.current = task; }
  function onDragEnd()       { dragTask.current = null; setDragOver(null); }

  function onDropColumn(newStatus) {
    if (!dragTask.current) return;
    const task = dragTask.current;
    if (task.status === newStatus) return;

    const updated = {
      ...task,
      status: newStatus,
      project: { id: parseInt(projectId) },
      assignee: task.assignee?.id ? { id: task.assignee.id } : null,
    };
    // optimistic update
    setTasks((prev) => prev.map((t) => t.id === task.id ? { ...t, status: newStatus } : t));

    api.put(`/projects/tasks/${task.id}`, updated)
      .then(() => addToast(`Moved to ${newStatus.replace('_', ' ')}`))
      .catch(() => {
        addToast('Failed to update status.', 'error');
        setTasks((prev) => prev.map((t) => t.id === task.id ? task : t));
      });
    dragTask.current = null;
    setDragOver(null);
  }

  const fetchComments = async (taskId) => {
    setLoadingComments(true);
    try {
      const { data } = await api.get(`/tasks/${taskId}/comments`);
      setComments(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Failed to load comments', err);
      setComments([]);
    } finally {
      setLoadingComments(false);
    }
  };

  /* ── CRUD ── */
  function openCreate() {
    setEditTask(null);
    setForm(EMPTY_TASK);
    setComments([]);
    setNewComment('');
    setShowModal(true);
  }

  function openEdit(task) {
    setEditTask(task);
    setForm({
      title:       task.title ?? '',
      description: task.description ?? '',
      type:        task.type ?? 'BUG',
      status:      task.status ?? 'OPEN',
      priority:    task.priority ?? 'MEDIUM',
      assigneeId:  task.assignee?.id ? String(task.assignee.id) : '',
    });
    setNewComment('');
    fetchComments(task.id);
    setShowModal(true);
  }

  async function handleAddComment(e) {
    e.preventDefault();
    if (!newComment.trim() || !editTask) return;
    setSubmittingComment(true);
    try {
      const { data } = await api.post(`/tasks/${editTask.id}/comments`, {
        content: newComment.trim(),
      });
      setComments((prev) => [...prev, data]);
      setNewComment('');
      addToast('Comment posted! 💬');
    } catch (err) {
      addToast(err.response?.data || 'Failed to post comment.', 'error');
    } finally {
      setSubmittingComment(false);
    }
  }

  async function handleDeleteComment(commentId) {
    if (!confirm('Delete this comment?')) return;
    try {
      await api.delete(`/tasks/${editTask.id}/comments/${commentId}`);
      setComments((prev) => prev.filter((c) => c.id !== commentId));
      addToast('Comment deleted.');
    } catch (err) {
      addToast(err.response?.data || 'Failed to delete comment.', 'error');
    }
  }

  async function handleDelete(id) {
    if (!confirm('Delete this task?')) return;
    try {
      await api.delete(`/projects/tasks/${id}`);
      setTasks((t) => t.filter((x) => x.id !== id));
      addToast('Task deleted.');
    } catch {
      addToast('Failed to delete task.', 'error');
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);
    const payload = {
      ...form,
      project: { id: parseInt(projectId) },
      assignee: form.assigneeId ? { id: parseInt(form.assigneeId) } : null,
    };
    try {
      if (editTask) {
        await api.put(`/projects/tasks/${editTask.id}`, payload);
        const { data: tasksRes } = await api.get(`/projects/${projectId}/tasks`);
        setTasks(Array.isArray(tasksRes) ? tasksRes : []);
        addToast('Task updated!');
      } else {
        await api.post('/projects/tasks', payload);
        const { data: tasksRes } = await api.get(`/projects/${projectId}/tasks`);
        setTasks(Array.isArray(tasksRes) ? tasksRes : []);
        addToast('Task created! 🎉');
      }
      setShowModal(false);
    } catch (err) {
      addToast(err.response?.data?.message ?? 'Something went wrong.', 'error');
    } finally {
      setSaving(false);
    }
  }

  /* ── Members Management ── */
  async function handleAddMember(e) {
    e.preventDefault();
    if (!newMemberEmail.trim()) return;
    setAddingMember(true);
    try {
      await api.post(`/projects/${projectId}/members`, {
        email: newMemberEmail.trim(),
        role: newMemberRole,
      });
      addToast('Member added to project! 🎉');
      setNewMemberEmail('');
      fetchMembers();
    } catch (err) {
      addToast(err.response?.data || 'Failed to add member.', 'error');
    } finally {
      setAddingMember(false);
    }
  }

  async function handleRemoveMember(membershipId, memberName) {
    if (!confirm(`Remove ${memberName} from this project?`)) return;
    try {
      await api.delete(`/projects/${projectId}/members/${membershipId}`);
      addToast('Member removed.');
      fetchMembers();
    } catch (err) {
      addToast(err.response?.data || 'Failed to remove member.', 'error');
    }
  }

  /* ── Filter ── */
  const visible = tasks.filter((t) => {
    if (filterType !== 'ALL' && t.type !== filterType) return false;
    if (filterPriority !== 'ALL' && t.priority !== filterPriority) return false;
    if (filterAssignee === 'UNASSIGNED') {
      if (t.assignee) return false;
    } else if (filterAssignee === 'ME') {
      if (!t.assignee || t.assignee.mail !== currentUser?.mail) return false;
    } else if (filterAssignee !== 'ALL') {
      if (!t.assignee || String(t.assignee.id) !== String(filterAssignee)) return false;
    }
    return true;
  });

  if (loading) return <div className="spinner-wrap"><div className="spinner" /></div>;

  return (
    <div style={{ animation: 'fadeIn 0.4s ease' }}>
      {/* Header */}
      <div className="page-header" style={{ flexWrap: 'wrap', gap: 16 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <button className="btn btn-ghost btn-icon" onClick={() => navigate('/projects')} id="back-to-projects">
            <ArrowLeft size={18} />
          </button>
          <div>
            <h1 className="page-title">{project?.name ?? 'Project Board'}</h1>
            <p className="page-subtitle">{project?.description}</p>
          </div>
        </div>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap', alignItems: 'center' }}>
          {/* Members Button */}
          <button
            className="btn btn-ghost"
            onClick={() => setShowMembersModal(true)}
            id="manage-members-btn"
            style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '7px 14px' }}
            title="Manage project members"
          >
            <Users size={16} />
            <span>Members ({members.length})</span>
            {members.length > 0 && (
              <div style={{ display: 'flex', marginLeft: 4 }}>
                {members.slice(0, 3).map((m, idx) => (
                  <div
                    key={m.id}
                    style={{
                      width: 22,
                      height: 22,
                      borderRadius: '50%',
                      background: 'linear-gradient(135deg, var(--primary), var(--accent))',
                      color: '#fff',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: 9,
                      fontWeight: 700,
                      marginLeft: idx === 0 ? 0 : -6,
                      border: '2px solid var(--bg-dark)',
                    }}
                    title={`${m.user.username} (${m.role})`}
                  >
                    {m.user.username.slice(0, 2).toUpperCase()}
                  </div>
                ))}
              </div>
            )}
          </button>

          {/* Filters */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
            <Filter size={14} style={{ color: 'var(--text-muted)' }} />
            <select
              className="input-field"
              style={{ width: 'auto', padding: '7px 12px', fontSize: 13 }}
              value={filterAssignee}
              onChange={(e) => setFilterAssignee(e.target.value)}
              id="filter-assignee"
            >
              <option value="ALL">All Assignees</option>
              <option value="ME">Assigned to Me</option>
              <option value="UNASSIGNED">Unassigned</option>
              {members.map((m) => (
                <option key={m.id} value={m.user.id}>
                  👤 {m.user.username}
                </option>
              ))}
            </select>
            <select
              className="input-field"
              style={{ width: 'auto', padding: '7px 12px', fontSize: 13 }}
              value={filterType}
              onChange={(e) => setFilterType(e.target.value)}
              id="filter-type"
            >
              <option value="ALL">All Types</option>
              <option value="BUG">🐞 Bug</option>
              <option value="FEATURE">✨ Feature</option>
            </select>
            <select
              className="input-field"
              style={{ width: 'auto', padding: '7px 12px', fontSize: 13 }}
              value={filterPriority}
              onChange={(e) => setFilterPriority(e.target.value)}
              id="filter-priority"
            >
              <option value="ALL">All Priorities</option>
              <option value="CRITICAL">🔴 Critical</option>
              <option value="HIGH">🟠 High</option>
              <option value="MEDIUM">🟡 Medium</option>
              <option value="LOW">🟢 Low</option>
            </select>
          </div>
          <button className="btn btn-primary" onClick={openCreate} id="create-task-btn">
            <Plus size={16} /> New Task
          </button>
        </div>
      </div>

      {/* Kanban Board */}
      <div className="kanban-board">
        {COLUMNS.map((col) => {
          const colTasks = visible.filter((t) => t.status === col.key);
          return (
            <div
              key={col.key}
              className={`kanban-column${dragOver === col.key ? ' drag-over' : ''}`}
              onDragOver={(e) => { e.preventDefault(); setDragOver(col.key); }}
              onDragLeave={() => setDragOver(null)}
              onDrop={() => onDropColumn(col.key)}
              id={`kanban-col-${col.key}`}
            >
              <div className="kanban-col-header">
                <span className="kanban-col-title" style={{ color: col.color }}>
                  {col.label}
                </span>
                <span className="kanban-col-count">{colTasks.length}</span>
              </div>

              <div className="kanban-cards">
                {colTasks.length === 0 ? (
                  <div
                    style={{
                      border: '2px dashed var(--border)',
                      borderRadius: 'var(--radius-md)',
                      padding: '24px 12px',
                      textAlign: 'center',
                      color: 'var(--text-muted)',
                      fontSize: 13,
                    }}
                  >
                    Drop tasks here
                  </div>
                ) : (
                  colTasks.map((task) => (
                    <TaskCard
                      key={task.id}
                      task={task}
                      onEdit={openEdit}
                      onDelete={handleDelete}
                      onDragStart={onDragStart}
                      onDragEnd={onDragEnd}
                    />
                  ))
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Task Modal */}
      {showModal && (
        <Modal
          title={editTask ? 'Edit Task' : 'Create New Task'}
          onClose={() => setShowModal(false)}
        >
          <form className="modal-form" onSubmit={handleSubmit} id="task-form">
            <div className="input-group">
              <label className="input-label" htmlFor="task-title">Title *</label>
              <input
                id="task-title"
                className="input-field"
                type="text"
                name="title"
                placeholder="Short, descriptive title"
                value={form.title}
                onChange={handle}
                required
              />
            </div>

            <div className="input-group">
              <label className="input-label" htmlFor="task-desc">Description</label>
              <textarea
                id="task-desc"
                className="input-field"
                name="description"
                placeholder="Steps to reproduce, context, expected vs actual…"
                value={form.description}
                onChange={handle}
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
              <div className="input-group">
                <label className="input-label" htmlFor="task-type">Type</label>
                <select id="task-type" className="input-field" name="type" value={form.type} onChange={handle}>
                  <option value="BUG">🐞 Bug</option>
                  <option value="FEATURE">✨ Feature</option>
                </select>
              </div>

              <div className="input-group">
                <label className="input-label" htmlFor="task-priority">Priority</label>
                <select id="task-priority" className="input-field" name="priority" value={form.priority} onChange={handle}>
                  <option value="LOW">🟢 Low</option>
                  <option value="MEDIUM">🟡 Medium</option>
                  <option value="HIGH">🟠 High</option>
                  <option value="CRITICAL">🔴 Critical</option>
                </select>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
              <div className="input-group">
                <label className="input-label" htmlFor="task-status">Status</label>
                <select id="task-status" className="input-field" name="status" value={form.status} onChange={handle}>
                  <option value="OPEN">Open</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="RESOLVED">Resolved</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </div>

              <div className="input-group">
                <label className="input-label" htmlFor="task-assignee">Assignee</label>
                <select
                  id="task-assignee"
                  className="input-field"
                  name="assigneeId"
                  value={form.assigneeId}
                  onChange={handle}
                >
                  <option value="">Unassigned</option>
                  {members.map((m) => (
                    <option key={m.id} value={m.user.id}>
                      {m.user.username} ({m.role})
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="modal-actions">
              <button type="button" className="btn btn-ghost" onClick={() => setShowModal(false)}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary" disabled={saving} id="task-submit-btn">
                {saving ? 'Saving…' : editTask ? 'Save Changes' : 'Create Task'}
              </button>
            </div>
          </form>

          {/* Task Comments & Discussion Section */}
          {editTask && (
            <div style={{ marginTop: 24, paddingTop: 20, borderTop: '1px solid var(--border)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 16 }}>
                <MessageSquare size={17} style={{ color: 'var(--primary-light)' }} />
                <h3 style={{ fontSize: 15, fontWeight: 700, margin: 0 }}>
                  Activity & Comments ({comments.length})
                </h3>
              </div>

              {/* Comment Input */}
              <form onSubmit={handleAddComment} style={{ marginBottom: 20 }}>
                <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                  <textarea
                    className="input-field"
                    placeholder="Write a comment, update, or mention details..."
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    rows={2}
                    style={{ flex: 1, resize: 'vertical', minHeight: 64 }}
                    required
                  />
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={submittingComment || !newComment.trim()}
                    style={{ height: 42, padding: '0 16px', display: 'flex', alignItems: 'center', gap: 6, flexShrink: 0 }}
                  >
                    <Send size={14} /> {submittingComment ? 'Posting…' : 'Comment'}
                  </button>
                </div>
              </form>

              {/* Comment Thread */}
              {loadingComments ? (
                <div style={{ textAlign: 'center', padding: '16px 0', color: 'var(--text-muted)', fontSize: 13 }}>
                  Loading comments…
                </div>
              ) : comments.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '16px 0', color: 'var(--text-muted)', fontSize: 13 }}>
                  No comments yet. Start the conversation!
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 10, maxHeight: 280, overflowY: 'auto', paddingRight: 4 }}>
                  {comments.map((c) => {
                    const isAuthor = currentUser?.id === c.author?.id;
                    const dateStr = c.createdAt ? new Date(c.createdAt).toLocaleString(undefined, {
                      month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
                    }) : 'Just now';

                    return (
                      <div
                        key={c.id}
                        style={{
                          background: 'rgba(255, 255, 255, 0.03)',
                          border: '1px solid var(--border)',
                          borderRadius: 'var(--radius-md)',
                          padding: '12px 14px',
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 6 }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                            <div
                              style={{
                                width: 24,
                                height: 24,
                                borderRadius: '50%',
                                background: 'linear-gradient(135deg, var(--primary), var(--accent))',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                fontSize: 10,
                                fontWeight: 700,
                                color: '#fff',
                              }}
                            >
                              {c.author?.username?.slice(0, 2).toUpperCase() ?? 'U'}
                            </div>
                            <span style={{ fontWeight: 600, fontSize: 13 }}>{c.author?.username}</span>
                            <span style={{ fontSize: 11, color: 'var(--text-muted)' }}>• {dateStr}</span>
                          </div>

                          {isAuthor && (
                            <button
                              type="button"
                              className="task-action-btn delete"
                              onClick={() => handleDeleteComment(c.id)}
                              title="Delete comment"
                            >
                              <Trash2 size={13} />
                            </button>
                          )}
                        </div>
                        <p style={{ margin: 0, fontSize: 13, color: 'var(--text-secondary)', whiteSpace: 'pre-wrap', lineHeight: 1.5 }}>
                          {c.content}
                        </p>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}
        </Modal>
      )}

      {/* Members Modal */}
      {showMembersModal && (
        <Modal
          title={`Project Members (${members.length})`}
          onClose={() => setShowMembersModal(false)}
        >
          <div style={{ marginBottom: 24, paddingBottom: 20, borderBottom: '1px solid var(--border)' }}>
            <h4 style={{ fontSize: 13, textTransform: 'uppercase', letterSpacing: '0.05em', color: 'var(--text-muted)', marginBottom: 12 }}>
              Add / Invite Member
            </h4>
            <form onSubmit={handleAddMember} style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
              <input
                className="input-field"
                type="email"
                placeholder="colleague@example.com"
                value={newMemberEmail}
                onChange={(e) => setNewMemberEmail(e.target.value)}
                required
                style={{ flex: 1, minWidth: 200 }}
              />
              <select
                className="input-field"
                value={newMemberRole}
                onChange={(e) => setNewMemberRole(e.target.value)}
                style={{ width: 'auto' }}
              >
                <option value="DEVELOPER">Developer</option>
                <option value="TESTER">Tester</option>
                <option value="MANAGER">Manager</option>
                <option value="VIEWER">Viewer</option>
                <option value="ADMIN">Admin</option>
              </select>
              <button type="submit" className="btn btn-primary" disabled={addingMember}>
                <UserPlus size={15} /> {addingMember ? 'Adding…' : 'Add Member'}
              </button>
            </form>
          </div>

          <div>
            <h4 style={{ fontSize: 13, textTransform: 'uppercase', letterSpacing: '0.05em', color: 'var(--text-muted)', marginBottom: 12 }}>
              Current Team
            </h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10, maxHeight: 320, overflowY: 'auto' }}>
              {members.map((m) => {
                const isOwner = project?.owner?.id === m.user.id;
                return (
                  <div
                    key={m.id}
                    className="glass-card"
                    style={{
                      padding: '12px 16px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      gap: 12,
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                      <div
                        style={{
                          width: 34,
                          height: 34,
                          borderRadius: '50%',
                          background: 'linear-gradient(135deg, var(--primary), var(--accent))',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontWeight: 700,
                          fontSize: 13,
                          color: '#fff',
                        }}
                      >
                        {m.user.username.slice(0, 2).toUpperCase()}
                      </div>
                      <div>
                        <div style={{ fontWeight: 600, fontSize: 14 }}>
                          {m.user.username} {isOwner && <span style={{ fontSize: 11, color: 'var(--primary-light)', marginLeft: 4 }}>(Owner)</span>}
                        </div>
                        <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>{m.user.mail}</div>
                      </div>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                      <span className={`badge badge-${m.role?.toLowerCase()}`}>
                        {m.role}
                      </span>
                      {!isOwner && (
                        <button
                          className="task-action-btn delete"
                          onClick={() => handleRemoveMember(m.id, m.user.username)}
                          title="Remove member"
                        >
                          <Trash2 size={14} />
                        </button>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
