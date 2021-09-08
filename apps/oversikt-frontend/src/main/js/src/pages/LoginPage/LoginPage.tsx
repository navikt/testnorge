import React from 'react'
import LoginModal from './LoginModal'
import './LoginPage.less'
import { HalloweenBackground as Background } from './Background'

export default () => {
	return (
		<React.Fragment>
			<Background>
				<LoginModal />
			</Background>
		</React.Fragment>
	)
}
