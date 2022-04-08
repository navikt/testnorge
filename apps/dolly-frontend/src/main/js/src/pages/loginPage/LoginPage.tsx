import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import LoginModal from '~/pages/loginPage/LoginModal'
import './LoginPage.less'
import { PaaskeBackground as Background } from '~/components/ui/background/Background'

import './../../snow.scss'

export default () => {
	return (
		<>
			<BlankHeader />
			{Array.from(Array(15).keys()).map(() => (
				<div className="snowflake" />
			))}
			<Background>
				<LoginModal />
			</Background>
		</>
	)
}
