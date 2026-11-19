import { Calendar, Pencil, Trash2, ArrowRight, Users } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function ProjectCard({ project, onEdit, onDelete }) {
  const navigate = useNavigate();
  const memberCount = project.memberships?.length || 1;

  return (
    <div className="glass-card project-card" id={`project-card-${project.id}`}>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: 10 }}>
        <div className="project-card-name">{project.name}</div>
        <span
          className="badge"
          style={{ background: 'rgba(108,99,255,0.15)', color: 'var(--primary-light)', flexShrink: 0 }}
          title={`${memberCount} member(s)`}
        >
          <Users size={12} /> {memberCount}
        </span>
      </div>
      <p className="project-card-desc">
        {project.description || 'No description provided.'}
      </p>

      <div className="project-card-meta">
        <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          <Calendar size={12} />
          {project.startDate ?? 'No start'}
        </span>
        {project.endDate && (
          <span>→ {project.endDate}</span>
        )}
        {project.owner && (
          <span style={{ marginLeft: 'auto', fontSize: 11, color: 'var(--text-muted)' }}>
            Owner: {project.owner.username}
          </span>
        )}
      </div>

      <div className="project-card-actions">
        <button
          className="btn btn-primary btn-sm"
          onClick={() => navigate(`/projects/${project.id}/tasks`)}
          id={`open-board-${project.id}`}
        >
          <ArrowRight size={14} /> Open Board
        </button>
        <button
          className="btn btn-ghost btn-sm"
          onClick={() => onEdit(project)}
          id={`edit-project-${project.id}`}
        >
          <Pencil size={14} /> Edit
        </button>
        <button
          className="btn btn-danger btn-sm"
          onClick={() => onDelete(project.id)}
          id={`delete-project-${project.id}`}
        >
          <Trash2 size={14} />
        </button>
      </div>
    </div>
  );
}
