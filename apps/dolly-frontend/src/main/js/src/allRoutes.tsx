import React, { lazy } from 'react'
import { Navigate } from 'react-router-dom'
import GruppeConnector from '@/pages/gruppe/GruppeConnector'

const GruppeOversikt = lazy(() => import('@/pages/gruppeOversikt/GruppeOversiktConnector'))
const Organisasjon = lazy(() => import('@/pages/organisasjoner/OrganisasjonerConnector'))
const BestillingsveilederConnector = lazy(
	() => import('@/components/bestillingsveileder/BestillingsveilederConnector')
)
const MinSide = lazy(() => import('@/pages/minSide/MinSide'))
const UI = lazy(() => import('@/pages/ui/index'))
const TestnorgePage = lazy(() => import('@/pages/testnorgePage/index'))
const Endringsmelding = lazy(() => import('@/pages/endringsmelding/Endringsmelding'))

const GruppeBreadcrumb = (props) => <span>Gruppe #{props.match?.params?.gruppeId}</span>

const allRoutes = [
	{ path: '/', breadcrumb: 'Hjem', element: () => <Navigate to="/gruppe" replace /> },
	{ path: '/gruppe', breadcrumb: 'Personer', element: () => <GruppeOversikt /> },
	{ path: '/gruppe/:gruppeId', breadcrumb: GruppeBreadcrumb, element: () => <GruppeConnector /> },
	{
		path: '/gruppe/:gruppeId/bestilling/:personId',
		breadcrumb: 'Legg til/endre',
		element: () => <BestillingsveilederConnector />,
	},
	{
		path: '/gruppe/:gruppeId/bestilling',
		breadcrumb: 'Opprett personer',
		element: () => <BestillingsveilederConnector />,
	},
	{
		path: '/organisasjoner',
		breadcrumb: 'Organisasjoner',
		element: () => <Organisasjon />,
	},
	{
		path: '/organisasjoner/bestilling',
		breadcrumb: 'Opprett organisasjon',
		element: () => <BestillingsveilederConnector />,
	},
	{ path: '/minside', breadcrumb: 'Min side', element: () => <MinSide /> },
	{ path: '/ui', breadcrumb: 'UI demo', element: () => <UI /> },
	{ path: '/testnorge', breadcrumb: 'Test-Norge', element: () => <TestnorgePage /> },
	{
		path: '/importer',
		breadcrumb: 'Importer',
		element: () => <BestillingsveilederConnector />,
	},
	{
		path: '/endringsmelding',
		breadcrumb: 'Endringsmelding',
		element: () => <Endringsmelding />,
	},
]

export default allRoutes
