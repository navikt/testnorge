import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukerModal from '~/pages/brukerPage/BrukerModel'
import './BrukerPage.less'
import { HalloweenBackground } from '~/components/ui/background/Background'

type BrukerPageProps = {
	brukerData: any
}

export default ({ brukerData }: BrukerPageProps) => {
	if (brukerData !== null) {
		window.location.replace(window.location.protocol + '//' + window.location.host)
	}

	return (
		<React.Fragment>
			<BlankHeader />
			<HalloweenBackground>
				<BrukerModal />
			</HalloweenBackground>
		</React.Fragment>
	)
}
