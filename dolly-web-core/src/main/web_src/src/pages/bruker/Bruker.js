import React from 'react'
import Maler from './maler/Maler'

import './Bruker.less'

export default ({ brukerId }) => {
	return <Maler brukerId={brukerId} />
}
