import { NavLink } from 'react-router-dom'
import Icon from '@/components/ui/icon/Icon'
// @ts-ignore
import logo from '@/assets/img/nav-logo-hvit.png'
import './Header.less'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import Loading from '@/components/ui/loading/Loading'
import { BrukerDropdown } from '@/components/layout/header/BrukerDropdown'
import { DokumentasjonDropdown } from '@/components/layout/header/DokumentasjonDropdown'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { FinnPersonDropdown } from '@/components/layout/header/FinnPersonDropdown'
import { OrganisasjonDropdown } from '@/components/layout/header/OrganisasjonDropdown'
import { AdminDropdown } from '@/components/layout/header/AdminDropdown'

export default () => {
	const { currentBruker, loading } = useCurrentBruker()

	if (loading) {
		return <Loading label="Laster bruker" panel />
	}

	const bankidBruker = currentBruker?.brukertype === 'BANKID'
	const erAdmin = true //TODO: Implementer sjekk for admin

	return (
		<header className="app-header">
			<NavLink to="/" end className="home-nav">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<Icon size={30} kind="dolly" className="dollysheep" />
				<h1>Dolly</h1>
			</NavLink>

			<div className="menu-links">
				<NavLink data-testid={TestComponentSelectors.BUTTON_HEADER_PERSONER} to="/gruppe">
					Personer
				</NavLink>
				<FinnPersonDropdown />
				<OrganisasjonDropdown />
				{!bankidBruker && (
					<NavLink
						data-testid={TestComponentSelectors.BUTTON_HEADER_ENDRINGSMELDING}
						to="/endringsmelding"
					>
						Endringsmelding
					</NavLink>
				)}
				<DokumentasjonDropdown />
				{erAdmin && <AdminDropdown />}
			</div>
			<BrukerDropdown />
		</header>
	)
}
