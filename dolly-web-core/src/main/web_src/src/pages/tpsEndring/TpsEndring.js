import React, { PureComponent, Fragment } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import './TpsEndring.less'
import SendFoedselsmelding from './SendFoedselsmelding'
import SendDoedsmelding from './SendDoedsmelding'

export default class TPSEndring extends PureComponent {
	render() {
		return (
			<Fragment>
				<Overskrift label={'Send Endringsmelding'} />
				<SendFoedselsmelding />
				<SendDoedsmelding />
			</Fragment>
		)
	}
}
