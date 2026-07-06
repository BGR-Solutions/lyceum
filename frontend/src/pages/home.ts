export function renderHome(container: HTMLElement): void {
  container.innerHTML = `
    <div class="home">
      <h1>Lyceum - Gestão Acadêmica</h1>
      <p class="hint">Selecione uma área para continuar:</p>
      <div class="home-cards">
        <a href="#/cadastros" class="card">
          <span class="card-icon">📋</span>
          <h2>Perfil de Cadastros</h2>
          <p>Gerencie alunos, cursos, disciplinas, turmas e matrículas.</p>
        </a>
        <a href="#/aluno" class="card">
          <span class="card-icon">🎓</span>
          <h2>Perfil de Aluno</h2>
          <p>Busque disciplinas e realize matrículas como aluno.</p>
        </a>
      </div>
    </div>
  `
}
