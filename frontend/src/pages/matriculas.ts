import { request } from '../api'
import { buildClassroomLabels } from './cadastros'

interface Student { id: string; name: string }
interface Classroom {
  id: string
  discipline?: { id: string; name: string }
  status?: string
  seatLimit?: { maxSeats: number; occupiedSeats: number }
  enrollmentPeriod?: { startDate: string; endDate: string }
}
interface Enrollment {
  id: string
  status: string
  student?: { id: string; name: string }
  classroom?: { id: string; discipline?: { id: string; name: string } }
}

let students: Student[] = []
let classrooms: Classroom[] = []
let classroomLabels: Map<string, string> = new Map()

const esc = (s: string) =>
  s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')

const statusText: Record<string, string> = {
  CONFIRMED: 'Confirmada',
  CANCELLED: 'Cancelada',
  PENDING: 'Pendente',
}

const setStatus = (msg: string) => {
  const el = document.getElementById('matriculasStatus')
  if (el) el.textContent = msg
}

const renderTable = (enrollments: Enrollment[]) => {
  // #matriculasTable is the <tbody> itself — no descendant selector needed
  const tbody = document.getElementById('matriculasTable') as HTMLTableSectionElement | null
  if (!tbody) return

  if (enrollments.length === 0) {
    tbody.innerHTML = '<tr><td colspan="3" class="empty-msg">Nenhuma matrícula encontrada.</td></tr>'
    return
  }

  tbody.innerHTML = enrollments
    .map((e) => {
      const studentName = e.student?.name ?? '–'
      const classroomId = e.classroom?.id ?? ''
      const classroomLabel = classroomId ? (classroomLabels.get(classroomId) ?? classroomId) : '–'
      const status = e.status ?? '–'
      return `
        <tr>
          <td>${esc(studentName)}</td>
          <td>${esc(classroomLabel)}</td>
          <td><span class="status-badge status-${status.toLowerCase()}">${esc(statusText[status] ?? status)}</span></td>
        </tr>`
    })
    .join('')
}

const search = async () => {
  const studentId = (document.getElementById('filterStudent') as HTMLSelectElement).value
  const classroomId = (document.getElementById('filterClassroom') as HTMLSelectElement).value
  setStatus('Carregando...')

  try {
    let enrollments: Enrollment[] = []

    if (classroomId) {
      enrollments = await request(`/enrollments/by-classroom/${classroomId}`)
      if (studentId) {
        enrollments = enrollments.filter((e) => e.student?.id === studentId)
      }
    } else if (studentId) {
      enrollments = await request(`/enrollments/by-student/${studentId}`)
    } else {
      // No filter selected — load all by iterating classrooms
      const all: Enrollment[] = []
      for (const c of classrooms) {
        const rows: Enrollment[] = await request(`/enrollments/by-classroom/${c.id}`)
        all.push(...rows)
      }
      enrollments = all
    }

    renderTable(enrollments)
    setStatus(enrollments.length === 0 ? '' : `${enrollments.length} matrícula(s) encontrada(s).`)
  } catch (err) {
    setStatus(String(err))
  }
}

export async function renderMatriculas(container: HTMLElement): Promise<void> {
  container.innerHTML = `
    <nav class="breadcrumb"><a href="#/">← Início</a></nav>
    <h1>Consulta de Matrículas</h1>

    <section>
      <div class="discipline-filters">
        <select id="filterStudent">
          <option value="">— todos os alunos —</option>
        </select>
        <select id="filterClassroom">
          <option value="">— todas as turmas —</option>
        </select>
        <button id="searchBtn" class="enroll-btn">Buscar</button>
      </div>
      <p id="matriculasStatus" class="status-msg"></p>
      <table class="matriculas-table">
        <thead>
          <tr><th>Aluno</th><th>Turma</th><th>Status</th></tr>
        </thead>
        <tbody id="matriculasTable">
          <tr><td colspan="3" class="empty-msg">Use os filtros acima e clique em Buscar.</td></tr>
        </tbody>
      </table>
    </section>
  `

  document.getElementById('searchBtn')!.addEventListener('click', search)
  document.getElementById('filterStudent')!.addEventListener('change', search)
  document.getElementById('filterClassroom')!.addEventListener('change', search)

  try {
    setStatus('Carregando...')
    ;[students, classrooms] = await Promise.all([request('/students'), request('/classrooms')])
    classroomLabels = buildClassroomLabels(classrooms)

    const studentSel = document.getElementById('filterStudent') as HTMLSelectElement
    studentSel.innerHTML =
      '<option value="">— todos os alunos —</option>' +
      students.map((s) => `<option value="${s.id}">${esc(s.name)}</option>`).join('')

    const classroomSel = document.getElementById('filterClassroom') as HTMLSelectElement
    classroomSel.innerHTML =
      '<option value="">— todas as turmas —</option>' +
      classrooms
        .map((c) => `<option value="${c.id}">${esc(classroomLabels.get(c.id) ?? c.id)}</option>`)
        .join('')

    setStatus('')
  } catch (err) {
    setStatus(String(err))
  }
}
