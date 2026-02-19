import { useState, useEffect } from 'react'
import './index.css'

function App() {
  const [tab, setTab] = useState('dashboard');

  return (
    <div className="app">
      <header className="header">
        <div className="brand">
          <h1>ProManage Enterprise</h1>
          <span>Intelligent Scheduling System</span>
        </div>
        <nav className="nav-tabs">
          <button className={`nav-btn ${tab === 'dashboard' ? 'active' : ''}`} onClick={() => setTab('dashboard')}>Dashboard</button>
          <button className={`nav-btn ${tab === 'planner' ? 'active' : ''}`} onClick={() => setTab('planner')}>AI Planner</button>
          <button className={`nav-btn ${tab === 'schedule' ? 'active' : ''}`} onClick={() => setTab('schedule')}>Schedule</button>
        </nav>
      </header>

      <main>
        {tab === 'dashboard' && <Dashboard />}
        {tab === 'planner' && <Planner />}
        {tab === 'schedule' && <ScheduleView />}
      </main>
    </div>
  )
}

function Dashboard() {
  const [projects, setProjects] = useState([]);
  const [stats, setStats] = useState({ revenue: 0, count: 0, historicalCount: 0 });

  useEffect(() => {
    fetch('http://localhost:8080/api/projects')
      .then(res => res.json())
      .then(data => {
        setProjects(data);
        // "Active" = PENDING (or not COMPLETED)
        const active = data.filter(p => p.status !== 'COMPLETED');
        const historical = data.filter(p => p.status === 'COMPLETED');

        const totalRev = active.reduce((sum, p) => sum + p.revenue, 0);
        setStats({
          revenue: totalRev,
          count: active.length,
          historicalCount: historical.length
        });
      })
      .catch(err => console.error(err));
  }, []);

  // Show only Active projects in table
  const activeProjects = projects.filter(p => p.status !== 'COMPLETED');

  return (
    <div>
      <div className="grid-3">
        <div className="card stat-card">
          <span className="stat-label">Active Revenue</span>
          <span className="stat-val">₹{stats.revenue.toLocaleString()}</span>
        </div>
        <div className="card stat-card">
          <span className="stat-label">Active Projects</span>
          <span className="stat-val">{stats.count}</span>
        </div>
        <div className="card stat-card">
          <span className="stat-label">Historical Data</span>
          <span className="stat-val" style={{ color: 'var(--text-muted)' }}>{stats.historicalCount} Records</span>
        </div>
      </div>

      <div className="card">
        <h3>Active Workload</h3>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Deadline</th>
              <th>Revenue</th>
            </tr>
          </thead>
          <tbody>
            {activeProjects.slice(0, 10).map(p => (
              <tr key={p.projectId}>
                <td>{p.projectId}</td>
                <td>{p.title}</td>
                <td>Day {p.deadline}</td>
                <td>₹{p.revenue.toLocaleString()}</td>
              </tr>
            ))}
            {activeProjects.length === 0 && <tr><td colSpan="4">No active projects. Use the AI Planner to add new work!</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function Planner() {
  const [form, setForm] = useState({ title: '', revenue: '', deadline: '' });
  const [result, setResult] = useState(null);
  const [history, setHistory] = useState([]);

  const startSession = () => {
    fetch('http://localhost:8080/api/acceptance/session', { method: 'POST' })
      .then(res => res.json())
      .then(() => {
        setHistory([]);
        setResult(null);
        alert("New Planning Session Started!");
      });
  };

  const checkProject = (e) => {
    e.preventDefault();
    const data = {
      title: form.title || `Project ${history.length + 1}`,
      revenue: parseFloat(form.revenue),
      deadline: parseInt(form.deadline)
    };

    fetch('http://localhost:8080/api/acceptance/evaluate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })
      .then(res => res.json())
      .then(res => {
        setResult(res);
        if (res.accepted) {
          setHistory([...history, { ...data, status: 'Accepted' }]);
        } else {
          setHistory([...history, { ...data, status: 'Rejected', reason: res.reason }]);
        }
      });
  };

  return (
    <div className="grid-3" style={{ gridTemplateColumns: '1fr 1fr' }}>
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h3>Incoming Project Stream</h3>
          <button onClick={startSession} style={{ background: 'var(--text-main)', color: 'white', border: 'none', padding: '0.5rem 1rem', borderRadius: '6px', cursor: 'pointer' }}>Start New Session</button>
        </div>

        <form onSubmit={checkProject}>
          <div className="form-group">
            <label>Project Title (Optional)</label>
            <input value={form.title} onChange={e => setForm({ ...form, title: e.target.value })} placeholder="e.g. Website Redesign" />
          </div>
          <div className="form-group">
            <label>Revenue (₹)</label>
            <input type="number" required value={form.revenue} onChange={e => setForm({ ...form, revenue: e.target.value })} />
          </div>
          <div className="form-group">
            <label>Deadline (Days)</label>
            <input type="number" min="1" required value={form.deadline} onChange={e => setForm({ ...form, deadline: e.target.value })} />
          </div>
          <button type="submit" className="btn">Evaluate Project</button>
        </form>

        {result && (
          <div style={{ marginTop: '1.5rem', padding: '1rem', borderRadius: '8px', background: result.accepted ? '#eff6ff' : '#fef2f2', borderLeft: `4px solid ${result.accepted ? 'var(--primary)' : 'var(--danger)'}` }}>
            <h4 style={{ color: result.accepted ? 'var(--primary)' : 'var(--danger)' }}>
              {result.accepted ? '✅ ACCEPTED' : '❌ REJECTED'}
            </h4>
            <p>{result.reason}</p>
          </div>
        )}
      </div>

      <div className="card">
        <h3>Session History</h3>
        <table>
          <thead>
            <tr>
              <th>Project</th>
              <th>Rev/Dead</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {history.map((h, i) => (
              <tr key={i}>
                <td>{h.title}</td>
                <td>₹{h.revenue} / {h.deadline}d</td>
                <td>
                  <span className={`tag ${h.status.toLowerCase()}`}>{h.status}</span>
                </td>
              </tr>
            ))}
            {history.length === 0 && <tr><td colSpan="3">No projects evaluated this session.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function ScheduleView() {
  const [schedule, setSchedule] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8080/api/schedule')
      .then(res => res.json())
      .then(data => setSchedule(data));
  }, []);

  const totalRev = schedule.reduce((sum, item) => sum + item.project.revenue, 0);

  return (
    <div className="card">
      <div className="header" style={{ borderBottom: 'none', marginBottom: '1rem' }}>
        <h3>Optimal Weekly Schedule</h3>
        <span className="tag accepted" style={{ fontSize: '1rem' }}>Total Revenue: ₹{totalRev.toLocaleString()}</span>
      </div>

      <table>
        <thead>
          <tr>
            <th>Day</th>
            <th>Project</th>
            <th>Deadline</th>
            <th>Revenue</th>
          </tr>
        </thead>
        <tbody>
          {schedule.map(item => (
            <tr key={item.dayNumber}>
              <td>
                <div style={{ fontWeight: 'bold' }}>{item.dayName}</div>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Day {item.dayNumber}</div>
              </td>
              <td>
                <div style={{ fontWeight: '600' }}>{item.project.title}</div>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{item.project.projectId}</div>
              </td>
              <td>Day {item.project.deadline}</td>
              <td>₹{item.project.revenue.toLocaleString()}</td>
            </tr>
          ))}
          {schedule.length === 0 && <tr><td colSpan="4">Schedule is empty. Run the Planner first!</td></tr>}
        </tbody>
      </table>
    </div>
  )
}

export default App
