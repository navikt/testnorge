import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukerModal from '~/pages/brukerPage/BrukerModel'
import './BrukerPage.less'
import { PaaskeBackground as Background } from '~/components/ui/background/Background'

import './../../eggs.scss'

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			{/*{Array.from(Array(50).keys()).map(() => (*/}
			{/*	<div className="egg" />*/}
			{/*))}*/}
			<Background>
				<BrukerModal />
			</Background>
		</React.Fragment>
	)
}
