import React, { lazy } from 'react'
import { Navigate } from 'react-router'
import GruppeConnector from '@/pages/gruppe/GruppeConnector'
import { OrganisasjonTenorSoekPage } from '@/pages/organisasjoner/OrganisasjonTenorSoek/OrganisasjonTenorSoekPage'
import NyansettelserPage from '@/pages/nyansettelser/NyansettelserPage'

const GruppeOversikt = lazy(() => import('@/pages/gruppeOversikt/GruppeOversiktConnector'))
const Organisasjon = lazy(() => import('@/pages/organisasjoner/Organisasjoner'))
const BestillingsveilederConnector = lazy(
	() => import('@/components/bestillingsveileder/BestillingsveilederConnector'),
)
const MinSide = lazy(() => import('@/pages/minSide/MinSide'))
const UI = lazy(() => import('@/pages/ui/index'))
const TestnorgePage = lazy(() => import('@/pages/testnorgePage/index'))
const Endringsmelding = lazy(() => import('@/pages/endringsmelding/Endringsmelding'))
const DollySoekPage = lazy(() => import('@/pages/dollySoek/DollySoekPage'))
const TenorSoekPage = lazy(() => import('@/pages/tenorSoek/TenorSoekPage'))
const OrgtilgangPage = lazy(() => import('@/pages/adminPages/Orgtilgang/OrgtilgangPage'))
const LevendeArbeidsforholdPage = lazy(
	() => import('@/pages/adminPages/Levendearbeidsforhold/AppstyringPage'),
)

const GruppeBreadcrumb = (props) => <span>Gruppe #{props.match?.params?.gruppeId}</span>

const allRoutes = [
	{ path: '/', breadcrumb: 'Hjem', element: () => <Navigate to="/gruppe" replace /> },
	{
		path: '/gruppe',
		handle: {
			crumb: () => 'Personer',
		},
		element: () => <GruppeOversikt />,
	},
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
		path: '/tenor/organisasjoner',
		breadcrumb: 'Søk i Tenor organisasjoner',
		element: () => <OrganisasjonTenorSoekPage />,
	},
	{
		path: '/organisasjoner/bestilling',
		breadcrumb: 'Opprett organisasjon',
		element: () => <BestillingsveilederConnector />,
	},
	{ path: '/minside', breadcrumb: 'Min side', element: () => <MinSide /> },
	{ path: '/ui', breadcrumb: 'UI demo', element: () => <UI /> },
	{ path: '/dollysoek', breadcrumb: 'Søk i Dolly', element: () => <DollySoekPage /> },
	{ path: '/testnorge', breadcrumb: 'Søk i Test-Norge', element: () => <TestnorgePage /> },
	{ path: '/tenor/personer', breadcrumb: 'Søk i Tenor personer', element: () => <TenorSoekPage /> },
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
	{
		path: '/admin/orgtilgang',
		breadcrumb: 'Organisasjon-tilgang',
		element: () => <OrgtilgangPage />,
	},
	{
		path: '/admin/levendearbeidsforhold',
		breadcrumb: 'Levende-arbeidsforhold',
		element: () => <LevendeArbeidsforholdPage />,
	},
	{
		path: '/nyansettelser',
		breadcrumb: 'Nyansettelser',
		element: () => <NyansettelserPage />,
	},
]

export default allRoutes
