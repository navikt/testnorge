import React from 'react'

import './SoekMiniNorge.less'

import { Search } from './search/Search'
import Title from '~/components/Title'

export default () => (
	<div>
		<Title beta={true} title="Søk i Mini-Norge" />
		<Search />
	</div>
)
