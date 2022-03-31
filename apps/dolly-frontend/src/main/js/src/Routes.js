import React, { lazy } from 'react'

const Gruppe = lazy(() => import('./pages/gruppe/GruppeConnector'))
const GruppeOversikt = lazy(() => import('./pages/gruppeOversikt/GruppeOversiktConnector'))
const Organisasjon = lazy(() => import('./pages/organisasjoner/OrganisasjonerConnector'))
const BestillingsveilederConnector = lazy(() =>
	import('./components/bestillingsveileder/BestillingsveilederConnector')
)
const MinSide = lazy(() => import('./pages/minSide/MinSideConnector'))
const UI = lazy(() => import('./pages/ui/index'))
const TestnorgePage = lazy(() => import('./pages/testnorgePage/TestnorgePage'))

const GruppeBreadcrumb = (props) => <span>Gruppe #{props.match.params.gruppeId}</span>

const routes = [
	{ path: '/', exact: true, breadcrumb: 'Personer', component: GruppeOversikt },
	{ path: '/gruppe/:gruppeId', exact: true, breadcrumb: GruppeBreadcrumb, component: Gruppe },
	{
		path: '/gruppe/:gruppeId/bestilling/:personId',
		exact: true,
		breadcrumb: 'Legg til/endre',
		component: BestillingsveilederConnector,
	},
	{
		path: '/gruppe/:gruppeId/bestilling',
		exact: true,
		breadcrumb: 'Opprett personer',
		component: BestillingsveilederConnector,
	},
	{
		path: '/organisasjoner',
		exact: true,
		breadcrumb: 'Organisasjoner',
		component: Organisasjon,
	},
	{
		path: '/organisasjoner/bestilling',
		exact: true,
		breadcrumb: 'Opprett organisasjon',
		component: BestillingsveilederConnector,
	},
	{ path: '/minside', exact: true, breadcrumb: 'Min side', component: MinSide },
	{ path: '/ui', exact: true, breadcrumb: 'UI demo', component: UI },
	{ path: '/testnorge', exact: true, breadcrumb: 'Testnorge', component: TestnorgePage },
	{
		path: '/importer',
		exact: true,
		breadcrumb: 'Importer',
		component: BestillingsveilederConnector,
	},
]

export default routes
