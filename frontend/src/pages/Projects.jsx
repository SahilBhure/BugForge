import { useEffect, useState } from 'react';
import { Plus } from 'lucide-react';
import api from '../api/axios';
import { useToast } from '../context/ToastContext';
import ProjectCard from '../components/ProjectCard';
import Modal from '../components/Modal';

const EMPTY_FORM = { name: '', description: '', startDate: '', endDate: '' };

export default function Projects() {
  const { addToast } = useToast();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editProject, setEditProject] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);

  const fetchProjects = () =>
    api.get('/user/projects')
      .then((r) => setProjects(r.data))
      .catch(console.error)
      .finally(() => setLoading(false));

  useEffect(() => { fetchProjects(); }, []);

  const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  function openCreate() {
    setEditProject(null);
    setForm(EMPTY_FORM);
    setShowModal(true);
  }

  function openEdit(project) {
    setEditProject(project);
    setForm({
      name: project.name ?? '',
      description: project.description ?? '',
      startDate: project.startDate ?? '',
      endDate: project.endDate ?? '',
    });
    setShowModal(true);
  }

  async function handleDelete(id) {
    if (!confirm('Delete this project?')) return;
    try {
      await api.delete(`/user/projects/${id}`);
      setProjects((p) => p.filter((x) => x.id !== id));
      addToast('Project deleted.');
    } catch {
      addToast('Failed to delete project.', 'error');
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);
    try {
      if (editProject) {
        await api.put(`/user/projects/${editProject.id}`, form);
        addToast('Project updated!');
      } else {
        await api.post('/user/projects', form);
        addToast('Project created! 🎉');
      }
      setShowModal(false);
      fetchProjects();
    } catch (err) {
      addToast(err.response?.data?.message ?? 'Something went wrong.', 'error');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div style={{ animation: 'fadeIn 0.4s ease' }}>
      <div className="page-header">
        <div>
          <h1 className="page-title">Projects</h1>
          <p className="page-subtitle">{projects.length} project{projects.length !== 1 ? 's' : ''} in your workspace</p>
        </div>
        <button className="btn btn-primary" onClick={openCreate} id="create-project-btn">
          <Plus size={16} /> New Project
        </button>
      </div>

      {loading ? (
        <div className="spinner-wrap"><div className="spinner" /></div>
      ) : projects.length === 0 ? (
        <div className="glass-card empty-state">
          <div className="empty-state-icon">📁</div>
          <div className="empty-state-text">No projects yet</div>
          <div className="empty-state-sub">Create your first project to start tracking bugs and features</div>
          <button className="btn btn-primary" onClick={openCreate} style={{ marginTop: 12 }} id="empty-create-btn">
            <Plus size={16} /> Create Project
          </button>
        </div>
      ) : (
        <div className="projects-grid">
          {projects.map((p) => (
            <ProjectCard
              key={p.id}
              project={p}
              onEdit={openEdit}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}

      {showModal && (
        <Modal
          title={editProject ? 'Edit Project' : 'Create New Project'}
          onClose={() => setShowModal(false)}
        >
          <form className="modal-form" onSubmit={handleSubmit} id="project-form">
            <div className="input-group">
              <label className="input-label" htmlFor="proj-name">Project Name *</label>
              <input
                id="proj-name"
                className="input-field"
                type="text"
                name="name"
                placeholder="e.g. Customer Portal v2"
                value={form.name}
                onChange={handle}
                required
              />
            </div>

            <div className="input-group">
              <label className="input-label" htmlFor="proj-desc">Description</label>
              <textarea
                id="proj-desc"
                className="input-field"
                name="description"
                placeholder="What is this project about?"
                value={form.description}
                onChange={handle}
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
              <div className="input-group">
                <label className="input-label" htmlFor="proj-start">Start Date</label>
                <input
                  id="proj-start"
                  className="input-field"
                  type="date"
                  name="startDate"
                  value={form.startDate}
                  onChange={handle}
                />
              </div>
              <div className="input-group">
                <label className="input-label" htmlFor="proj-end">End Date</label>
                <input
                  id="proj-end"
                  className="input-field"
                  type="date"
                  name="endDate"
                  value={form.endDate}
                  onChange={handle}
                />
              </div>
            </div>

            <div className="modal-actions">
              <button type="button" className="btn btn-ghost" onClick={() => setShowModal(false)}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary" disabled={saving} id="project-submit-btn">
                {saving ? 'Saving…' : editProject ? 'Save Changes' : 'Create Project'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
