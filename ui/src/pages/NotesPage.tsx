import {
  Lightbulb,
  MoreHorizontal,
  Plus,
  Search,
  StickyNote,
  X,
} from "lucide-react";
import { FormEvent, useMemo, useState } from "react";
import "./NotesPage.css";

type Note = {
  id: number;
  title: string;
  body: string;
  tag: string;
  date: string;
};

const initialNotes: Note[] = [
  {
    id: 1,
    title: "Database ideas",
    body: "Test SQLite CRUD and make sure task updates are reflected in the schedule.",
    tag: "Project",
    date: "16 Sep 2026",
  },
  {
    id: 2,
    title: "Presentation reminder",
    body: "Keep the strategy explanation simple: what goes in, how it prioritises, what comes out.",
    tag: "Reminder",
    date: "15 Sep 2026",
  },
  {
    id: 3,
    title: "INF3011F project",
    body: "Connect the React interface to the existing Java task and scheduling logic.",
    tag: "Academic",
    date: "14 Sep 2026",
  },
  {
    id: 4,
    title: "Weekend",
    body: "Call home and make time to rest after finishing the week's work.",
    tag: "Personal",
    date: "13 Sep 2026",
  },
];

export default function NotesPage() {
  const [notes, setNotes] = useState(initialNotes);
  const [query, setQuery] = useState("");
  const [showForm, setShowForm] = useState(false);

  const filteredNotes = useMemo(
    () =>
      notes.filter((note) =>
        `${note.title} ${note.body} ${note.tag}`
          .toLowerCase()
          .includes(query.toLowerCase()),
      ),
    [notes, query],
  );

  function addNote(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const title = String(form.get("title") ?? "").trim();
    const body = String(form.get("body") ?? "").trim();
    const tag = String(form.get("tag") ?? "General");

    if (!title || !body) return;

    setNotes((current) => [
      {
        id: Date.now(),
        title,
        body,
        tag,
        date: "17 Sep 2026",
      },
      ...current,
    ]);

    setShowForm(false);
  }

  return (
    <div className="notes-page">
      <header className="notes-header">
        <div>
          <span className="notes-eyebrow">Capture it before you forget</span>
          <h1>Notes</h1>
          <p>Keep quick thoughts, reminders and ideas close to your plan.</p>
        </div>

        <button className="new-note-button" onClick={() => setShowForm(true)}>
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
              <button aria-label={`Options for ${note.title}`}>
                <MoreHorizontal size={18} />
              </button>
            </div>

            <h2>{note.title}</h2>
            <p>{note.body}</p>

            <footer>
              <span>{note.date}</span>
            </footer>
          </article>
        ))}

        <button className="note-add-card" onClick={() => setShowForm(true)}>
          <span><Plus size={22} /></span>
          <strong>Add a note</strong>
          <small>Write down anything on your mind.</small>
        </button>
      </section>

      {filteredNotes.length === 0 && (
        <div className="notes-empty">
          <Search size={28} />
          <h3>No matching notes</h3>
          <p>Try a different search.</p>
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
        <div className="note-modal-backdrop" onMouseDown={() => setShowForm(false)}>
          <div className="note-modal" onMouseDown={(event) => event.stopPropagation()}>
            <div className="note-modal-heading">
              <div>
                <span>Quick capture</span>
                <h2>New note</h2>
              </div>
              <button onClick={() => setShowForm(false)} aria-label="Close">
                <X size={19} />
              </button>
            </div>

            <form onSubmit={addNote} className="note-form">
              <label>
                <span>Title</span>
                <input name="title" placeholder="What's this about?" autoFocus required />
              </label>

              <label>
                <span>Note</span>
                <textarea
                  name="body"
                  rows={6}
                  placeholder="Write anything on your mind..."
                  required
                />
              </label>

              <label>
                <span>Tag</span>
                <select name="tag" defaultValue="General">
                  <option>General</option>
                  <option>Academic</option>
                  <option>Project</option>
                  <option>Reminder</option>
                  <option>Personal</option>
                </select>
              </label>

              <div className="note-form-actions">
                <button type="button" onClick={() => setShowForm(false)}>
                  Cancel
                </button>
                <button type="submit">Save Note</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
