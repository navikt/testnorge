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

const GruppeBreadcrumb = (props) => <span>Gruppe #{props.match?.params?.gruppeId}</span>

const allRoutes = [
	{ path: '/', breadcrumb: 'Hjem', element: GruppeOversikt },
	{ path: '/gruppe', breadcrumb: 'Personer', element: GruppeOversikt },
	{ path: '/gruppe/:gruppeId', breadcrumb: GruppeBreadcrumb, element: Gruppe },
	{
		path: '/gruppe/:gruppeId/bestilling/:personId',
		breadcrumb: 'Legg til/endre',
		element: BestillingsveilederConnector,
	},
	{
		path: '/gruppe/:gruppeId/bestilling',
		breadcrumb: 'Opprett personer',
		element: BestillingsveilederConnector,
	},
	{
		path: '/organisasjoner',
		breadcrumb: 'Organisasjoner',
		element: Organisasjon,
	},
	{
		path: '/organisasjoner/bestilling',
		breadcrumb: 'Opprett organisasjon',
		element: BestillingsveilederConnector,
	},
	{ path: '/minside', breadcrumb: 'Min side', element: MinSide },
	{ path: '/ui', breadcrumb: 'UI demo', element: UI },
	{ path: '/testnorge', breadcrumb: 'Testnorge', element: TestnorgePage },
	{
		path: '/importer',
		breadcrumb: 'Importer',
		element: BestillingsveilederConnector,
	},
]

export default allRoutes
