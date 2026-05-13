const API_URL = window.API_GATEWAY_URL || 'http://localhost:8080';

const state = {
  profesores: [],
  asignaturas: [],
  selectedSubject: null,
};

const elements = {
  apiStatus: document.querySelector('#apiStatus'),
  professorSelect: document.querySelector('#professorSelect'),
  subjectSelect: document.querySelector('#subjectSelect'),
  studentsBox: document.querySelector('#studentsBox'),
  topicInput: document.querySelector('#topicInput'),
  requestForm: document.querySelector('#requestForm'),
  refreshButton: document.querySelector('#refreshButton'),
  submitButton: document.querySelector('#submitButton'),
  formMessage: document.querySelector('#formMessage'),
  resultContent: document.querySelector('#resultContent'),
  finalStatus: document.querySelector('#finalStatus'),
  historyButton: document.querySelector('#historyButton'),
  historyList: document.querySelector('#historyList'),
};

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  });

  if (!response.ok) {
    let message = `Error HTTP ${response.status}`;
    try {
      const body = await response.json();
      message = body.error || message;
    } catch {
      message = await response.text();
    }
    throw new Error(message);
  }

  return response.status === 204 ? null : response.json();
}

function setStatus(message, mode = 'idle') {
  elements.apiStatus.textContent = message;
  elements.apiStatus.dataset.mode = mode;
}

function setFormMessage(message, mode = 'info') {
  elements.formMessage.textContent = message;
  elements.formMessage.dataset.mode = mode;
}

function option(label, value) {
  const element = document.createElement('option');
  element.textContent = label;
  element.value = value;
  return element;
}

function renderProfessors() {
  elements.professorSelect.replaceChildren();
  for (const professor of state.profesores) {
    elements.professorSelect.append(option(`${professor.nombre} (${professor.email})`, professor.id));
  }
}

function renderSubjects() {
  const professorId = Number(elements.professorSelect.value);
  const filtered = state.asignaturas.filter((subject) => subject.profesor.id === professorId);
  elements.subjectSelect.replaceChildren();

  for (const subject of filtered) {
    elements.subjectSelect.append(option(`${subject.nombre} - ${subject.ciclo}`, subject.id));
  }

  if (filtered.length === 0) {
    elements.subjectSelect.append(option('Sin asignaturas disponibles', ''));
  }

  renderStudents();
}

function renderStudents() {
  const subjectId = Number(elements.subjectSelect.value);
  state.selectedSubject = state.asignaturas.find((subject) => subject.id === subjectId);
  elements.studentsBox.replaceChildren();

  if (!state.selectedSubject) {
    elements.studentsBox.textContent = 'Seleccione una asignatura.';
    return;
  }

  for (const student of state.selectedSubject.estudiantes) {
    const label = document.createElement('label');
    label.className = 'student-row';
    label.innerHTML = `
      <input type="checkbox" name="student" value="${student.id}" checked />
      <span>${student.nombre}</span>
      <small>${student.email}</small>
    `;
    elements.studentsBox.append(label);
  }
}

function selectedStudentIds() {
  return [...document.querySelectorAll('input[name="student"]:checked')]
    .map((input) => Number(input.value));
}

function resourceCard(resource) {
  const article = document.createElement('article');
  article.className = 'resource-card';
  article.innerHTML = `
    <div class="resource-provider">
      <span>${resource.provider}</span>
      ${resource.fallback ? '<strong>Demo</strong>' : '<strong>API</strong>'}
    </div>
    <h3>${resource.title}</h3>
    <p>${resource.description}</p>
    <a href="${resource.url}" target="_blank" rel="noreferrer">Abrir recurso</a>
  `;
  return article;
}

function renderResult(result) {
  elements.finalStatus.textContent = result.finalStatus;
  elements.finalStatus.dataset.mode = result.finalStatus === 'COMPLETADA' ? 'ok' : 'idle';
  elements.resultContent.className = 'result-content';
  elements.resultContent.replaceChildren();

  const summary = document.createElement('div');
  summary.className = 'summary-grid';
  summary.innerHTML = `
    <div><span>Profesor</span><strong>${result.professor.nombre}</strong></div>
    <div><span>Asignatura</span><strong>${result.subject.nombre}</strong></div>
    <div><span>Tema</span><strong>${result.topic}</strong></div>
    <div><span>Destinatarios</span><strong>${result.students.length}</strong></div>
  `;

  const providers = document.createElement('div');
  providers.className = 'providers';
  providers.innerHTML = result.providersUsed.map((provider) => `<span>${provider}</span>`).join('');

  const nasa = document.createElement('section');
  nasa.className = 'resource-section';
  nasa.innerHTML = '<h3>Recursos NASA</h3>';
  result.nasaResources.forEach((resource) => nasa.append(resourceCard(resource)));

  const nytimes = document.createElement('section');
  nytimes.className = 'resource-section';
  nytimes.innerHTML = '<h3>Articulos NYTimes</h3>';
  result.nytimesArticles.forEach((resource) => nytimes.append(resourceCard(resource)));

  const notification = document.createElement('section');
  notification.className = 'notification-box';
  notification.innerHTML = `
    <h3>Notificacion</h3>
    <p><strong>${result.notification.status}</strong> - ${result.notification.message}</p>
    <p>Evidencia: ${result.notification.evidenceId}</p>
  `;

  elements.resultContent.append(summary, providers, nasa, nytimes, notification);

  if (result.warnings?.length) {
    const warnings = document.createElement('div');
    warnings.className = 'warnings';
    warnings.textContent = `Avisos: ${result.warnings.join(' | ')}`;
    elements.resultContent.append(warnings);
  }
}

function renderHistory(items) {
  elements.historyList.replaceChildren();

  if (!items.length) {
    elements.historyList.className = 'empty-state';
    elements.historyList.textContent = 'Aun no existen solicitudes registradas.';
    return;
  }

  elements.historyList.className = 'history-list';
  for (const item of items.toReversed()) {
    const row = document.createElement('article');
    row.className = 'history-item';
    row.innerHTML = `
      <div>
        <h3>${item.topic}</h3>
        <p>${item.subject.nombre} - ${item.professor.nombre}</p>
      </div>
      <div>
        <span>${new Date(item.createdAt).toLocaleString()}</span>
        <strong>${item.status}</strong>
      </div>
      <small>${item.providersUsed.join(', ')}</small>
    `;
    elements.historyList.append(row);
  }
}

async function loadBaseData() {
  setStatus('Conectando', 'idle');
  setFormMessage('');
  const [profesores, asignaturas] = await Promise.all([
    request('/api/profesores'),
    request('/api/asignaturas'),
  ]);

  state.profesores = profesores;
  state.asignaturas = asignaturas;
  renderProfessors();
  renderSubjects();
  setStatus('API conectada', 'ok');
}

async function loadHistory() {
  const solicitudes = await request('/api/solicitudes');
  renderHistory(solicitudes);
}

async function submitForm(event) {
  event.preventDefault();
  const studentIds = selectedStudentIds();
  if (!studentIds.length) {
    setFormMessage('Seleccione al menos un estudiante.', 'error');
    return;
  }

  const payload = {
    professorId: Number(elements.professorSelect.value),
    subjectId: Number(elements.subjectSelect.value),
    studentIds,
    topic: elements.topicInput.value.trim(),
  };

  elements.submitButton.disabled = true;
  setFormMessage('Ejecutando proceso distribuido...', 'info');

  try {
    const result = await request('/api/academic-requests', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    renderResult(result);
    await loadHistory();
    setFormMessage('Solicitud academica creada correctamente.', 'ok');
  } catch (error) {
    setFormMessage(error.message, 'error');
  } finally {
    elements.submitButton.disabled = false;
  }
}

elements.professorSelect.addEventListener('change', renderSubjects);
elements.subjectSelect.addEventListener('change', renderStudents);
elements.requestForm.addEventListener('submit', submitForm);
elements.refreshButton.addEventListener('click', async () => {
  try {
    await loadBaseData();
    await loadHistory();
  } catch (error) {
    setStatus('API no disponible', 'error');
    setFormMessage(error.message, 'error');
  }
});
elements.historyButton.addEventListener('click', async () => {
  try {
    await loadHistory();
  } catch (error) {
    setFormMessage(error.message, 'error');
  }
});

loadBaseData()
  .then(loadHistory)
  .catch((error) => {
    setStatus('API no disponible', 'error');
    setFormMessage(error.message, 'error');
  });
