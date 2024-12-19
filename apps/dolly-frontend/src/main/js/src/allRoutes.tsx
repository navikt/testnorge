import React from 'react'
import { Navigate } from 'react-router'
import GruppeConnector from './pages/gruppe/GruppeConnector'
import { lazyWithPreload } from './utils/lazyWithPreload'

const OrganisasjonTenorSoekPage = lazyWithPreload(
	() => import('@/pages/organisasjoner/OrganisasjonTenorSoek/OrganisasjonTenorSoekPage'),
)
const NyansettelserPage = lazyWithPreload(() => import('@/pages/nyansettelser/NyansettelserPage'))
const GruppeOversikt = lazyWithPreload(
	() => import('@/pages/gruppeOversikt/GruppeOversiktConnector'),
)
const Organisasjon = lazyWithPreload(() => import('@/pages/organisasjoner/Organisasjoner'))
const BestillingsveilederConnector = lazyWithPreload(
	() => import('@/components/bestillingsveileder/BestillingsveilederConnector'),
)
const MinSide = lazyWithPreload(() => import('@/pages/minSide/MinSide'))
const UI = lazyWithPreload(() => import('@/pages/ui/index'))
const TestnorgePage = lazyWithPreload(() => import('@/pages/testnorgePage/index'))
const Endringsmelding = lazyWithPreload(() => import('@/pages/endringsmelding/Endringsmelding'))
const DollySoekPage = lazyWithPreload(() => import('@/pages/dollySoek/DollySoekPage'))
const TenorSoekPage = lazyWithPreload(() => import('@/pages/tenorSoek/TenorSoekPage'))
const OrgtilgangPage = lazyWithPreload(() => import('@/pages/adminPages/Orgtilgang/OrgtilgangPage'))
const LevendeArbeidsforholdPage = lazyWithPreload(
	() => import('@/pages/adminPages/Levendearbeidsforhold/AppstyringPage'),
)

const GruppeBreadcrumb = (props) => <span>Gruppe #{props.match?.params?.gruppeId}</span>

const allRoutes = [
	{ path: '/', breadcrumb: 'Hjem', element: () => <Navigate to="/gruppe" replace /> },
	{ path: '/gruppe', handle: {
			crumb: () => 'Personer',
		},
		element: GruppeOversikt },
	{ path: '/gruppe/:gruppeId', breadcrumb: GruppeBreadcrumb, element: GruppeConnector },
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
	{ path: '/organisasjoner', breadcrumb: 'Organisasjoner', element: Organisasjon },
	{
		path: '/tenor/organisasjoner',
		breadcrumb: 'Søk i Tenor organisasjoner',
		element: OrganisasjonTenorSoekPage,
	},
	{
		path: '/organisasjoner/bestilling',
		breadcrumb: 'Opprett organisasjon',
		element: BestillingsveilederConnector,
	},
	{ path: '/minside', breadcrumb: 'Min side', element: MinSide },
	{ path: '/ui', breadcrumb: 'UI demo', element: UI },
	{ path: '/dollysoek', breadcrumb: 'Søk i Dolly', element: DollySoekPage },
	{ path: '/testnorge', breadcrumb: 'Søk i Test-Norge', element: TestnorgePage },
	{ path: '/tenor/personer', breadcrumb: 'Søk i Tenor personer', element: TenorSoekPage },
	{ path: '/importer', breadcrumb: 'Importer', element: BestillingsveilederConnector },
	{ path: '/endringsmelding', breadcrumb: 'Endringsmelding', element: Endringsmelding },
	{ path: '/admin/orgtilgang', breadcrumb: 'Organisasjon-tilgang', element: OrgtilgangPage },
	{
		path: '/admin/levendearbeidsforhold',
		breadcrumb: 'Levende-arbeidsforhold',
		element: LevendeArbeidsforholdPage,
	},
	{ path: '/nyansettelser', breadcrumb: 'Nyansettelser', element: NyansettelserPage },
]

export const preloadComponentOnRoute = (path: string) => {
	const matchingRoute: any = allRoutes.find((route) => route.path === path)

	matchingRoute?.element?.preload?.()
}

export default allRoutes
