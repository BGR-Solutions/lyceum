import './style.css'
import { renderHome } from './pages/home'
import { renderCadastros } from './pages/cadastros'
import { renderAluno } from './pages/aluno'

const app = document.querySelector<HTMLDivElement>('#app')!

const navigate = async () => {
  const hash = window.location.hash || '#/'

  if (hash.startsWith('#/cadastros')) {
    renderCadastros(app)
  } else if (hash.startsWith('#/aluno')) {
    await renderAluno(app)
  } else {
    renderHome(app)
  }
}

window.addEventListener('hashchange', navigate)
navigate()
