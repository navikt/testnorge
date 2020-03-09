import React from 'react'
import Gruppe from './pages/gruppe/GruppeConnector'
import GruppeOversikt from './pages/gruppeOversikt/GruppeOversiktConnector'
import TPSEndring from './pages/tpsEndring/TpsEndring'
import BestillingsveilederConnector from './components/bestillingsveileder/BestillingsveilederConnector'
import MinSide from './pages/minSide/MinSideConnector'
import UI from './pages/ui/index'

const GruppeBreadcrumb = props => <span>Gruppe #{props.match.params.gruppeId}</span>

const routes = [
	{ path: '/', exact: true, breadcrumb: 'Testdatagrupper', component: GruppeOversikt },
	{ path: '/gruppe/:gruppeId', exact: true, breadcrumb: GruppeBreadcrumb, component: Gruppe },
	{
		path: '/gruppe/:gruppeId/bestilling',
		exact: true,
		breadcrumb: 'Legg til personer',
		component: BestillingsveilederConnector
	},
	{ path: '/tpsendring', exact: true, breadcrumb: 'TPSEndring', component: TPSEndring },
	{ path: '/minside', exact: true, breadcrumb: 'Min side', component: MinSide },
	{ path: '/ui', exact: true, breadcrumb: 'UI demo', component: UI }
]

export default routes
