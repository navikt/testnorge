import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukerModal from '~/pages/brukerPage/BrukerModel'
import './BrukerPage.less'
import { Background } from '~/components/ui/background/Background'

export default () => {
	return (
		<>
			<BlankHeader />
			<Background>
				<BrukerModal />
			</Background>
		</>
	)
}
