import { request } from '../api'

interface Discipline {
  id: string
  name: string
  course?: { id: string; name: string }
}

interface Classroom {
  id: string
  discipline?: { id: string; name: string }
  status?: string
  seatLimit?: { maxSeats: number; occupiedSeats: number }
}

interface Enrollment {
  id: string
  classroom?: { id: string; discipline?: { id: string } }
  status: string
}

interface Student {
  id: string
  name: string
}

let allDisciplines: Discipline[] = []
let allClassrooms: Classroom[] = []
let enrolledDisciplineIds: Set<string> = new Set()
let selectedStudentId = ''
let nameFilter = ''
let showOnlyEnrolled = false
let allowFullClassroomEnroll = false

const isClassroomFull = (c: Classroom): boolean => {
  const sl = c.seatLimit
  return !!sl && sl.occupiedSeats >= sl.maxSeats
}

const buildDisciplineToClassroomsMap = (): Map<string, Classroom[]> => {
  const map = new Map<string, Classroom[]>()
  for (const c of allClassrooms) {
    const discId = c.discipline?.id
    if (!discId) continue
    if (!map.has(discId)) map.set(discId, [])
    map.get(discId)!.push(c)
  }
  return map
}

const renderDisciplineList = () => {
  const list = document.getElementById('disciplineList')
  if (!list) return

  const discToClassrooms = buildDisciplineToClassroomsMap()

  let filtered = allDisciplines
  if (nameFilter) {
    filtered = filtered.filter((d) =>
      d.name.toLowerCase().includes(nameFilter.toLowerCase()),
    )
  }
  if (showOnlyEnrolled) {
    filtered = filtered.filter((d) => enrolledDisciplineIds.has(d.id))
  }

  if (filtered.length === 0) {
    list.innerHTML = '<li class="empty-msg">Nenhuma disciplina encontrada.</li>'
    return
  }

  list.innerHTML = filtered
    .map((d) => {
      const isEnrolled = enrolledDisciplineIds.has(d.id)
      const classrooms = discToClassrooms.get(d.id) || []
      const hasClassroom = classrooms.length > 0
      const course = d.course?.name ? `<span class="course-tag">${d.course.name}</span>` : ''

      const allFull = classrooms.length > 0 && classrooms.every(isClassroomFull)

      let actionBtn: string
      if (isEnrolled) {
        actionBtn = `<span class="badge enrolled">Matriculado</span>`
      } else if (!hasClassroom) {
        actionBtn = `<span class="badge no-class">Sem turma disponível</span>`
      } else if (allFull && !allowFullClassroomEnroll) {
        actionBtn = `<span class="badge full">Turma Cheia</span>`
      } else {
        const fullCls = allFull ? ' enroll-btn--full' : ''
        actionBtn = `<button class="enroll-btn${fullCls}" data-discipline-id="${d.id}">Matricular</button>`
      }

      return `
        <li class="discipline-item">
          <div class="discipline-info">
            <strong>${d.name}</strong> ${course}
          </div>
          <div class="discipline-action">${actionBtn}</div>
        </li>
      `
    })
    .join('')

  list.querySelectorAll<HTMLButtonElement>('.enroll-btn').forEach((btn) => {
    btn.addEventListener('click', async () => {
      const disciplineId = btn.dataset.disciplineId!
      await enrollStudentInDiscipline(disciplineId, btn)
    })
  })
}

const enrollStudentInDiscipline = async (disciplineId: string, btn: HTMLButtonElement) => {
  const statusEl = document.getElementById('studentStatus')
  const setStatus = (msg: string, type: 'error' | 'success' | '') => {
    if (!statusEl) return
    statusEl.textContent = msg
    statusEl.className = `status-msg${type ? ` ${type}` : ''}`
  }

  if (!selectedStudentId) {
    setStatus('Selecione um aluno primeiro.', 'error')
    return
  }

  const discToClassrooms = buildDisciplineToClassroomsMap()
  const classrooms = discToClassrooms.get(disciplineId) || []
  if (classrooms.length === 0) {
    setStatus('Nenhuma turma disponível para esta disciplina.', 'error')
    return
  }

  if (classrooms.every(isClassroomFull)) {
    setStatus('Turma cheia: não há vagas disponíveis para esta disciplina.', 'error')
    return
  }

  const classroom = classrooms.find((c) => !isClassroomFull(c)) ?? classrooms[0]
  try {
    btn.disabled = true
    await request('/enrollments', {
      method: 'POST',
      body: JSON.stringify({ studentId: selectedStudentId, classroomId: classroom.id }),
    })
    enrolledDisciplineIds.add(disciplineId)
    setStatus('Matrícula realizada com sucesso!', 'success')
    allClassrooms = await request('/classrooms')
    renderDisciplineList()
  } catch (err) {
    const msg = String(err).toLowerCase()
    const isFull = msg.includes('seat') || msg.includes('vaga') || msg.includes('no seats')
    setStatus(
      isFull ? 'Turma cheia: não há vagas disponíveis.' : String(err),
      'error',
    )
    btn.disabled = false
  }
}

const loadStudentData = async (studentId: string) => {
  const statusEl = document.getElementById('studentStatus')
  if (!studentId) {
    enrolledDisciplineIds = new Set()
    renderDisciplineList()
    return
  }

  try {
    if (statusEl) statusEl.textContent = 'Carregando...'
    const enrollments: Enrollment[] = await request(`/enrollments/by-student/${studentId}`)

    enrolledDisciplineIds = new Set(
      enrollments
        .map((e) => e.classroom?.discipline?.id)
        .filter((id): id is string => Boolean(id)),
    )

    if (statusEl) statusEl.textContent = ''
    renderDisciplineList()
  } catch (err) {
    if (statusEl) statusEl.textContent = String(err)
  }
}

export async function renderAluno(container: HTMLElement): Promise<void> {
  container.innerHTML = `
    <nav class="breadcrumb"><a href="#/">← Início</a></nav>
    <h1>Perfil de Aluno</h1>

    <section>
      <h2>Selecionar Aluno</h2>
      <select id="alunoSelect"><option value="">— selecione —</option></select>
    </section>

    <section>
      <h2>Disciplinas</h2>
      <div class="discipline-filters">
        <input id="disciplineSearch" type="search" placeholder="Buscar por nome..." />
        <label class="filter-toggle">
          <input id="enrolledFilter" type="checkbox" />
          Apenas matriculadas
        </label>
        <label class="filter-toggle flag-toggle" title="Habilitar para demonstrar o tratamento de erro em turmas cheias">
          <input id="fullClassroomFlag" type="checkbox" />
          Habilitar matrícula em turmas cheias
        </label>
      </div>
      <ul id="disciplineList" class="discipline-list">
        <li class="empty-msg">Selecione um aluno para ver as disciplinas.</li>
      </ul>
    </section>

    <p id="studentStatus" class="status-msg"></p>
  `

  // reset state
  allDisciplines = []
  allClassrooms = []
  enrolledDisciplineIds = new Set()
  selectedStudentId = ''
  nameFilter = ''
  showOnlyEnrolled = false
  allowFullClassroomEnroll = false

  try {
    const [students, disciplines, classrooms] = await Promise.all([
      request('/students'),
      request('/disciplines'),
      request('/classrooms'),
    ])

    allDisciplines = disciplines as Discipline[]
    allClassrooms = classrooms as Classroom[]

    const alunoSelect = document.getElementById('alunoSelect') as HTMLSelectElement
    alunoSelect.innerHTML =
      '<option value="">— selecione —</option>' +
      (students as Student[])
        .map((s) => `<option value="${s.id}">${s.name || s.id}</option>`)
        .join('')

    alunoSelect.addEventListener('change', () => {
      selectedStudentId = alunoSelect.value
      loadStudentData(selectedStudentId)
    })
  } catch (err) {
    const statusEl = document.getElementById('studentStatus')
    if (statusEl) statusEl.textContent = String(err)
  }

  const searchInput = document.getElementById('disciplineSearch') as HTMLInputElement
  searchInput.addEventListener('input', () => {
    nameFilter = searchInput.value
    renderDisciplineList()
  })

  const enrolledCheckbox = document.getElementById('enrolledFilter') as HTMLInputElement
  enrolledCheckbox.addEventListener('change', () => {
    showOnlyEnrolled = enrolledCheckbox.checked
    renderDisciplineList()
  })

  const fullClassroomFlag = document.getElementById('fullClassroomFlag') as HTMLInputElement
  fullClassroomFlag.addEventListener('change', () => {
    allowFullClassroomEnroll = fullClassroomFlag.checked
    renderDisciplineList()
  })
}
