import React from 'react'
import { Route } from 'react-router-dom'
import { Switch } from 'react-router-dom'
import Gruppe from './pages/gruppe/GruppeConnector'
import GruppeOversikt from './pages/gruppeOversikt/GruppeOversiktConnector'
import Oppskrift from './pages/oppskrift/Oppskrift'

const Routes = () => (
	<Switch>
		<Route exact path="/" component={GruppeOversikt} />
		<Route exact path="/gruppe/:gruppeId" component={Gruppe} />
		<Route exact path="/gruppe/:gruppeId/oppskrift" component={Oppskrift} />
	</Switch>
)

export default Routes
