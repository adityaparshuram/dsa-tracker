const $ = s => document.querySelector(s);
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const label = s => s.replace('_', ' ').toLowerCase().replace(/^./, c => c.toUpperCase());
let editingId = null, problems = [];

async function api(path, opts = {}) {
  const res = await fetch('/api' + path, { headers: { 'Content-Type': 'application/json' }, ...opts });
  if (res.status === 401) { location.href = 'login.html'; throw new Error('Please log in'); }
  if (!res.ok) { const e = await res.json().catch(() => ({})); throw new Error(e.message || 'Request failed'); }
  return res.status === 204 ? null : res.json();
}

async function load() {
  const p = new URLSearchParams();
  ['q', 'topic', 'difficulty', 'status'].forEach(k => { const v = $('#f-' + k).value.trim(); if (v) p.set(k, v); });
  const [list, stats] = await Promise.all([api('/problems?' + p), api('/stats')]);
  problems = list; renderRows(); renderStats(stats);
}

function renderRows() {
  $('#empty').hidden = problems.length > 0;
  $('#rows').innerHTML = problems.map(p => `
    <tr>
      <td>${p.link ? `<a href="${esc(p.link)}" target="_blank" rel="noopener">${esc(p.title)}</a>` : esc(p.title)}
          ${p.platform ? `<small class="muted"> ${esc(p.platform)}</small>` : ''}</td>
      <td>${esc(p.topic)}</td>
      <td><span class="tag ${p.difficulty}">${label(p.difficulty)}</span></td>
      <td><span class="tag ${p.status}">${label(p.status)}</span></td>
      <td>${p.solvedDate ?? '–'}</td>
      <td><button class="icon" data-edit="${p.id}">Edit</button><button class="icon" data-del="${p.id}">Delete</button></td>
    </tr>`).join('');
}

function bars(el, data, title) {
  const max = Math.max(1, ...Object.values(data));
  el.innerHTML = `<p class="muted">${title}</p>` + (Object.keys(data).length
      ? Object.entries(data).slice(0, 6).map(([k, v]) =>
          `<div class="row"><span>${esc(label(k))}</span><div class="bar"><span style="width:${v / max * 100}%"></span></div><b>${v}</b></div>`).join('')
      : '<p class="muted">Nothing solved yet</p>');
}

function renderStats(s) {
  $('#s-streak').textContent = s.streak; $('#s-solved').textContent = s.solved;
  $('#s-total').textContent = s.total; $('#s-revisit').textContent = s.revisit;
  bars($('#s-diff'), s.byDifficulty, 'Solved by difficulty');
  bars($('#s-topic'), s.byTopic, 'Solved by topic');
  const heat = $('#heat'); heat.innerHTML = '';
  const days = Object.entries(s.activity);
  const offset = new Date(days[0][0] + 'T00:00').getDay();           // align first day to its weekday row
  for (let i = 0; i < offset; i++) heat.appendChild(document.createElement('i')).style.visibility = 'hidden';
  days.forEach(([d, n]) => {
    const c = document.createElement('i'); c.title = `${d}: ${n} solved`;
    c.dataset.l = n === 0 ? 0 : n === 1 ? 1 : n < 4 ? 2 : 3; heat.appendChild(c);
  });
}

function openForm(p = null) {
  editingId = p?.id ?? null; $('#err').textContent = '';
  $('#dlg-title').textContent = p ? 'Edit problem' : 'Add problem';
  for (const f of ['title', 'topic', 'platform', 'difficulty', 'status', 'link', 'notes'])
    $('#' + f).value = p?.[f] ?? (f === 'difficulty' ? 'MEDIUM' : f === 'status' ? 'TODO' : '');
  $('#dlg').showModal(); $('#title').focus();
}

async function save() {
  const body = {};
  for (const f of ['title', 'topic', 'platform', 'difficulty', 'status', 'link', 'notes']) body[f] = $('#' + f).value.trim() || null;
  try {
    await api(editingId ? '/problems/' + editingId : '/problems', { method: editingId ? 'PUT' : 'POST', body: JSON.stringify(body) });
    $('#dlg').close(); load();
  } catch (e) { $('#err').textContent = e.message; }
}

$('#add-btn').onclick = () => openForm();
$('#cancel').onclick = () => $('#dlg').close();
$('#save').onclick = save;
$('#rows').onclick = async e => {
  const ed = e.target.dataset.edit, del = e.target.dataset.del;
  if (ed) openForm(problems.find(p => p.id == ed));
  if (del && confirm('Delete this problem?')) { await api('/problems/' + del, { method: 'DELETE' }); load(); }
};
let t; ['q', 'topic', 'difficulty', 'status'].forEach(k =>
    $('#f-' + k).addEventListener('input', () => { clearTimeout(t); t = setTimeout(load, 250); }));
load();

api('/auth/me').then(u => { $('#who').textContent = u.name; }).catch(() => {});
$('#logout').onclick = async () => { await api('/auth/logout', { method: 'POST' }); location.href = 'login.html'; };