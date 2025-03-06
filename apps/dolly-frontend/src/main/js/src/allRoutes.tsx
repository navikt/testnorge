import React from 'react'
import { Navigate } from 'react-router'
import GruppeConnector from './pages/gruppe/GruppeConnector'
import { lazyWithPreload } from './utils/lazyWithPreload'
import { Bestillingsveileder } from '@/components/bestillingsveileder/Bestillingsveileder'

const OrganisasjonTenorSoekPage = lazyWithPreload(
	() => import('@/pages/organisasjoner/OrganisasjonTenorSoek/OrganisasjonTenorSoekPage'),
)
const NyansettelserPage = lazyWithPreload(() => import('@/pages/nyansettelser/NyansettelserPage'))
const GruppeOversikt = lazyWithPreload(
	() => import('@/pages/gruppeOversikt/GruppeOversiktConnector'),
)
const Organisasjon = lazyWithPreload(() => import('@/pages/organisasjoner/Organisasjoner'))
const MinSide = lazyWithPreload(() => import('@/pages/minSide/MinSide'))
const UI = lazyWithPreload(() => import('@/pages/ui/index'))
const Endringsmelding = lazyWithPreload(() => import('@/pages/endringsmelding/Endringsmelding'))
const DollySoekPage = lazyWithPreload(() => import('@/pages/dollySoek/DollySoekPage'))
const TenorSoekPage = lazyWithPreload(() => import('@/pages/tenorSoek/TenorSoekPage'))
const OrgtilgangPage = lazyWithPreload(() => import('@/pages/adminPages/Orgtilgang/OrgtilgangPage'))
const LevendeArbeidsforholdPage = lazyWithPreload(
	() => import('@/pages/adminPages/Levendearbeidsforhold/AppstyringPage'),
)

const GruppeBreadcrumb = (props) => <span>Gruppe #{props.params?.gruppeId}</span>

const allRoutes = [
	{
		path: '/',
		handle: {
			crumb: () => 'Hjem',
		},
		element: () => <Navigate to="/gruppe" replace />,
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
	{ path: '/ui', handle: { crumb: () => 'UI demo' }, element: UI },
	{
		path: '/dollysoek',
		handle: { crumb: () => 'Søk i Dolly' },
		element: DollySoekPage,
	},
	{
		path: '/tenor',
		handle: { crumb: () => 'Tenor' },
		element: () => <Navigate to="/tenor/personer" replace />,
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
		handle: { crumb: () => 'Levende-arbeidsforhold' },
		element: LevendeArbeidsforholdPage,
	},
	{
		path: '/nyansettelser',
		handle: { crumb: () => 'Nyansettelser' },
		element: NyansettelserPage,
	},
]

export const preloadComponentOnRoute = (path: string) => {
	const matchingRoute: any = allRoutes.find((route) => route.path === path)

	matchingRoute?.element?.preload?.()
}

export default allRoutes
