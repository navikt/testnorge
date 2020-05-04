import React from 'react'
import Gruppe from './pages/gruppe/GruppeConnector'
import GruppeOversikt from './pages/gruppeOversikt/GruppeOversiktConnector'
import TPSEndring from './pages/tpsEndring/TpsEndring'
import BestillingsveilederConnector from './components/bestillingsveileder/BestillingsveilederConnector'
import MinSide from './pages/minSide/MinSideConnector'
import UI from './pages/ui/index'
import SoekMiniNorge from '~/pages/soekMiniNorge/SoekMiniNorge'
// import InntektStubPage from './pages/inntektStubPage/InntektStubPage'

const GruppeBreadcrumb = props => <span>Gruppe #{props.match.params.gruppeId}</span>

const routes = [
	{ path: '/', exact: true, breadcrumb: 'Testdatagrupper', component: GruppeOversikt },
	{ path: '/gruppe/:gruppeId', exact: true, breadcrumb: GruppeBreadcrumb, component: Gruppe },
	{
		path: '/gruppe/:gruppeId/bestilling/:personId',
		exact: true,
		breadcrumb: 'Legg til på personer',
		component: BestillingsveilederConnector
	},
	{
		path: '/gruppe/:gruppeId/bestilling',
		exact: true,
		breadcrumb: 'Opprett personer',
		component: BestillingsveilederConnector
	},
	{ path: '/tpsendring', exact: true, breadcrumb: 'TPSEndring', component: TPSEndring },
	{ path: '/minside', exact: true, breadcrumb: 'Min side', component: MinSide },
	{ path: '/ui', exact: true, breadcrumb: 'UI demo', component: UI },
	{ path: '/soek', exact: true, breadcrumb: 'Søk i Mini-Norge', component: SoekMiniNorge}
	// { path: '/inntektstub', exact: true, breadcrumb: 'Inntektstub', component: InntektStubPage }
]

export default routes
