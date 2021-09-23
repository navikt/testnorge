import React from 'react'
import LoginModal from './LoginModal'
import './LoginPage.less'
import { SummerBackground as Background } from './Background'
import { Page } from '@/pages/Page'

export default () => {
	return (
		<Background>
			<Page loggedIn={false}>
				<LoginModal />
			</Page>
		</Background>
	)
}
