import React, { lazy } from 'react'

const Gruppe = lazy(() => import('./pages/gruppe/GruppeConnector'))
const GruppeOversikt = lazy(() => import('./pages/gruppeOversikt/GruppeOversiktConnector'))
const BestillingsveilederConnector = lazy(() =>
	import('./components/bestillingsveileder/BestillingsveilederConnector')
)
const MinSide = lazy(() => import('./pages/minSide/MinSideConnector'))
const UI = lazy(() => import('./pages/ui/index'))
const SoekMiniNorge = lazy(() => import('./pages/soekMiniNorge/SoekMiniNorge'))

const GruppeBreadcrumb = props => <span>Gruppe #{props.match.params.gruppeId}</span>

const routes = [
	{ path: '/', exact: true, breadcrumb: 'Testdatagrupper', component: GruppeOversikt },
	{ path: '/gruppe/:gruppeId', exact: true, breadcrumb: GruppeBreadcrumb, component: Gruppe },
	{
		path: '/gruppe/:gruppeId/bestilling/:personId',
		exact: true,
		breadcrumb: 'Legg til/endre',
		component: BestillingsveilederConnector
	},
	{
		path: '/gruppe/:gruppeId/bestilling',
		exact: true,
		breadcrumb: 'Opprett personer',
		component: BestillingsveilederConnector
	},
	{ path: '/minside', exact: true, breadcrumb: 'Min side', component: MinSide },
	{ path: '/ui', exact: true, breadcrumb: 'UI demo', component: UI },
	{ path: '/soek', exact: true, breadcrumb: 'Søk i Mini-Norge', component: SoekMiniNorge }
]

export default routes
