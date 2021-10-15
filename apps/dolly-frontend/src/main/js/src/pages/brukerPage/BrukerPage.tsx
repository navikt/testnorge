import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukerModal from '~/pages/brukerPage/BrukerModel'
import './BrukerPage.less'
import { HalloweenBackground } from '~/components/ui/background/Background'

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			<HalloweenBackground>
				<BrukerModal />
			</HalloweenBackground>
		</React.Fragment>
	)
}
