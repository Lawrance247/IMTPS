import {
  Lightbulb,
  Pencil,
  Trash2,
  Plus,
  Search,
  StickyNote,
  X,
} from "lucide-react";
import { FormEvent, useEffect, useMemo, useState } from "react";
import "./NotesPage.css";

type Note = {
  id: number;
  title: string;
  body: string;
  tag: string;
  date: string;
};

const STORAGE_KEY = "imtps.notes";

function loadNotes(): Note[] {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    return saved ? JSON.parse(saved) : [];
  } catch {
    return [];
  }
}

function todayLabel() {
  return new Date().toLocaleDateString("en-ZA", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

export default function NotesPage() {
  const [notes, setNotes] = useState<Note[]>(loadNotes);
  const [query, setQuery] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingNote, setEditingNote] = useState<Note | null>(null);

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notes));
  }, [notes]);

  function deleteNote(id: number) {
    setNotes((current) => current.filter((note) => note.id !== id));
  }

  function openNewNote() {
    setEditingNote(null);
    setShowForm(true);
  }

  function openEditNote(note: Note) {
    setEditingNote(note);
    setShowForm(true);
  }

  function closeForm() {
    setEditingNote(null);
    setShowForm(false);
  }

  const filteredNotes = useMemo(
    () =>
      notes.filter((note) =>
        `${note.title} ${note.body} ${note.tag}`
          .toLowerCase()
          .includes(query.toLowerCase()),
      ),
    [notes, query],
  );

  function saveNote(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const title = String(form.get("title") ?? "").trim();
    const body = String(form.get("body") ?? "").trim();
    const tag = String(form.get("tag") ?? "General");

    if (!title || !body) return;

    if (editingNote) {
      setNotes((current) =>
        current.map((note) =>
          note.id === editingNote.id
            ? { ...note, title, body, tag }
            : note,
        ),
      );
    } else {
      setNotes((current) => [
        {
          id: Date.now(),
          title,
          body,
          tag,
          date: todayLabel(),
        },
        ...current,
      ]);
    }

    closeForm();
  }

  return (
    <div className="notes-page">
      <header className="notes-header">
        <div>
          <span className="notes-eyebrow">Capture it before you forget</span>
          <h1>Notes</h1>
          <p>Keep quick thoughts, reminders and ideas close to your plan.</p>
        </div>

        <button className="new-note-button" onClick={openNewNote}>
          <Plus size={18} />
          New Note
        </button>
      </header>

      <section className="notes-tools">
        <label className="notes-search">
          <Search size={18} />
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Search your notes..."
          />
        </label>

        <div className="notes-count">
          <StickyNote size={17} />
          <span>{notes.length} notes</span>
        </div>
      </section>

      <section className="notes-grid">
        {filteredNotes.map((note) => (
          <article className="note-card" key={note.id}>
            <div className="note-card-top">
              <span className={`note-tag ${note.tag.toLowerCase()}`}>
                {note.tag}
              </span>
              <div className="note-card-actions">
                <button
                  type="button"
                  aria-label={`Edit ${note.title}`}
                  title="Edit note"
                  onClick={() => openEditNote(note)}
                >
                  <Pencil size={17} />
                </button>
                <button
                  type="button"
                  aria-label={`Delete ${note.title}`}
                  title="Delete note"
                  onClick={() => deleteNote(note.id)}
                >
                  <Trash2 size={17} />
                </button>
              </div>
            </div>

            <h2>{note.title}</h2>
            <p>{note.body}</p>

            <footer>
              <span>{note.date}</span>
            </footer>
          </article>
        ))}

        <button className="note-add-card" onClick={openNewNote}>
          <span><Plus size={22} /></span>
          <strong>Add a note</strong>
          <small>Write down anything on your mind.</small>
        </button>
      </section>

      {filteredNotes.length === 0 && (
        <div className="notes-empty">
          <Search size={28} />
          <h3>{notes.length === 0 ? "No notes yet" : "No matching notes"}</h3>
          <p>
            {notes.length === 0
              ? "Create your first note using the New Note button."
              : "Try a different search."}
          </p>
        </div>
      )}

      <aside className="notes-tip">
        <Lightbulb size={18} />
        <div>
          <strong>Keep notes lightweight.</strong>
          <span>
            Notes are for thoughts and reminders. Anything that needs scheduling
            should become a task.
          </span>
        </div>
      </aside>

      {showForm && (
        <div className="note-modal-backdrop" onMouseDown={closeForm}>
          <div className="note-modal" onMouseDown={(event) => event.stopPropagation()}>
            <div className="note-modal-heading">
              <div>
                <span>Quick capture</span>
                <h2>{editingNote ? "Edit note" : "New note"}</h2>
              </div>
              <button onClick={closeForm} aria-label="Close">
                <X size={19} />
              </button>
            </div>

            <form onSubmit={saveNote} className="note-form">
              <label>
                <span>Title</span>
                <input name="title" defaultValue={editingNote?.title ?? ""} placeholder="What\'s this about?" autoFocus required />
              </label>

              <label>
                <span>Note</span>
                <textarea
                  name="body"
                  rows={6}
                  defaultValue={editingNote?.body ?? ""}
                  placeholder="Write anything on your mind..."
                  required
                />
              </label>

              <label>
                <span>Tag</span>
                <select name="tag" defaultValue={editingNote?.tag ?? "General"}>
                  <option>General</option>
                  <option>Academic</option>
                  <option>Project</option>
                  <option>Reminder</option>
                  <option>Personal</option>
                </select>
              </label>

              <div className="note-form-actions">
                <button type="button" onClick={closeForm}>
                  Cancel
                </button>
                <button type="submit">{editingNote ? "Save Changes" : "Save Note"}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
