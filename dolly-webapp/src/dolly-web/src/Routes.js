import React from 'react'
import { Route } from 'react-router-dom'
import { Switch } from 'react-router-dom'
import Gruppe from './components/gruppe/GruppeConnector'
import Home from './components/home/HomeContainer'

const Routes = () => (
	<Switch>
		<Route exact path="/" component={Home} />
		<Route exact path="/gruppe/:gruppeId" component={Gruppe} />
	</Switch>
)

export default Routes
