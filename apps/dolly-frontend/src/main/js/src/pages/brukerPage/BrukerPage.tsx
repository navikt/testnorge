import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukerModal from '~/pages/brukerPage/BrukerModel'
import './BrukerPage.less'
import { SnowingBackground } from '~/components/ui/background/Background'

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			<SnowingBackground>
				<BrukerModal />
			</SnowingBackground>
		</React.Fragment>
	)
}
