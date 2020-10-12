import React from 'react'
import Maler from './maler/Maloversikt'
import GruppeImport from './GruppeImport'

import './MinSide.less'

export default ({ brukerId }) => {
	return (
		<>
			<h1>Min side</h1>
			<GruppeImport />
			<Maler brukerId={brukerId} />
		</>
	)
}
