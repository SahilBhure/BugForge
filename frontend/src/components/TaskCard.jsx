import { Pencil, Trash2, MessageSquare } from 'lucide-react';

const priorityClass = { LOW: 'low', MEDIUM: 'medium', HIGH: 'high', CRITICAL: 'critical' };
const typeClass     = { BUG: 'bug', FEATURE: 'feature' };
const typeEmoji     = { BUG: '🐞', FEATURE: '✨' };

export default function TaskCard({ task, onEdit, onDelete, onDragStart, onDragEnd }) {
  return (
    <div
      className="task-card"
      draggable
      onDragStart={(e) => { e.dataTransfer.effectAllowed = 'move'; onDragStart(task); }}
      onDragEnd={onDragEnd}
      onClick={() => onEdit(task)}
      style={{ cursor: 'pointer' }}
      id={`task-card-${task.id}`}
    >
      <div className="task-card-top">
        <span className="task-card-title">{task.title}</span>
      </div>

      {task.description && (
        <p className="task-card-desc">{task.description}</p>
      )}

      <div className="task-card-badges">
        <span className={`badge badge-${typeClass[task.type] ?? 'bug'}`}>
          {typeEmoji[task.type]} {task.type}
        </span>
        {task.priority && (
          <span className={`badge badge-${priorityClass[task.priority] ?? 'medium'}`}>
            {task.priority}
          </span>
        )}
      </div>

      <div className="task-card-footer">
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span className="task-card-id">#{task.id}</span>
          {task.assignee ? (
            <div
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: 5,
                background: 'rgba(255,255,255,0.06)',
                padding: '2px 8px 2px 4px',
                borderRadius: '12px',
                fontSize: 11,
                color: 'var(--text-secondary)',
              }}
              title={`Assigned to ${task.assignee.username} (${task.assignee.mail})`}
            >
              <div
                style={{
                  width: 18,
                  height: 18,
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, var(--primary), var(--accent))',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: 9,
                  fontWeight: 700,
                  color: '#fff',
                }}
              >
                {task.assignee.username.slice(0, 2).toUpperCase()}
              </div>
              <span>{task.assignee.username}</span>
            </div>
          ) : (
            <span style={{ fontSize: 11, color: 'var(--text-muted)' }} title="Unassigned">
              Unassigned
            </span>
          )}
        </div>
        <div className="task-card-actions">
          <button
            className="task-action-btn"
            onClick={(e) => { e.stopPropagation(); onEdit(task); }}
            title="Comments & Details"
            id={`comments-task-${task.id}`}
          >
            <MessageSquare size={13} />
          </button>
          <button
            className="task-action-btn"
            onClick={(e) => { e.stopPropagation(); onEdit(task); }}
            title="Edit task"
            id={`edit-task-${task.id}`}
          >
            <Pencil size={13} />
          </button>
          <button
            className="task-action-btn delete"
            onClick={(e) => { e.stopPropagation(); onDelete(task.id); }}
            title="Delete task"
            id={`delete-task-${task.id}`}
          >
            <Trash2 size={13} />
          </button>
        </div>
      </div>
    </div>
  );
}
