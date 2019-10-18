import React, { PureComponent, Fragment } from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import SendFoedselsmelding from './SendFoedselsmelding'
import SendDoedsmelding from './SendDoedsmelding'
import './TpsEndring.less'

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
