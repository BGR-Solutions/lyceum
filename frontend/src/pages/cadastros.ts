import { request } from '../api'

interface Student { id: string; name: string }
interface Course { id: string; name: string }
interface Discipline { id: string; name: string; course?: { id: string; name: string } }
interface Classroom { id: string; discipline?: { id: string; name: string }; status?: string }

// ── state ─────────────────────────────────────────────────────────────────────
let students: Student[] = []
let courses: Course[] = []
let disciplines: Discipline[] = []
let classrooms: Classroom[] = []

let editingStudentId: string | null = null
let editingCourseId: string | null = null
let editingDisciplineId: string | null = null
let editingClassroomId: string | null = null

const CLASSROOM_STATUSES = ['OPEN', 'CLOSED']

// ── helpers ───────────────────────────────────────────────────────────────────
const esc = (s: string) =>
  s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')

const setResult = (msg: string) => {
  const el = document.getElementById('result') as HTMLPreElement | null
  if (el) el.textContent = msg
}

const loadAll = async () => {
  ;[students, courses, disciplines, classrooms] = await Promise.all([
    request('/students'),
    request('/courses'),
    request('/disciplines'),
    request('/classrooms'),
  ])
}

const refreshSelects = () => {
  const fill = (id: string, items: { id: string; label: string }[]) => {
    const sel = document.getElementById(id) as HTMLSelectElement | null
    if (sel) sel.innerHTML = items.map((i) => `<option value="${i.id}">${esc(i.label)}</option>`).join('')
  }
  fill('disciplineCourse', courses.map((c) => ({ id: c.id, label: c.name })))
  fill('classroomDiscipline', disciplines.map((d) => ({ id: d.id, label: d.name })))
  fill('enrollmentStudent', students.map((s) => ({ id: s.id, label: s.name })))
  fill('enrollmentClassroom', classrooms.map((c) => ({ id: c.id, label: c.discipline?.name ?? c.id })))
}

const refresh = async () => {
  await loadAll()
  renderStudentList()
  renderCourseList()
  renderDisciplineList()
  renderClassroomList()
  refreshSelects()
}

// ── students ──────────────────────────────────────────────────────────────────
const renderStudentList = () => {
  const tbody = document.querySelector<HTMLTableSectionElement>('#studentTable tbody')
  if (!tbody) return
  tbody.innerHTML = students.length === 0
    ? '<tr><td colspan="2">Nenhum aluno cadastrado.</td></tr>'
    : students.map((s) => `
        <tr data-id="${s.id}">
          <td>${esc(s.name)}</td>
          <td><button class="btn-edit">Editar</button></td>
        </tr>`).join('')
  tbody.querySelectorAll<HTMLButtonElement>('.btn-edit').forEach((btn) =>
    btn.addEventListener('click', () => startEditStudent(btn.closest('tr')!.dataset.id!)),
  )
}

const startEditStudent = (id: string) => {
  const s = students.find((x) => x.id === id)
  if (!s) return
  editingStudentId = id
  ;(document.getElementById('studentName') as HTMLInputElement).value = s.name
  ;(document.getElementById('saveStudent') as HTMLButtonElement).textContent = 'Salvar'
  ;(document.getElementById('cancelStudent') as HTMLButtonElement).style.display = ''
}

const cancelEditStudent = () => {
  editingStudentId = null
  ;(document.getElementById('studentName') as HTMLInputElement).value = ''
  ;(document.getElementById('saveStudent') as HTMLButtonElement).textContent = 'Incluir'
  ;(document.getElementById('cancelStudent') as HTMLButtonElement).style.display = 'none'
}

const saveStudent = async () => {
  const name = (document.getElementById('studentName') as HTMLInputElement).value.trim()
  if (!name) return
  try {
    if (editingStudentId) {
      await request(`/students/${editingStudentId}`, { method: 'PUT', body: JSON.stringify({ name }) })
    } else {
      await request('/students', { method: 'POST', body: JSON.stringify({ name }) })
    }
    cancelEditStudent()
    await refresh()
    setResult('Operação executada com sucesso.')
  } catch (err) {
    setResult(String(err))
  }
}

// ── courses ────────────────────────────────────────────────────────────────────
const renderCourseList = () => {
  const tbody = document.querySelector<HTMLTableSectionElement>('#courseTable tbody')
  if (!tbody) return
  tbody.innerHTML = courses.length === 0
    ? '<tr><td colspan="2">Nenhum curso cadastrado.</td></tr>'
    : courses.map((c) => `
        <tr data-id="${c.id}">
          <td>${esc(c.name)}</td>
          <td><button class="btn-edit">Editar</button></td>
        </tr>`).join('')
  tbody.querySelectorAll<HTMLButtonElement>('.btn-edit').forEach((btn) =>
    btn.addEventListener('click', () => startEditCourse(btn.closest('tr')!.dataset.id!)),
  )
}

const startEditCourse = (id: string) => {
  const c = courses.find((x) => x.id === id)
  if (!c) return
  editingCourseId = id
  ;(document.getElementById('courseName') as HTMLInputElement).value = c.name
  ;(document.getElementById('saveCourse') as HTMLButtonElement).textContent = 'Salvar'
  ;(document.getElementById('cancelCourse') as HTMLButtonElement).style.display = ''
}

const cancelEditCourse = () => {
  editingCourseId = null
  ;(document.getElementById('courseName') as HTMLInputElement).value = ''
  ;(document.getElementById('saveCourse') as HTMLButtonElement).textContent = 'Incluir'
  ;(document.getElementById('cancelCourse') as HTMLButtonElement).style.display = 'none'
}

const saveCourse = async () => {
  const name = (document.getElementById('courseName') as HTMLInputElement).value.trim()
  if (!name) return
  try {
    if (editingCourseId) {
      await request(`/courses/${editingCourseId}`, { method: 'PUT', body: JSON.stringify({ name }) })
    } else {
      await request('/courses', { method: 'POST', body: JSON.stringify({ name }) })
    }
    cancelEditCourse()
    await refresh()
    setResult('Operação executada com sucesso.')
  } catch (err) {
    setResult(String(err))
  }
}

// ── disciplines ────────────────────────────────────────────────────────────────
const renderDisciplineList = () => {
  const tbody = document.querySelector<HTMLTableSectionElement>('#disciplineTable tbody')
  if (!tbody) return
  tbody.innerHTML = disciplines.length === 0
    ? '<tr><td colspan="3">Nenhuma disciplina cadastrada.</td></tr>'
    : disciplines.map((d) => `
        <tr data-id="${d.id}">
          <td>${esc(d.name)}</td>
          <td>${esc(d.course?.name ?? '–')}</td>
          <td><button class="btn-edit">Editar</button></td>
        </tr>`).join('')
  tbody.querySelectorAll<HTMLButtonElement>('.btn-edit').forEach((btn) =>
    btn.addEventListener('click', () => startEditDiscipline(btn.closest('tr')!.dataset.id!)),
  )
}

const startEditDiscipline = (id: string) => {
  const d = disciplines.find((x) => x.id === id)
  if (!d) return
  editingDisciplineId = id
  ;(document.getElementById('disciplineName') as HTMLInputElement).value = d.name
  const sel = document.getElementById('disciplineCourse') as HTMLSelectElement
  if (d.course) sel.value = d.course.id
  ;(document.getElementById('saveDiscipline') as HTMLButtonElement).textContent = 'Salvar'
  ;(document.getElementById('cancelDiscipline') as HTMLButtonElement).style.display = ''
}

const cancelEditDiscipline = () => {
  editingDisciplineId = null
  ;(document.getElementById('disciplineName') as HTMLInputElement).value = ''
  ;(document.getElementById('saveDiscipline') as HTMLButtonElement).textContent = 'Incluir'
  ;(document.getElementById('cancelDiscipline') as HTMLButtonElement).style.display = 'none'
}

const saveDiscipline = async () => {
  const name = (document.getElementById('disciplineName') as HTMLInputElement).value.trim()
  const courseId = (document.getElementById('disciplineCourse') as HTMLSelectElement).value
  if (!name || !courseId) return
  try {
    if (editingDisciplineId) {
      await request(`/disciplines/${editingDisciplineId}`, { method: 'PUT', body: JSON.stringify({ name, courseId }) })
    } else {
      await request('/disciplines', { method: 'POST', body: JSON.stringify({ name, courseId }) })
    }
    cancelEditDiscipline()
    await refresh()
    setResult('Operação executada com sucesso.')
  } catch (err) {
    setResult(String(err))
  }
}

// ── classrooms ─────────────────────────────────────────────────────────────────
const renderClassroomList = () => {
  const tbody = document.querySelector<HTMLTableSectionElement>('#classroomTable tbody')
  if (!tbody) return
  tbody.innerHTML = classrooms.length === 0
    ? '<tr><td colspan="3">Nenhuma turma cadastrada.</td></tr>'
    : classrooms.map((c) => `
        <tr data-id="${c.id}">
          <td>${esc(c.discipline?.name ?? '–')}</td>
          <td>${
            editingClassroomId === c.id
              ? `<select class="status-sel">${CLASSROOM_STATUSES.map((st) =>
                  `<option value="${st}"${c.status === st ? ' selected' : ''}>${st}</option>`).join('')}</select>`
              : esc(c.status ?? '–')
          }</td>
          <td>${
            editingClassroomId === c.id
              ? `<button class="btn-save-status">Salvar</button>
                 <button class="btn-cancel-status">Cancelar</button>`
              : `<button class="btn-edit-status">Editar status</button>`
          }</td>
        </tr>`).join('')

  tbody.querySelectorAll<HTMLButtonElement>('.btn-edit-status').forEach((btn) =>
    btn.addEventListener('click', () => {
      editingClassroomId = btn.closest('tr')!.dataset.id!
      renderClassroomList()
    }),
  )
  tbody.querySelectorAll<HTMLButtonElement>('.btn-cancel-status').forEach((btn) =>
    btn.addEventListener('click', () => {
      editingClassroomId = null
      renderClassroomList()
    }),
  )
  tbody.querySelectorAll<HTMLButtonElement>('.btn-save-status').forEach((btn) =>
    btn.addEventListener('click', async () => {
      const row = btn.closest('tr')!
      const id = row.dataset.id!
      const status = row.querySelector<HTMLSelectElement>('.status-sel')!.value
      try {
        await request(`/classrooms/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) })
        editingClassroomId = null
        await refresh()
        setResult('Status atualizado com sucesso.')
      } catch (err) {
        setResult(String(err))
      }
    }),
  )
}

const createClassroom = async () => {
  const disciplineId = (document.getElementById('classroomDiscipline') as HTMLSelectElement).value
  const maxSeats = Number((document.getElementById('classroomSeats') as HTMLInputElement).value)
  const enrollmentStart = (document.getElementById('classroomStart') as HTMLInputElement).value
  const enrollmentEnd = (document.getElementById('classroomEnd') as HTMLInputElement).value
  if (!disciplineId || !maxSeats || !enrollmentStart || !enrollmentEnd) return
  try {
    await request('/classrooms', {
      method: 'POST',
      body: JSON.stringify({ disciplineId, maxSeats, enrollmentStart, enrollmentEnd }),
    })
    await refresh()
    setResult('Turma criada com sucesso.')
  } catch (err) {
    setResult(String(err))
  }
}

// ── enrollments ────────────────────────────────────────────────────────────────
const renderEnrollmentList = async () => {
  const list = document.getElementById('enrollmentList') as HTMLUListElement | null
  if (!list) return

  const all: any[] = []
  for (const classroom of classrooms) {
    const rows = await request(`/enrollments/by-classroom/${classroom.id}`)
    all.push(...rows)
  }

  if (all.length === 0) {
    list.innerHTML = '<li>Nenhuma matrícula encontrada.</li>'
    return
  }
  list.innerHTML = all
    .map(
      (e) => `
    <li data-id="${e.id}">
      ${esc(e.id)} – ${esc(e.status)}
      <button class="btn-confirm"${e.status !== 'PENDING' ? ' disabled' : ''}>Confirmar</button>
      <button class="btn-cancel-enr"${e.status === 'CANCELLED' ? ' disabled' : ''}>Cancelar</button>
    </li>`,
    )
    .join('')

  list.querySelectorAll<HTMLButtonElement>('.btn-confirm').forEach((btn) =>
    btn.addEventListener('click', async () => {
      const id = btn.closest('li')!.dataset.id!
      try {
        await request(`/enrollments/${id}/confirm`, { method: 'POST' })
        await renderEnrollmentList()
        setResult('Matrícula confirmada.')
      } catch (err) {
        setResult(String(err))
      }
    }),
  )
  list.querySelectorAll<HTMLButtonElement>('.btn-cancel-enr').forEach((btn) =>
    btn.addEventListener('click', async () => {
      const id = btn.closest('li')!.dataset.id!
      try {
        await request(`/enrollments/${id}/cancel`, { method: 'POST' })
        await renderEnrollmentList()
        setResult('Matrícula cancelada.')
      } catch (err) {
        setResult(String(err))
      }
    }),
  )
}

const createEnrollment = async () => {
  const studentId = (document.getElementById('enrollmentStudent') as HTMLSelectElement).value
  const classroomId = (document.getElementById('enrollmentClassroom') as HTMLSelectElement).value
  if (!studentId || !classroomId) return
  try {
    await request('/enrollments', { method: 'POST', body: JSON.stringify({ studentId, classroomId }) })
    await renderEnrollmentList()
    setResult('Matrícula criada com sucesso.')
  } catch (err) {
    setResult(String(err))
  }
}

// ── main render ────────────────────────────────────────────────────────────────
export function renderCadastros(container: HTMLElement): void {
  container.innerHTML = `
    <nav class="breadcrumb"><a href="#/">← Início</a></nav>
    <h1>Lyceum - Cadastros</h1>

    <section>
      <h2>Alunos</h2>
      <div class="form-row">
        <input id="studentName" placeholder="Nome do aluno" />
        <button id="saveStudent">Incluir</button>
        <button id="cancelStudent" style="display:none">Cancelar</button>
      </div>
      <table id="studentTable">
        <thead><tr><th>Nome</th><th>Ações</th></tr></thead>
        <tbody></tbody>
      </table>
    </section>

    <section>
      <h2>Cursos</h2>
      <div class="form-row">
        <input id="courseName" placeholder="Nome do curso" />
        <button id="saveCourse">Incluir</button>
        <button id="cancelCourse" style="display:none">Cancelar</button>
      </div>
      <table id="courseTable">
        <thead><tr><th>Nome</th><th>Ações</th></tr></thead>
        <tbody></tbody>
      </table>
    </section>

    <section>
      <h2>Disciplinas</h2>
      <div class="form-row">
        <input id="disciplineName" placeholder="Nome da disciplina" />
        <select id="disciplineCourse"></select>
        <button id="saveDiscipline">Incluir</button>
        <button id="cancelDiscipline" style="display:none">Cancelar</button>
      </div>
      <table id="disciplineTable">
        <thead><tr><th>Nome</th><th>Curso</th><th>Ações</th></tr></thead>
        <tbody></tbody>
      </table>
    </section>

    <section>
      <h2>Turmas</h2>
      <div class="form-row">
        <select id="classroomDiscipline"></select>
        <input id="classroomSeats" type="number" min="1" value="30" />
        <input id="classroomStart" type="date" />
        <input id="classroomEnd" type="date" />
        <button id="createClassroom">Incluir</button>
      </div>
      <table id="classroomTable">
        <thead><tr><th>Disciplina</th><th>Status</th><th>Ações</th></tr></thead>
        <tbody></tbody>
      </table>
    </section>

    <section>
      <h2>Matrículas</h2>
      <div class="form-row">
        <select id="enrollmentStudent"></select>
        <select id="enrollmentClassroom"></select>
        <button id="createEnrollment">Incluir matrícula</button>
        <button id="refreshEnrollments">Atualizar listagem</button>
      </div>
      <ul id="enrollmentList"></ul>
    </section>

    <pre id="result"></pre>
  `

  const today = new Date().toISOString().slice(0, 10)
  ;(document.getElementById('classroomStart') as HTMLInputElement).value = today
  ;(document.getElementById('classroomEnd') as HTMLInputElement).value = today

  document.getElementById('saveStudent')!.addEventListener('click', saveStudent)
  document.getElementById('cancelStudent')!.addEventListener('click', cancelEditStudent)
  document.getElementById('saveCourse')!.addEventListener('click', saveCourse)
  document.getElementById('cancelCourse')!.addEventListener('click', cancelEditCourse)
  document.getElementById('saveDiscipline')!.addEventListener('click', saveDiscipline)
  document.getElementById('cancelDiscipline')!.addEventListener('click', cancelEditDiscipline)
  document.getElementById('createClassroom')!.addEventListener('click', createClassroom)
  document.getElementById('createEnrollment')!.addEventListener('click', createEnrollment)
  document.getElementById('refreshEnrollments')!.addEventListener('click', () => {
    renderEnrollmentList().catch((err) => setResult(String(err)))
  })

  loadAll()
    .then(() => {
      renderStudentList()
      renderCourseList()
      renderDisciplineList()
      renderClassroomList()
      refreshSelects()
    })
    .catch((err) => setResult(String(err)))
}
