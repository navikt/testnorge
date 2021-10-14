import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import LoginModal from '~/pages/loginPage/LoginModal'
import './LoginPage.less'
import { HalloweenBackground } from '~/components/ui/background/Background'

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			<HalloweenBackground>
				<LoginModal />
			</HalloweenBackground>
		</React.Fragment>
	)
}
