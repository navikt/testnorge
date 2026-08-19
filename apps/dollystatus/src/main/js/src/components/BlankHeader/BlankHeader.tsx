import logo from '@/assets/img/nav-logo-hvit.png'
import './BlankHeader.less'

export default () => {
	return (
		<header className="blank-header">
			<div className="home-nav">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<h1>Dolly</h1>
			</div>
			<div className="menu-links">
				<a href="https://navikt.github.io/testnorge/" target="_blank" rel="noreferrer">
					Dokumentasjon
				</a>
			</div>
		</header>
	)
}
