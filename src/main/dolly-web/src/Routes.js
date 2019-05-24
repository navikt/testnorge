import React from 'react'
import Gruppe from './pages/gruppe/GruppeConnector'
import GruppeOversikt from './pages/gruppeOversikt/GruppeOversiktConnector'
import Bestilling from './pages/bestilling/BestillingConnector'
import Profil from './pages/profil/ProfilConnector'
import TeamOversiktConnector from './pages/teamOversikt/TeamOversiktConnector'
import TeamConnector from './pages/team/TeamConnector'
import RedigerTestbrukerConnector from './pages/redigerTestbruker/RedigerTestbrukerConnector'
import TPSEndring from './pages/tpsEndring/TpsEndring'

const GruppeBreadcrumb = props => <span>Gruppe #{props.match.params.gruppeId}</span>
const TeamBreadcrumb = props => <span>Team #{props.match.params.teamId}</span>

const routes = [
	{ path: '/', exact: true, breadcrumb: 'Testdatagrupper', component: GruppeOversikt },
	{ path: '/profil', exact: true, breadcrumb: 'Min profil', component: Profil },
	{ path: '/team', exact: true, breadcrumb: 'Team oversikt', component: TeamOversiktConnector },
	{ path: '/team/:teamId', exact: true, breadcrumb: TeamBreadcrumb, component: TeamConnector },
	{ path: '/gruppe/:gruppeId', exact: true, breadcrumb: GruppeBreadcrumb, component: Gruppe },
	{
		path: '/gruppe/:gruppeId/bestilling',
		exact: true,
		breadcrumb: 'Legg til testpersoner',
		component: Bestilling
	},
	{
		path: '/gruppe/:gruppeId/testbruker/:ident&:datasources',
		exact: true,
		breadcrumb: 'Rediger',
		component: RedigerTestbrukerConnector
	},
	{ path: '/tpsendring', exact: true, breadcrumb: 'TPSEndring', component: TPSEndring }
]

export default routes
