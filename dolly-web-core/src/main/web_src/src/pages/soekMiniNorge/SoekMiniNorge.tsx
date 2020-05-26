import React, { PureComponent } from 'react'

import './SoekMiniNorge.less'

import { Search } from './search/Search'

export default class SoekMiniNorge extends PureComponent {
	render() {
		return (
			<div className="soek-page">
				<h1 className="soek-page__header">Søk i Mini-Norge</h1>
				<h3 className="soek-page__header">(beta)</h3>
				<Search />
			</div>
		)
	}
}
