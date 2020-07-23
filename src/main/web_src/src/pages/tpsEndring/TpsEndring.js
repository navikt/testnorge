import React, { PureComponent, Fragment } from 'react'
import SendFoedselsmelding from './SendFoedselsmelding'
import SendDoedsmelding from './SendDoedsmelding'
import './TpsEndring.less'

export default class TPSEndring extends PureComponent {
	render() {
		return (
			<Fragment>
				<h1>Send Endringsmelding</h1>
				<SendFoedselsmelding />
				<SendDoedsmelding />
			</Fragment>
		)
	}
}
