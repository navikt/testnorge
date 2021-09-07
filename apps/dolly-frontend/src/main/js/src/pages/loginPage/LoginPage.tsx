import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import LoginModal from '~/pages/loginPage/LoginModal'
import './LoginPage.less'
import { SummerBackground } from '~/pages/loginPage/Background'

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			<SummerBackground>
				<LoginModal />
			</SummerBackground>
		</React.Fragment>
	)
}
