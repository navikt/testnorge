import React from 'react'
import Maler from './maler/Maloversikt'

import './MinSide.less'

export default ({ brukerId }) => {
	return (
		<>
			<h1>Min side</h1>
			<Maler brukerId={brukerId} />
		</>
	)
}
