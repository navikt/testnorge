import React from 'react'
import { Navigate } from 'react-router'
import GruppeConnector from './pages/gruppe/GruppeConnector'
import { lazyWithPreload } from './utils/lazyWithPreload'
import { Bestillingsveileder } from '@/components/bestillingsveileder/Bestillingsveileder'

const OrganisasjonTenorSoekPage = lazyWithPreload(
	() => import('@/pages/organisasjoner/OrganisasjonTenorSoek/OrganisasjonTenorSoekPage'),
)
const NyansettelserPage = lazyWithPreload(() => import('@/pages/nyansettelser/NyansettelserPage'))
const GruppeOversikt = lazyWithPreload(() => import('@/pages/gruppeOversikt/GruppeOversikt'))
const Organisasjon = lazyWithPreload(() => import('@/pages/organisasjoner/Organisasjoner'))
const MinSide = lazyWithPreload(() => import('@/pages/minSide/MinSide'))
const Endringsmelding = lazyWithPreload(() => import('@/pages/endringsmelding/Endringsmelding'))
const DollySoekPage = lazyWithPreload(() => import('@/pages/dollySoek/DollySoekPage'))
const TenorSoekPage = lazyWithPreload(() => import('@/pages/tenorSoek/TenorSoekPage'))
const OrgtilgangPage = lazyWithPreload(() => import('@/pages/adminPages/Orgtilgang/OrgtilgangPage'))
const LevendeArbeidsforholdPage = lazyWithPreload(
	() => import('@/pages/adminPages/Levendearbeidsforhold/AppstyringPage'),
)
const InfostripePage = lazyWithPreload(
	() => import('@/pages/adminPages/dollyInfostriper/DollyInfostripePage'),
)
const ApiOversiktPage = lazyWithPreload(() => import('@/pages/apiOversikt/ApiOversiktPage'))
const TeamOversiktPage = lazyWithPreload(() => import('@/pages/teamOversikt/TeamOversiktPage'))

const GruppeBreadcrumb = (props: any) => <span>Gruppe #{props.params?.gruppeId}</span>

const allRoutes = [
	{
		path: '/',
		handle: {
			crumb: () => 'Hjem',
		},
		element: () => <Navigate to="/gruppe" />,
	},
	{
		path: '/gruppe',
		handle: {
			crumb: () => 'Personer',
		},
		element: GruppeOversikt,
	},
	{
		path: '/gruppe/:gruppeId',
		handle: {
			crumb: GruppeBreadcrumb,
		},
		element: GruppeConnector,
	},
	{
		path: '/gruppe/:gruppeId/bestilling/:personId',
		handle: {
			crumb: () => 'Legg til/endre',
		},
		element: Bestillingsveileder,
	},
	{
		path: '/gruppe/:gruppeId/bestilling',
		handle: {
			crumb: () => 'Opprett personer',
		},
		element: Bestillingsveileder,
	},
	{
		path: '/organisasjoner',
		handle: {
			crumb: () => 'Organisasjoner',
		},
		element: Organisasjon,
	},
	{
		path: '/tenor/organisasjoner',
		handle: {
			crumb: () => 'Søk etter organisasjoner i Tenor',
		},
		element: OrganisasjonTenorSoekPage,
	},
	{
		path: '/organisasjoner/bestilling',
		handle: {
			crumb: () => 'Opprett organisasjon',
		},
		element: Bestillingsveileder,
	},
	{ path: '/minside', handle: { crumb: () => 'Min side' }, element: MinSide },
	{
		path: '/dollysoek',
		handle: { crumb: () => 'Søk i Dolly' },
		element: DollySoekPage,
	},
	{
		path: '/tenor',
		handle: { crumb: () => 'Tenor' },
		element: () => <Navigate to="/tenor/personer" />,
	},
	{
		path: '/tenor/personer',
		handle: { crumb: () => 'Søk etter personer i Tenor' },
		element: TenorSoekPage,
	},
	{
		path: '/importer',
		handle: { crumb: () => 'Importer' },
		element: Bestillingsveileder,
	},
	{
		path: '/endringsmelding',
		handle: { crumb: () => 'Endringsmelding' },
		element: Endringsmelding,
	},
	{
		path: '/admin/orgtilgang',
		handle: { crumb: () => 'Organisasjon-tilgang' },
		element: OrgtilgangPage,
	},
	{
		path: '/admin/levendearbeidsforhold',
		handle: { crumb: () => 'Levende arbeidsforhold' },
		element: LevendeArbeidsforholdPage,
	},
	{
		path: '/admin/infostriper',
		handle: { crumb: () => 'Dolly infostriper' },
		element: InfostripePage,
	},
	{
		path: '/nyansettelser',
		handle: { crumb: () => 'Nyansettelser' },
		element: NyansettelserPage,
	},
	{
		path: '/oversikt',
		handle: { crumb: () => 'API-oversikt' },
		element: ApiOversiktPage,
	},
	{
		path: '/team',
		handle: { crumb: () => 'Team-oversikt' },
		element: TeamOversiktPage,
	},
]

export const preloadComponentOnRoute = (path: string) => {
	const matchingRoute: any = allRoutes.find((route) => route.path === path)

	matchingRoute?.element?.preload?.()
}

export default allRoutes
