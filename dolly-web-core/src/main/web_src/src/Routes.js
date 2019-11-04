import React from 'react'
import Gruppe from './pages/gruppe/GruppeConnector'
import GruppeOversikt from './pages/gruppeOversikt/GruppeOversiktConnector'
import Bestilling from './pages/bestilling/BestillingConnector'
import RedigerTestbrukerConnector from './pages/redigerTestbruker/RedigerTestbrukerConnector'
import TPSEndring from './pages/tpsEndring/TpsEndring'

const GruppeBreadcrumb = props => <span>Gruppe #{props.match.params.gruppeId}</span>

const routes = [
	{ path: '/', exact: true, breadcrumb: 'Testdatagrupper', component: GruppeOversikt },
	{ path: '/gruppe/:gruppeId', exact: true, breadcrumb: GruppeBreadcrumb, component: Gruppe },
	{
		path: '/gruppe/:gruppeId/bestilling',
		exact: true,
		breadcrumb: 'Legg til testpersoner',
		component: Bestilling
	},
	{
		path: '/gruppe/:gruppeId/testbruker/:ident',
		exact: true,
		breadcrumb: 'Rediger',
		component: RedigerTestbrukerConnector
	},
	{ path: '/tpsendring', exact: true, breadcrumb: 'TPSEndring', component: TPSEndring }
]

export default routes
