import './style.css'

type Entity = { id: string; name?: string }

const apiBase = (import.meta.env.VITE_API_BASE as string) || 'http://localhost:8080'

const app = document.querySelector<HTMLDivElement>('#app')!
app.innerHTML = `
  <h1>Lyceum - Gestão Acadêmica</h1>
  <p class="hint">API: ${apiBase}</p>

  <section>
    <h2>Aluno</h2>
    <input id="studentName" placeholder="Nome do aluno" />
    <button id="createStudent">Criar</button>
  </section>

  <section>
    <h2>Curso</h2>
    <input id="courseName" placeholder="Nome do curso" />
    <button id="createCourse">Criar</button>
  </section>

  <section>
    <h2>Disciplina</h2>
    <input id="disciplineName" placeholder="Nome da disciplina" />
    <select id="disciplineCourse"></select>
    <button id="createDiscipline">Criar</button>
  </section>

  <section>
    <h2>Turma</h2>
    <select id="classroomDiscipline"></select>
    <input id="classroomSeats" type="number" min="1" value="30" />
    <input id="classroomStart" type="date" />
    <input id="classroomEnd" type="date" />
    <button id="createClassroom">Criar</button>
  </section>

  <section>
    <h2>Matrícula</h2>
    <select id="enrollmentStudent"></select>
    <select id="enrollmentClassroom"></select>
    <button id="createEnrollment">Criar matrícula</button>
    <button id="refreshEnrollments">Atualizar listagem</button>
    <ul id="enrollmentList"></ul>
  </section>

  <pre id="result"></pre>
`

const result = document.getElementById('result') as HTMLPreElement

const request = async (path: string, options: RequestInit = {}) => {
  const response = await fetch(`${apiBase}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text)
  }

  if (response.status === 204) return null
  return response.json()
}

const toOption = (entity: Entity) => `<option value="${entity.id}">${entity.name || entity.id}</option>`

const renderSelect = (id: string, values: Entity[]) => {
  const select = document.getElementById(id) as HTMLSelectElement
  select.innerHTML = values.map(toOption).join('')
}

const reloadBaseData = async () => {
  const [students, courses, disciplines, classrooms] = await Promise.all([
    request('/students'),
    request('/courses'),
    request('/disciplines'),
    request('/classrooms'),
  ])

  renderSelect('disciplineCourse', courses)
  renderSelect('classroomDiscipline', disciplines)
  renderSelect('enrollmentStudent', students)
  renderSelect('enrollmentClassroom', classrooms.map((c: any) => ({ id: c.id, name: c.id })))
}

const reloadEnrollments = async () => {
  const classrooms = await request('/classrooms')
  const list = document.getElementById('enrollmentList') as HTMLUListElement
  const all: any[] = []

  for (const classroom of classrooms) {
    const rows = await request(`/enrollments/by-classroom/${classroom.id}`)
    all.push(...rows)
  }

  list.innerHTML = all.map((e) => `<li>${e.id} - ${e.status}</li>`).join('')
}

const bind = (id: string, listener: () => Promise<void>) => {
  document.getElementById(id)!.addEventListener('click', async () => {
    try {
      await listener()
      result.textContent = 'Operação executada com sucesso.'
    } catch (err) {
      result.textContent = String(err)
    }
  })
}

bind('createStudent', async () => {
  const name = (document.getElementById('studentName') as HTMLInputElement).value
  await request('/students', { method: 'POST', body: JSON.stringify({ name }) })
  await reloadBaseData()
})

bind('createCourse', async () => {
  const name = (document.getElementById('courseName') as HTMLInputElement).value
  await request('/courses', { method: 'POST', body: JSON.stringify({ name }) })
  await reloadBaseData()
})

bind('createDiscipline', async () => {
  const name = (document.getElementById('disciplineName') as HTMLInputElement).value
  const courseId = (document.getElementById('disciplineCourse') as HTMLSelectElement).value
  await request('/disciplines', { method: 'POST', body: JSON.stringify({ name, courseId }) })
  await reloadBaseData()
})

bind('createClassroom', async () => {
  const disciplineId = (document.getElementById('classroomDiscipline') as HTMLSelectElement).value
  const maxSeats = Number((document.getElementById('classroomSeats') as HTMLInputElement).value)
  const enrollmentStart = (document.getElementById('classroomStart') as HTMLInputElement).value
  const enrollmentEnd = (document.getElementById('classroomEnd') as HTMLInputElement).value
  await request('/classrooms', { method: 'POST', body: JSON.stringify({ disciplineId, maxSeats, enrollmentStart, enrollmentEnd }) })
  await reloadBaseData()
})

bind('createEnrollment', async () => {
  const studentId = (document.getElementById('enrollmentStudent') as HTMLSelectElement).value
  const classroomId = (document.getElementById('enrollmentClassroom') as HTMLSelectElement).value
  await request('/enrollments', { method: 'POST', body: JSON.stringify({ studentId, classroomId }) })
  await reloadEnrollments()
})

bind('refreshEnrollments', reloadEnrollments)

const today = new Date().toISOString().slice(0, 10)
;(document.getElementById('classroomStart') as HTMLInputElement).value = today
;(document.getElementById('classroomEnd') as HTMLInputElement).value = today

reloadBaseData().catch((err) => (result.textContent = String(err)))
