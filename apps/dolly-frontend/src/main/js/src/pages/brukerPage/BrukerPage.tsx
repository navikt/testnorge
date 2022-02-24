import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukerModal from '~/pages/brukerPage/BrukerModel'
import './BrukerPage.less'
import { SpringBackground as Background } from '~/components/ui/background/Background'

import './../../flowers.scss'

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			{Array.from(Array(50).keys()).map(() => (
				<div className="flower" />
			))}
			<Background>
				<BrukerModal />
			</Background>
		</React.Fragment>
	)
}
