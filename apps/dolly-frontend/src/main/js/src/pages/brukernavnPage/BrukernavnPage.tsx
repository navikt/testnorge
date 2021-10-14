import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukernavnModal from '~/pages/brukernavnPage/BrukernavnModel'
import './BrukernavnPage.less'
import { HalloweenBackground } from '~/components/ui/background/Background'

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			<HalloweenBackground>
				<BrukernavnModal />
			</HalloweenBackground>
		</React.Fragment>
	)
}
