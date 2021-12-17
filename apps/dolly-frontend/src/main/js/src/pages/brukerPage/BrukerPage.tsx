import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukerModal from '~/pages/brukerPage/BrukerModel'
import './BrukerPage.less'
import { ChristmasBackground as Background } from '~/components/ui/background/Background'

import './../../snow.scss'

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			{Array.from(Array(50).keys()).map(() => (
				<div className="snowflake" />
			))}
			<Background>
				<BrukerModal />
			</Background>
		</React.Fragment>
	)
}
